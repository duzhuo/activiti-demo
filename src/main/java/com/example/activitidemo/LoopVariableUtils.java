package com.example.activitidemo;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;

public class LoopVariableUtils {

    public static Integer getLoopVariable(ExecutionEntity execution, String variableName) {
        Object value = execution.getVariable(variableName);

        if (value == null && execution.getParent() != null) {
            value = execution.getParent().getVariableLocal(variableName);
        }

        return (Integer)(value != null ? value : 0);
    }

    public static void setLoopVariable(ExecutionEntity execution, String variableName, Object value) {
        execution.getParent().setVariableLocal(variableName, value);
    }
}
