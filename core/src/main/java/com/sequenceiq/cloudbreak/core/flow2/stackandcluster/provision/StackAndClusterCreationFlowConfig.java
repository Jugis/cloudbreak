package com.sequenceiq.cloudbreak.core.flow2.stackandcluster.provision;

import java.util.EnumSet;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.cloud.event.Payload;
import com.sequenceiq.cloudbreak.core.flow.FlowPhases;
import com.sequenceiq.cloudbreak.core.flow2.ChainFlow;
import com.sequenceiq.cloudbreak.core.flow2.Flow;
import com.sequenceiq.cloudbreak.core.flow2.stack.provision.StackCreationEvent;
import com.sequenceiq.cloudbreak.core.flow2.stack.provision.StackCreationFlowConfig;
import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.reactor.api.event.StackEvent;
import com.sequenceiq.cloudbreak.service.stack.StackService;

import reactor.bus.Event;

@Component
public class StackAndClusterCreationFlowConfig extends StackCreationFlowConfig {
    private static final EnumSet<StackCreationEvent> OWNEVENTS = EnumSet.of(StackCreationEvent.START_STACKANDCLUSTER_CREATION_EVENT);

    @Inject
    private StackService stackService;

    @Override
    public Flow createFlow(String flowId) {
        return new ChainFlow(super.createFlow(flowId)) {
            @Override
            public String nextSelector() {
                return FlowPhases.RUN_CLUSTER_CONTAINERS.name();
            }

            @Override
            public Object nextPayload(Event<? extends Payload> event) {
                Long stackId = event.getData().getStackId();
                Stack stack = stackService.getById(stackId);
                return new StackEvent(stack.getId());
            }
        };
    }

    @Override
    public StackCreationEvent[] getEvents() {
        return OWNEVENTS.toArray(new StackCreationEvent[]{});
    }

    @Override
    public StackCreationEvent[] getInitEvents() {
        return new StackCreationEvent[] {
                StackCreationEvent.START_STACKANDCLUSTER_CREATION_EVENT
        };
    }
}
