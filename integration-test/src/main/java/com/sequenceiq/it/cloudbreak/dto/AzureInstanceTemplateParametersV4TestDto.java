package com.sequenceiq.it.cloudbreak.dto;

import com.sequenceiq.cloudbreak.api.endpoint.v4.stacks.base.parameter.template.AzureInstanceTemplateV4Parameters;
import com.sequenceiq.it.cloudbreak.Prototype;
import com.sequenceiq.it.cloudbreak.context.TestContext;

@Prototype
public class AzureInstanceTemplateParametersV4TestDto extends AbstractCloudbreakTestDto<AzureInstanceTemplateV4Parameters, AzureInstanceTemplateV4Parameters,
        AzureInstanceTemplateParametersV4TestDto> {

    protected AzureInstanceTemplateParametersV4TestDto(TestContext testContext) {
        super(new AzureInstanceTemplateV4Parameters(), testContext);
    }
}
