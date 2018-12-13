package com.example.activitidemo;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.IdGenerator;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;

import java.util.Date;
import java.util.List;

public class AddSignCmd implements Command<Void> {

    private String executionId;

    private String assignee;

    public AddSignCmd(String executionId, String assignee) {
        this.executionId = executionId;
        this.assignee = assignee;
    }

    @Override
    public Void execute(CommandContext commandContext) {

        ProcessEngineConfigurationImpl pec = commandContext.getProcessEngineConfiguration();
        RuntimeService runtimeService = pec.getRuntimeService();
        TaskService taskService = pec.getTaskService();
        TaskEntityManager taskEntityManager = pec.getTaskEntityManager();
        IdGenerator idGenerator = pec.getIdGenerator();
        ExecutionEntityManager executionEntityManager = pec.getExecutionEntityManager();

        Execution execution = runtimeService.createExecutionQuery().executionId(executionId).singleResult();
        ExecutionEntity ee = (ExecutionEntity) execution;

        ExecutionEntity parent = ee.getParent();

        Task newTask = taskService.createTaskQuery().executionId(executionId).singleResult();
        TaskEntity t = (TaskEntity) newTask;

        ExecutionEntity newExecution = executionEntityManager.createChildExecution(parent);
        newExecution.setActive(true);
        newExecution.setConcurrent(false);
        newExecution.setScope(false);
        newExecution.setCurrentFlowElement(ee.getCurrentFlowElement());

        TaskEntity taskEntity = taskEntityManager.create();
        taskEntity.setCreateTime(new Date());
        taskEntity.setTaskDefinitionKey(t.getTaskDefinitionKey());
        taskEntity.setProcessDefinitionId(t.getProcessDefinitionId());
        taskEntity.setProcessInstanceId(t.getProcessInstanceId());
        taskEntity.setExecutionId(newExecution.getId());
        taskEntity.setName(newTask.getName());
        taskEntity.setExecution(newExecution);
        taskEntity.setAssignee(assignee);
        taskEntity.setId(idGenerator.getNextId());
        taskEntity.setRevision(0);

        taskEntityManager.insert(taskEntity, newExecution);

        Integer nrOfInstances = LoopVariableUtils.getLoopVariable(newExecution, "nrOfInstances");
        LoopVariableUtils.setLoopVariable(newExecution, "nrOfInstances", (nrOfInstances + 1));

        Integer nrOfActivelnstances = LoopVariableUtils.getLoopVariable(newExecution, "nrOfActiveInstances");
        LoopVariableUtils.setLoopVariable(newExecution, "nrOfActiveInstances", (nrOfActivelnstances + 1));

        List<String> userList = (List<String>)newExecution.getVariable("userList");
        userList.add(assignee);

        newExecution.setVariable("userList", userList);
        return null;
    }
}
