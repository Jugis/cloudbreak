package com.sequenceiq.it.cloudbreak.cloud.v4.yarn;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.network.YarnNetworkV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.stack.YarnStackV4Parameters;
import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.template.YarnInstanceTemplateV4Parameters;
import com.sequenceiq.cloudbreak.common.mappable.CloudPlatform;
import com.sequenceiq.distrox.api.v1.distrox.model.instancegroup.template.YarnInstanceTemplateV1Parameters;
import com.sequenceiq.environment.api.v1.credential.model.parameters.yarn.YarnParameters;
import com.sequenceiq.environment.api.v1.environment.model.EnvironmentNetworkYarnParams;
import com.sequenceiq.it.cloudbreak.cloud.v4.AbstractCloudProvider;
import com.sequenceiq.it.cloudbreak.dto.ClusterTestDto;
import com.sequenceiq.it.cloudbreak.dto.ImageSettingsTestDto;
import com.sequenceiq.it.cloudbreak.dto.InstanceTemplateV4TestDto;
import com.sequenceiq.it.cloudbreak.dto.NetworkV4TestDto;
import com.sequenceiq.it.cloudbreak.dto.PlacementSettingsTestDto;
import com.sequenceiq.it.cloudbreak.dto.StackAuthenticationTestDto;
import com.sequenceiq.it.cloudbreak.dto.VolumeV4TestDto;
import com.sequenceiq.it.cloudbreak.dto.credential.CredentialTestDto;
import com.sequenceiq.it.cloudbreak.dto.distrox.DistroXTestDtoBase;
import com.sequenceiq.it.cloudbreak.dto.distrox.cluster.DistroXClusterTestDto;
import com.sequenceiq.it.cloudbreak.dto.distrox.instancegroup.DistroXInstanceTemplateTestDto;
import com.sequenceiq.it.cloudbreak.dto.distrox.instancegroup.DistroXNetworkTestDto;
import com.sequenceiq.it.cloudbreak.dto.distrox.instancegroup.DistroXVolumeTestDto;
import com.sequenceiq.it.cloudbreak.dto.environment.EnvironmentNetworkTestDto;
import com.sequenceiq.it.cloudbreak.dto.environment.EnvironmentTestDto;
import com.sequenceiq.it.cloudbreak.dto.imagecatalog.ImageCatalogTestDto;
import com.sequenceiq.it.cloudbreak.dto.stack.StackTestDtoBase;

@Component
public class YarnCloudProvider extends AbstractCloudProvider {

    @Inject
    private YarnProperties yarnProperties;

    @Override
    public CloudPlatform getCloudPlatform() {
        return CloudPlatform.YARN;
    }

    @Override
    public CredentialTestDto credential(CredentialTestDto credential) {
        return credential
                .withDescription(commonCloudProperties().getDefaultCredentialDescription())
                .withCloudPlatform(CloudPlatform.YARN.name())
                .withYarnParameters(yarnCredentialParameters());
    }

    @Override
    public EnvironmentTestDto environment(EnvironmentTestDto environment) {
        return environment
                .withLocation(location());
    }

    @Override
    public PlacementSettingsTestDto placement(PlacementSettingsTestDto placement) {
        return placement.withRegion(region());
    }

    @Override
    public String region() {
        return yarnProperties.getRegion();
    }

    public String location() {
        return yarnProperties.getLocation();
    }

    @Override
    public InstanceTemplateV4TestDto template(InstanceTemplateV4TestDto template) {
        return template.withYarn(instanceParameters());
    }

    @Override
    public DistroXInstanceTemplateTestDto template(DistroXInstanceTemplateTestDto template) {
        return template.withYarn(distroxInstanceParameters());
    }

    @Override
    public StackTestDtoBase stack(StackTestDtoBase stack) {
        return stack.withYarn(stackParameters());
    }

    @Override
    public DistroXTestDtoBase distrox(DistroXTestDtoBase distrox) {
        return distrox;
    }

    @Override
    protected ClusterTestDto withCluster(ClusterTestDto cluster) {
        return cluster
                .withValidateBlueprint(Boolean.FALSE)
                .withBlueprintName(getBlueprintName());
    }

    @Override
    protected DistroXClusterTestDto withCluster(DistroXClusterTestDto cluster) {
        return cluster.withBlueprintName(getBlueprintName());
    }

