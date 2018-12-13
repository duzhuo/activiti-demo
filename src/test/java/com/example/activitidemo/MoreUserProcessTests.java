package com.example.activitidemo;

import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
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
 * moreUserProcess流程
 * 多人流程
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MoreUserProcessTests {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private HistoryService historyService;
    @Resource
    private ProcessService processService;
    @Resource
    private ProcessEngine processEngine;

    private String processInstanceId = "185001";

    @Test
    public void startProcess() {
        // 启动流程并指定到人
        Map<String, Object> vars = new HashMap<>();

        List<String> assigneeList = new ArrayList<>();
        assigneeList.add("a");
        assigneeList.add("b");

        vars.put("userList", assigneeList);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("moreUserProcess", vars);
        System.out.println("processInstance : " + processInstance.getId());
    }


}