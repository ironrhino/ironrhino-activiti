package com.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.demo.model.Leave;

@Component
public class LeaveServiceImpl implements LeaveService {

	@Autowired
	private LeaveManager leaveManager;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private ManagementService managementService;

	@Autowired
	private RepositoryService repositoryService;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Leave> findTask(String userid, String processDefinitionKey) {
		List<Task> tasks = new ArrayList<Task>();
		List<Leave> leaves = new ArrayList<Leave>();
		List<Task> taskAssignees = taskService.createTaskQuery()
				.processDefinitionKey(processDefinitionKey)
				.taskAssignee(userid).orderByTaskPriority().desc()
				.orderByTaskCreateTime().desc().list();

		List<Task> taskCandidates = taskService.createTaskQuery()
				.processDefinitionKey(processDefinitionKey)
				.taskCandidateUser(userid).orderByTaskPriority().desc()
				.orderByTaskCreateTime().desc().list();
		tasks.addAll(taskAssignees);
		tasks.addAll(taskCandidates);
		for (Task task : tasks) {
			String processInstanceId = task.getProcessInstanceId();
			ProcessInstance processInstance = runtimeService
					.createProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult();
			String businessKey = processInstance.getBusinessKey();
			Leave leave = leaveManager.get(businessKey);
			leave.setProcessInstance(processInstance);
			leave.setTask(task);
			leaves.add(leave);
		}
		return leaves;
	}

	@Transactional(readOnly = true)
	public List<Leave> findRunningProcessInstaces(String processDefinitionKey) {
		List<Leave> leaves = new ArrayList<Leave>();

		List<ProcessInstance> processInstances = runtimeService
				.createProcessInstanceQuery()
				.processDefinitionKey(processDefinitionKey).list();

		// 关联业务实体
		for (ProcessInstance processInstance : processInstances) {

			String businessKey = processInstance.getBusinessKey();

			Leave leave = leaveManager.get(businessKey);

			leave.setProcessInstanceId(processInstance.getId());

			// List<Task> tasks = taskService.createTaskQuery()
			// .processInstanceId(processInstance.getId())
			// .orderByTaskCreateTime().desc().listPage(0, 1);
			// TODO leave.setTask(tasks.get(0));

			leaves.add(leave);
		}

		return leaves;
	}

	/**
	 * 查询已结束的流程实例
	 * 
	 * @param processDefinitionKey
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<Leave> findFinishedProcessInstaces(String processDefinitionKey) {

		List<Leave> leaves = new ArrayList<Leave>();

		// 根据流程定义的key查询已经结束的流程实例(HistoricProcessInstance)
		List<HistoricProcessInstance> list = historyService
				.createHistoricProcessInstanceQuery().finished()
				.processDefinitionKey(processDefinitionKey).list();

		// 关联业务实体
		for (HistoricProcessInstance historicProcessInstance : list) {

			String businessKey = historicProcessInstance.getBusinessKey();

			Leave leave = leaveManager.get(businessKey);

			// leave.setHistoricProcessInstance(historicProcessInstance);
			// leave.setProcessDefinition(getProcessDefinition(historicProcessInstance
			// .getProcessDefinitionId()));

			leaves.add(leave);
		}

		return leaves;
	}

	/**
	 * 根据流程定义Id查询流程定义
	 */
	public ProcessDefinition getProcessDefinition(String processDefinitionId) {
		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();
		return processDefinition;
	}

}
