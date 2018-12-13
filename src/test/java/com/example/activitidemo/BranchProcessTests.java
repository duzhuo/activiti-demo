package com.example.activitidemo;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * branchProcess流程
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BranchProcessTests {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private HistoryService historyService;

    private String processInstanceId = "127501";

    @Test
    public void queryTasksByProcessId() {
//        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("85001").singleResult();

//        List<Task> taskList = taskService.createTaskQuery().processInstanceId("85001").list();

        // 查询角色下的任务
        List<Task> taskList = taskService.createTaskQuery().taskCandidateGroup("duzhuo").list();

        System.out.println("1");
    }


    // ${creator}
    // branchProcess
    // ${manager}
    // ${zongjian}
    // ${caiwu}

    @Test
    public void startProcess() {
        // 启动流程并指定到人
        Map<String, Object> vars = new HashMap<>();
        vars.put("creator", "duzhuo");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("branchProcess", vars);
        System.out.println("processInstance : " + processInstance.getId());
    }

    @Test
    public void commit2Manager() {

        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee("duzhuo").singleResult();
        Map<String, Object> vars = new HashMap<>();
        vars.put("money", 8000);
        vars.put("manager", "wuwei");
        taskService.complete(task.getId(), vars);
    }

    @Test
    public void commit2Zongjian() {

        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee("duzhuo").singleResult();
        Map<String, Object> vars = new HashMap<>();
        vars.put("money", 18000);
        vars.put("zongjian", "duyanbin");
        taskService.complete(task.getId(), vars);
    }

    @Test
    public void showProcessFlow() {

        BpmnModel bpmnModel = repositoryService.getBpmnModel("branchProcess:1:42570");

        DefaultProcessDiagramGenerator defaultProcessDiagramGenerator = new DefaultProcessDiagramGenerator();
        InputStream inputStream;

        if (runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult() == null) {

            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();

            String endId = historicProcessInstance.getEndActivityId();

            inputStream = defaultProcessDiagramGenerator.generateDiagram(bpmnModel, "png", Collections.singletonList(endId));

        } else {
            List<String> currentActivityinstances = runtimeService.getActiveActivityIds(processInstanceId);

            List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
            List<String> completedActivityInstances = historicActivityInstances.stream()
                    .map(HistoricActivityInstance::getActivityId).collect(Collectors.toList());
            List<String> completedFlows = gatherCompletedFlows(completedActivityInstances, currentActivityinstances, bpmnModel);

            inputStream = defaultProcessDiagramGenerator.generateDiagram(bpmnModel, "png", currentActivityinstances, completedFlows);
        }

        try {
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);

            File targetFile = new File("src/main/resources/process.png");
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void managerTaskAgree() {

        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee("wuwei").singleResult();

        Map<String, Object> vars = runtimeService.getVariables(processInstanceId);
        vars.forEach((key, value) -> System.out.println("key : " + key + "  | value : " + value));

        String [] candidateUsers={"a","b","c"};

        Map<String, Object> vars2 = new HashMap<>();
        vars2.put("caiwu", Arrays.asList(candidateUsers));
        vars2.put("isPass", true);

        // 经理审批完成
        taskService.complete(task.getId(), vars2);
    }

    @Test
    public void managerTaskNotAgree() {

        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee("wuwei").singleResult();

        Map<String, Object> vars = runtimeService.getVariables(processInstanceId);
        vars.forEach((key, value) -> System.out.println("key : " + key + "  | value : " + value));

        String [] candidateUsers={"a","b","c"};

        Map<String, Object> vars2 = new HashMap<>();
        vars2.put("caiwu", Arrays.asList(candidateUsers));
        vars2.put("isPass", false);

        // 经理审批完成
        taskService.complete(task.getId(), vars2);
    }

    @Test
    public void zongjianTaskAgree() {

        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).taskAssignee("duyanbin").singleResult();

        Map<String, Object> vars = runtimeService.getVariables(processInstanceId);
        vars.forEach((key, value) -> System.out.println("key : " + key + "  | value : " + value));

        String [] candidateUsers={"a","b","c"};

        Map<String, Object> vars2 = new HashMap<>();
        vars2.put("caiwu", Arrays.asList(candidateUsers));

        // 经理审批完成
        taskService.complete(task.getId(), vars2);
    }

    @Test
    public void caiwuCompleteTask() {

        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();

        Map<String, Object> vars = runtimeService.getVariables(processInstanceId);
        vars.forEach((key, value) -> System.out.println("key : " + key + "  | value : " + value));

        // 财务a来审批
        taskService.claim(task.getId(), "a");
        // 财务a审批完成
        taskService.complete(task.getId());
    }

    protected List<String> gatherCompletedFlows(List<String> completedActivityInstances,
                                                List<String> currentActivityinstances, BpmnModel pojoModel) {

        List<String> completedFlows = new ArrayList<String>();
        List<String> activities = new ArrayList<String>(completedActivityInstances);
        activities.addAll(currentActivityinstances);

        // TODO: not a robust way of checking when parallel paths are active, should be revisited
        // Go over all activities and check if it's possible to match any outgoing paths against the activities
        for (FlowElement activity : pojoModel.getMainProcess().getFlowElements()) {
            if(activity instanceof FlowNode) {
                int index = activities.indexOf(activity.getId());
                if (index >= 0 && index + 1 < activities.size()) {
                    List<SequenceFlow> outgoingFlows = ((FlowNode) activity).getOutgoingFlows();
                    for (SequenceFlow flow : outgoingFlows) {
                        String destinationFlowId = flow.getTargetRef();
                        if (destinationFlowId.equals(activities.get(index + 1))) {
                            completedFlows.add(flow.getId());
                        }
                    }
                }
            }
        }
        return completedFlows;
    }
}
