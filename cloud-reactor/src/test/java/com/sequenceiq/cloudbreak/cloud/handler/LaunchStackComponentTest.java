package com.sequenceiq.cloudbreak.cloud.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.sequenceiq.cloudbreak.cloud.event.CloudPlatformRequest;
import com.sequenceiq.cloudbreak.cloud.event.resource.LaunchStackRequest;
import com.sequenceiq.cloudbreak.cloud.event.resource.LaunchStackResult;
import com.sequenceiq.cloudbreak.cloud.model.CloudResourceStatus;
import com.sequenceiq.cloudbreak.cloud.model.ResourceStatus;
import com.sequenceiq.common.api.type.AdjustmentType;

public class LaunchStackComponentTest extends AbstractComponentTest<LaunchStackResult> {

    @Test
    public void testLaunchStack() {
        LaunchStackResult lsr = sendCloudRequest();
        List<CloudResourceStatus> r = lsr.getResults();

        assertEquals(ResourceStatus.CREATED, r.get(0).getStatus());
        assertNull(lsr.getErrorDetails());
    }

    @Override
    protected String getTopicName() {
        return "LAUNCHSTACKREQUEST";
    }

    @Override
    protected CloudPlatformRequest<LaunchStackResult> getRequest() {
        return new LaunchStackRequest(g().createCloudContext(), g().createCloudCredential(), g().createCloudStack(), AdjustmentType.BEST_EFFORT, 0L);
    }
}
