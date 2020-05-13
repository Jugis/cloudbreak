package com.sequenceiq.environment.environment.experience.liftie;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sequenceiq.environment.environment.dto.EnvironmentExperienceDto;
import com.sequenceiq.environment.environment.experience.Experience;
import com.sequenceiq.environment.environment.experience.ExperienceSource;
import com.sequenceiq.environment.environment.experience.liftie.responses.ClusterView;
import com.sequenceiq.environment.environment.experience.liftie.responses.ListClustersResponse;

@Component
public class ExperiencesByLiftie implements Experience {

    private final LiftieApi liftieApi;

    public ExperiencesByLiftie(LiftieApi liftieApi) {
        this.liftieApi = liftieApi;
    }

    @Override
    public boolean hasExistingClusterForEnvironment(EnvironmentExperienceDto environment) {
        //String tenant = "9d74eee4-1cad-45d7-b645-7ccf9edbb73d"; //todo: find a way to figure out the tenant of the given env
        //List<ClusterView> clusterViews = getClusterViews(environment.getName(), tenant);
        List<ClusterView> clusterViews = getClusterViews(environment.getName(), environment.getAccountId());
        return countNotDeletedClusters(clusterViews) > 0;
    }

    @Override
    public void deleteConnectedExperiences(EnvironmentExperienceDto dto) {

    }

    @Override
    public ExperienceSource getSource() {
        return ExperienceSource.LIFTIE;
    }

    private List<ClusterView> getClusterViews(String environmentName, String tenant) {
        List<ClusterView> clusterViews = new LinkedList<>();
        //ListClustersResponse first = liftieApi.listClusters(environmentName, tenant, "mon-platform", null);
        ListClustersResponse first = liftieApi.listClusters(environmentName, tenant, null, null);
        if (first.getPage().getTotalPages() > 1) {
            List<ListClustersResponse> clustersResponses = new LinkedList<>();
            clustersResponses.add(first);
            int currentPage = first.getPage().getNumber() + 1;
            while (currentPage < first.getPage().getTotalPages()) {
                clustersResponses.add(liftieApi.listClusters(environmentName, tenant, null, currentPage));
                currentPage++;
            }
            for (ListClustersResponse clustersResponse : clustersResponses) {
                clusterViews.addAll(clustersResponse.getClusters().values());
            }
        } else {
            clusterViews.addAll(first.getClusters().values());
        }
        return clusterViews;
    }

    private long countNotDeletedClusters(List<ClusterView> clusterViews) {
        return clusterViews.stream().filter(clusterView -> !"DELETED".equals(clusterView.getCluster_status().getStatus())).count();
    }

}
