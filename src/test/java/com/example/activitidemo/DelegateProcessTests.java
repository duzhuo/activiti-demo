package com.example.activitidemo;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * delegateProcess流程
 * 委派流程
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DelegateProcessTests {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private HistoryService historyService;

    private String processInstanceId = "180001";

    @Test
    public void startProcess() {
        // 启动流程并指定到人
        Map<String, Object> vars = new HashMap<>();
        vars.put("userId", "duzhuo");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("delegateProcess", vars);
        System.out.println("processInstance : " + processInstance.getId());
    }

    @Test
    public void delegateTask() {

        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee("duzhuo").singleResult();

        // 审批完成
        taskService.delegateTask(task.getId(), "jiayuan");
    }

    @Test
    public void jiayuanCompleteTask() {

        Task task = taskService.createTaskQuery()
                .taskDelegationState(DelegationState.PENDING)
                .taskAssignee("jiayuan").singleResult();

        // 审批完成
        taskService.resolveTask(task.getId());
    }

    @Test
    public void duzhuoCompleteTask() {

        Task task = taskService.createTaskQuery()
                .taskDelegationState(DelegationState.RESOLVED)
                .taskAssignee("duzhuo").singleResult();

        // 审批完成
        taskService.complete(task.getId());
    }

    // ${creator}
    //${approver}

}
