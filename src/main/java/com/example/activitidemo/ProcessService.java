package com.example.activitidemo;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProcessService {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;

    @Transactional
    public void startProcess(String processDefinitionKey,
                             String beginRoleParamKey,
                             String beginAssignee,
                             String nextStepRoleParamKey,
                             String nextAssignee) {

        Map<String, Object> vars = new HashMap<>();
        vars.put(beginRoleParamKey, beginAssignee);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, vars);
        System.out.println(processInstance.getId());

        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskAssignee(beginAssignee).singleResult();

        Map<String, Object> nextVars = new HashMap<>();
        nextVars.put(nextStepRoleParamKey, nextAssignee);

        // 审批完成
        taskService.complete(task.getId(), nextVars);
    }

    public void completeTask(String processInstanceId,
                             String currentAssignee,
                             String nextRoleParamKey,
                             String nextAssignee) {

        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskAssignee(currentAssignee).singleResult();

        if (nextRoleParamKey != null && !"".equals(nextRoleParamKey)) {
            Map<String, Object> nextVars = new HashMap<>();
            nextVars.put(nextRoleParamKey, nextAssignee);
            taskService.complete(task.getId(), nextVars);
        } else {
            taskService.complete(task.getId());
        }

    }

    public void completeTask(String processInstanceId, String currentAssignee) {

        completeTask(processInstanceId, currentAssignee, null, null);
    }

}
