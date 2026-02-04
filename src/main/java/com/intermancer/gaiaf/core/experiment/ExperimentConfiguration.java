package com.intermancer.gaiaf.core.experiment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "experiment")
public class ExperimentConfiguration {
    private int cycleCount = 1500;
    private int repoCapacity = 200;
    private boolean pausable = false;
    private int pauseCycles = 250;

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

    public boolean isPausable() {
        return pausable;
    }

    public void setPausable(boolean pausable) {
        this.pausable = pausable;
    }

    public int getPauseCycles() {
        return pauseCycles;
    }

    public void setPauseCycles(int pauseCycles) {
        this.pauseCycles = pauseCycles;
    }
}