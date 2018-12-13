package com.example.activitidemo;

import org.activiti.engine.*;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;

/**
 * addSignProcess流程
 * 加签流程
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AddSignProcessTests {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private ProcessEngine processEngine;
    @Resource
    private TaskService taskService;

    private String processInstanceId = "300001";

    private String assignee = "2";


    @Test
    public void startProcess() {

        List<String> userList = new ArrayList<>();
        userList.add("duzhuo");

        // 启动流程并指定到人
        Map<String, Object> vars = new HashMap<>();
        vars.put("userList", userList);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("addSignProcess", vars);
        System.out.println("processInstanceId : " + processInstance.getId());
    }




    @Test
    public void addSign() {
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee("duzhuo").singleResult();
        System.out.println("executionId : " + task.getExecutionId());
        processEngine.getManagementService().executeCommand(new AddSignCmd(task.getExecutionId(),assignee));
    }

    @Test
    public void aCompleteTask() {
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee(assignee).singleResult();
        // 经理审批完成
        taskService.complete(task.getId());
    }
}