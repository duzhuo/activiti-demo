package com.example.activitidemo;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
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
 * allSignProcess流程
 * 会签流程
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AllSignProcessTests {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private HistoryService historyService;

    private String processInstanceId = "142501";

    @Test
    public void startProcess() {
        // 启动流程并指定到人
        Map<String, Object> vars = new HashMap<>();

        List<String> assigneeList = new ArrayList<>();
        assigneeList.add("wangba");
        assigneeList.add("wangjiu");

        vars.put("leaderList", assigneeList);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("allSignProcess", vars);
        System.out.println("processInstance : " + processInstance.getId());
    }

    @Test
    public void wangbaTaskAgree() {

        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee("wangba").singleResult();
        // 审批完成
        taskService.complete(task.getId());
    }

    @Test
    public void wangjiuTaskAgree() {

        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee("wangjiu").singleResult();
        // 审批完成
        taskService.complete(task.getId());
    }

}
