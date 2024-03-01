package com.github.leapbound.yc.camunda.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yamath
 * @since 2023/11/16 15:01
 */
@Data
@NoArgsConstructor
public class ProcessStep {

    /**
     * process instance id
     */
    private String processInstanceId;
    /**
     * current task id
     */
    private String currentTaskId;
    /**
     * current step
     */
    private String currentStep;
    /**
     * current task require variables
     */
    private TaskReturn task;

    public ProcessStep(Builder builder) {
        setProcessInstanceId(builder.processInstanceId);
        setCurrentTaskId(builder.currentTaskId);
        setCurrentStep(builder.currentStep);
        setTask(builder.task);
    }

    public static class Builder {
        private String processInstanceId;
        private String currentTaskId;
        private String currentStep;
        private TaskReturn task;

        public Builder() {
        }

        public Builder processInstanceId(String processInstanceId) {
            this.processInstanceId = processInstanceId;
            return this;
        }

        public Builder currentTaskId(String currentTaskId) {
            this.currentTaskId = currentTaskId;
            return this;
        }

        public Builder currentStep(String currentStep) {
            this.currentStep = currentStep;
            return this;
        }

        public Builder task(TaskReturn task) {
            this.task = task;
            return this;
        }

        public ProcessStep build() {
            return new ProcessStep(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
