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
import java.util.HashMap;
import java.util.Map;

/**
 * modifyProcess流程
 * 旧流程运行一半修改流程
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ModifyProcessTests {

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

    private String processInstanceId = "155001";

    @Test
    public void startProcess() {
        // 启动流程并指定到人
        Map<String, Object> vars = new HashMap<>();
        vars.put("creator", "duzhuo");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("modifyProcess", vars);
        System.out.println("processInstance : " + processInstance.getId());
    }


    @Test
    public void duzhuoCompleteTask() {

        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskAssignee("duzhuo").singleResult();

        Map<String, Object> vars = new HashMap<>();
        vars.put("approver", "wuwei");

        // 审批完成
        taskService.complete(task.getId(), vars);
    }

    // ${finalApprover}
    @Test
    public void wuweiCompleteTask() {

        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskAssignee("wuwei").singleResult();

        // 审批完成
        taskService.complete(task.getId());
    }


    @Test
    public void startAndCompleteProcess() {
        processService.startProcess("modifyProcess",
                "creator", "duzhuo",
                "approver", "wuwei");
    }

    @Test
    public void completeTask() {

        processService.completeTask("167501",
                "wuwei", "finalApprover", "duyanbin");
    }

    @Test
    public void finalCompleteTask() {
        processService.completeTask("167501", "duyanbin");
    }
}