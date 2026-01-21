package com.intermancer.gaiaf.core.experiment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "experiment")
public class ExperimentConfiguration {
    private int cycleCount = 1500;
    private int repoCapacity = 200;

    public int getCycleCount() {
        return cycleCount;
    }

    public void setCycleCount(int cycleCount) {
        this.cycleCount = cycleCount;
    }

    public int getRepoCapacity() {
        return repoCapacity;
    }

    public void setRepoCapacity(int repoCapacity) {
        this.repoCapacity = repoCapacity;
    }
}