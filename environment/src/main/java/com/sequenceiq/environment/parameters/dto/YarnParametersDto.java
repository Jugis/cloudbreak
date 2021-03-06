package com.sequenceiq.environment.parameters.dto;

public class YarnParametersDto {

    private YarnParametersDto(Builder builder) {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Builder() {
        }

        public YarnParametersDto build() {
            return new YarnParametersDto(this);
        }
    }
}
