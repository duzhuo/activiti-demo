package com.example.activitidemo;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/")
public class Controller {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private HistoryService historyService;

    @RequestMapping(value = "/startProcess/{key}", method = {RequestMethod.GET})
    public String startProcessInstanceByKey(@PathVariable String key) {

//        Map<String, Object> vars = new HashMap<>();
//        vars.put("xuQiuDepRequestAssignee", "duzhuo");
//
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("fyljt_zgsSP2", vars);
//
//        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskAssignee("duzhuo").singleResult();


//        Map<String, Object> vars1 = new HashMap<>();
//        vars1.put("userAssignee", "duzhuo");
//        ProcessInstance processInstance1 = runtimeService.startProcessInstanceByKey("userAndGroupInUserTask", vars1);

//        Task task1 = taskService.createTaskQuery().processInstanceId(processInstance1.getId()).taskAssignee("duzhuo").singleResult();

        // noUserFlow
        Map<String, Object> vars2 = new HashMap<>();
        vars2.put("user", "duzhuo");
        ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey(key, vars2);

        return "ok";
    }

    @RequestMapping(value = "/completeTask/{taskId}", method = {RequestMethod.GET})
    public String completeTask(@PathVariable String taskId) {

        Map<String, Object> vars2 = new HashMap<>();
        vars2.put("approveUser", "wuwei");
        taskService.complete(taskId, vars2);

        return "ok";
    }

//    @RequestMapping(value = "/showProcess/{processId}", method = {RequestMethod.GET})
//    public String showProcess(@PathVariable String processId) {
//
//        BpmnModel bpmnModel = repositoryService.getBpmnModel("ddd:3:42541");
//
//        DefaultProcessDiagramGenerator defaultProcessDiagramGenerator = new DefaultProcessDiagramGenerator();
//        InputStream inputStream;
//
//        if (runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult() == null) {
//
//            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
//                    .processInstanceId(processId)
//                    .singleResult();
//
//            String endId = historicProcessInstance.getEndActivityId();
//
//            inputStream = defaultProcessDiagramGenerator.generateDiagram(bpmnModel, "png", Collections.singletonList(endId));
//
//        } else {
//            List<String> activeList = runtimeService.getActiveActivityIds(processId);
//            inputStream = defaultProcessDiagramGenerator.generateDiagram(bpmnModel, "png", activeList);
//        }
//
//        try {
//            byte[] buffer = new byte[inputStream.available()];
//            inputStream.read(buffer);
//
//            File targetFile = new File("src/main/resources/process.png");
//            OutputStream outStream = new FileOutputStream(targetFile);
//            outStream.write(buffer);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        return "ok";
//    }

    @RequestMapping(value = "/startProcessRole/{key}", method = {RequestMethod.GET})
    public String startProcessRole(@PathVariable String key) {

        // create_bill_role
        Map<String, Object> vars2 = new HashMap<>();

        String [] candidateUsers={"a","b","c"};
        vars2.put("create_bill_role", Arrays.asList(candidateUsers));

        ProcessInstance processInstance2 = runtimeService.startProcessInstanceByKey(key, vars2);

        return processInstance2.getId();
    }


}
