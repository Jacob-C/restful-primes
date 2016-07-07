package com.example.prime.application;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

public class PrimesConfiguration extends Configuration {

    @Range(min=2)
    private int defaultBounds;
    
    @NotEmpty
    private String defaultAlgorithm;
    
    @JsonProperty
    public int getDefaultBounds() {
        return defaultBounds;
    }

    @JsonProperty
    public void setDefaultBounds(int defaultBounds) {
        this.defaultBounds = defaultBounds;
    }

    @JsonProperty
    public String getDefaultAlgorithm() {
        return defaultAlgorithm;
    }

    @JsonProperty
    public void setDefaultAlgorithm(String defaultAlgorithm) {
        this.defaultAlgorithm = defaultAlgorithm;
    }
}