    @Override
    public YarnStackV4Parameters stackParameters() {
        YarnStackV4Parameters yarnStackV4Parameters = new YarnStackV4Parameters();
        yarnStackV4Parameters.setYarnQueue(getQueue());
        return yarnStackV4Parameters;
    }

    @Override
    public VolumeV4TestDto attachedVolume(VolumeV4TestDto volume) {
        return volume.withSize(yarnProperties.getInstance().getVolumeSize())
                .withCount(yarnProperties.getInstance().getVolumeCount());
    }

    @Override
    public DistroXVolumeTestDto attachedVolume(DistroXVolumeTestDto volume) {
        return volume.withSize(yarnProperties.getInstance().getVolumeSize())
                .withCount(yarnProperties.getInstance().getVolumeCount());
    }

    @Override
    public NetworkV4TestDto network(NetworkV4TestDto network) {
        return network.withYarn(networkParameters()).withSubnetCIDR(getSubnetCIDR());
    }

    @Override
    public DistroXNetworkTestDto network(DistroXNetworkTestDto network) {
        return network;
    }

    @Override
    public ImageCatalogTestDto imageCatalog(ImageCatalogTestDto imageCatalog) {
        imageCatalog.withUrl(yarnProperties.getImageCatalogUrl());
        return imageCatalog;
    }

    @Override
    public ImageSettingsTestDto imageSettings(ImageSettingsTestDto imageSettings) {
        return imageSettings.withImageId(yarnProperties.getImageId())
                .withImageCatalog(imageSettings.getTestContext().given(ImageSettingsTestDto.class).getName());
    }

    @Override
    public String availabilityZone() {
        return yarnProperties.getAvailabilityZone();
    }

    @Override
    public StackAuthenticationTestDto stackAuthentication(StackAuthenticationTestDto stackAuthenticationEntity) {
        String sshPublicKey = commonCloudProperties().getSshPublicKey();
        return stackAuthenticationEntity.withPublicKey(sshPublicKey);
    }

    @Override
    public EnvironmentNetworkTestDto environmentNetwork(EnvironmentNetworkTestDto environmentNetwork) {
        return environmentNetwork.withNetworkCIDR(getSubnetCIDR())
                .withYarn(environmentNetworkParameters());
    }

    private EnvironmentNetworkYarnParams environmentNetworkParameters() {
        EnvironmentNetworkYarnParams environmentNetworkYarnParams = new EnvironmentNetworkYarnParams();
        environmentNetworkYarnParams.setQueue(getQueue());
        return environmentNetworkYarnParams;
    }

    @Override
    public String getBlueprintName() {
        return yarnProperties.getDefaultBlueprintName();
    }

    public String getQueue() {
        return yarnProperties.getQueue();
    }

    public Integer getCPUCount() {
        return yarnProperties.getInstance().getCpuCount();
    }

    public Integer getMemorySize() {
        return yarnProperties.getInstance().getMemory();
    }

    public YarnParameters yarnCredentialParameters() {
        YarnParameters yarnCredentialV4Parameters = new YarnParameters();
        yarnCredentialV4Parameters.setEndpoint(yarnProperties.getCredential().getEndpoint());
        return yarnCredentialV4Parameters;
    }

    private YarnInstanceTemplateV4Parameters instanceParameters() {
        YarnInstanceTemplateV4Parameters yarnInstanceTemplateV4Parameters = new YarnInstanceTemplateV4Parameters();
        yarnInstanceTemplateV4Parameters.setCpus(getCPUCount());
        yarnInstanceTemplateV4Parameters.setMemory(getMemorySize());
        return yarnInstanceTemplateV4Parameters;
    }

    private YarnNetworkV4Parameters networkParameters() {
        YarnNetworkV4Parameters yarnNetworkV4Parameters = new YarnNetworkV4Parameters();
        yarnNetworkV4Parameters.getCloudPlatform();
        return yarnNetworkV4Parameters;
    }

    private YarnInstanceTemplateV1Parameters distroxInstanceParameters() {
        YarnInstanceTemplateV1Parameters yarnInstanceTemplateV4Parameters = new YarnInstanceTemplateV1Parameters();
        yarnInstanceTemplateV4Parameters.setCpus(getCPUCount());
        yarnInstanceTemplateV4Parameters.setMemory(getMemorySize());
        return yarnInstanceTemplateV4Parameters;
    }
}