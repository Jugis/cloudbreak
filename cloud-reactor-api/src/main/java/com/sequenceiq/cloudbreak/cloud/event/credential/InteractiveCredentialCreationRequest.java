package com.sequenceiq.cloudbreak.cloud.event.credential;

import com.sequenceiq.cloudbreak.cloud.context.CloudContext;
import com.sequenceiq.cloudbreak.cloud.event.CloudPlatformRequest;
import com.sequenceiq.cloudbreak.cloud.model.ExtendedCloudCredential;

/**
 * Created by perdos on 9/23/16.
 */
public class InteractiveCredentialCreationRequest extends CloudPlatformRequest {

    private final ExtendedCloudCredential extendedCloudCredential;

    public InteractiveCredentialCreationRequest(CloudContext cloudContext, ExtendedCloudCredential extendedCloudCredential) {
        super(cloudContext, extendedCloudCredential);
        this.extendedCloudCredential = extendedCloudCredential;
    }

    public ExtendedCloudCredential getExtendedCloudCredential() {
        return extendedCloudCredential;
    }
}
