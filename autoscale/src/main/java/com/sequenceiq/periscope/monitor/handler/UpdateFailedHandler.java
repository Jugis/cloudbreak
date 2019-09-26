package com.sequenceiq.periscope.monitor.handler;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.model.FailureReport;
import com.sequenceiq.cloudbreak.api.model.stack.StackResponse;
import com.sequenceiq.cloudbreak.api.model.stack.instance.InstanceMetaDataJson;
import com.sequenceiq.periscope.api.model.ClusterState;
import com.sequenceiq.periscope.domain.Cluster;
import com.sequenceiq.periscope.domain.FailedNode;
import com.sequenceiq.periscope.log.MDCBuilder;
import com.sequenceiq.periscope.monitor.event.UpdateFailedEvent;
import com.sequenceiq.periscope.repository.FailedNodeRepository;
import com.sequenceiq.periscope.service.ClusterService;
import com.sequenceiq.periscope.utils.StackResponseUtils;

@Component
public class UpdateFailedHandler implements ApplicationListener<UpdateFailedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateFailedHandler.class);

    private static final String DELETE_STATUSES_PREFIX = "DELETE_";

    private static final String AVAILABLE = "AVAILABLE";

    private static final int RETRY_THRESHOLD = 5;

    @Inject
    private ClusterService clusterService;

    @Inject
    private StackResponseUtils stackResponseUtils;

    @Inject
    private CloudbreakCommunicator cloudbreakCommunicator;

    @Inject
    private FailedNodeRepository failedNodeRepository;

    private final Map<Long, Integer> updateFailures = new ConcurrentHashMap<>();

    @Override
    public void onApplicationEvent(UpdateFailedEvent event) {
        long autoscaleClusterId = event.getClusterId();
        LOGGER.info("Cluster {} failed", autoscaleClusterId);
        Cluster cluster = clusterService.findById(autoscaleClusterId);
        if (cluster == null) {
            return;
        }
        MDCBuilder.buildMdcContext(cluster);
        StackResponse stackResponse = getStackById(cluster.getStackId());
        if (stackResponse == null) {
            LOGGER.info("Suspending cluster {}", autoscaleClusterId);
            suspendCluster(cluster);
            return;
        }
        String stackStatus = getStackStatus(stackResponse);
        if (stackStatus.startsWith(DELETE_STATUSES_PREFIX)) {
            clusterService.removeById(autoscaleClusterId);
            LOGGER.info("Delete cluster {} due to failing update attempts and Cloudbreak stack status.", autoscaleClusterId);
            return;
        }
        Integer failed = updateFailures.get(autoscaleClusterId);
        if (failed == null) {
            LOGGER.info("New failed cluster id: [{}]", autoscaleClusterId);
            updateFailures.put(autoscaleClusterId, 1);
        } else if (RETRY_THRESHOLD - 1 == failed) {
            try {
                String clusterStatus = stackResponse.getCluster().getStatus().name();
                if (stackStatus.equals(AVAILABLE) && clusterStatus.equals(AVAILABLE)) {
                    // Ambari server is unreacheable but the stack and cluster statuses are "AVAILABLE"
                    reportAmbariServerFailure(cluster, stackResponse);
                    LOGGER.info("Suspend cluster monitoring for cluster {} due to failing update attempts and Cloudbreak stack status {}",
                            autoscaleClusterId, stackStatus);
                } else {
                    LOGGER.info("Suspend cluster monitoring for cluster {}", autoscaleClusterId);
                }
            } catch (Exception ex) {
                LOGGER.warn("Problem when verifying cluster status. Original message: {}",
                        ex.getMessage());
            }
            suspendCluster(cluster);
            updateFailures.remove(autoscaleClusterId);
        } else {
            int value = failed + 1;
            LOGGER.info("Increase failed count[{}] for cluster id: [{}]", value, autoscaleClusterId);
            updateFailures.put(autoscaleClusterId, value);
        }
    }

    private String getStackStatus(StackResponse stackResponse) {
        return stackResponse.getStatus() != null ? stackResponse.getStatus().name() : "";
    }

    private StackResponse getStackById(long cloudbreakStackId) {
        try {
            return cloudbreakCommunicator.getById(cloudbreakStackId);
        } catch (Exception e) {
            LOGGER.warn("Cluster status could not be verified by Cloudbreak. Original message: {}",
                    e.getMessage());
            return null;
        }
    }

    private void suspendCluster(Cluster cluster) {
        clusterService.setState(cluster, ClusterState.SUSPENDED);
    }

    private void reportAmbariServerFailure(Cluster cluster, StackResponse stackResponse) {
        Optional<InstanceMetaDataJson> pgw = stackResponseUtils.getNotTerminatedPrimaryGateways(stackResponse);
        if (pgw.isPresent()) {
            FailureReport failureReport = new FailureReport();
            failureReport.setFailedNodes(Collections.singletonList(pgw.get().getDiscoveryFQDN()));
            try {
                cloudbreakCommunicator.failureReport(cluster.getStackId(), failureReport);
                FailedNode failedNode = new FailedNode();
                failedNode.setClusterId(cluster.getId());
                failedNode.setName(pgw.get().getDiscoveryFQDN());
                failedNodeRepository.save(failedNode);
            } catch (Exception e) {
                LOGGER.warn("Exception during failure report. Original message: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
