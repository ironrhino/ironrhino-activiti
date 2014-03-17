package com.demo.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.ironrhino.core.service.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.demo.model.Leave;

@Component
public class LeaveServiceImpl implements LeaveService {

	@Resource
	private EntityManager<Leave> entityManager;

	@Resource(name = "identityService")
	private IdentityService identityService;

	@Resource(name = "runtimeService")
	private RuntimeService runtimeService;

	@Resource(name = "historyService")
	private HistoryService historyService;

	@Resource(name = "taskService")
	private TaskService taskService;

	@Resource(name = "managementService")
	private ManagementService managementService;

	@Resource(name = "formService")
	private FormService formService;

	@Resource(name = "repositoryService")
	private RepositoryService repositoryService;

	/**
	 * 根据用户Id查询待办任务列表
	 * 
	 * @param userid
	 *            用户id
	 * @param processDefinitionKey
	 *            流程定义的key
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Leave> findTask(String userid, String processDefinitionKey) {

		// 存放当前用户的所有任务
		List<Task> tasks = new ArrayList<Task>();

		List<Leave> leaves = new ArrayList<Leave>();

		// 根据当前用户的id查询代办任务列表(已经签收)
		List<Task> taskAssignees = taskService.createTaskQuery()
				.processDefinitionKey(processDefinitionKey)
				.taskAssignee(userid).orderByTaskPriority().desc()
				.orderByTaskCreateTime().desc().list();
		// 根据当前用户id查询未签收的任务列表
		List<Task> taskCandidates = taskService.createTaskQuery()
				.processDefinitionKey(processDefinitionKey)
				.taskCandidateUser(userid).orderByTaskPriority().desc()
				.orderByTaskCreateTime().desc().list();

		tasks.addAll(taskAssignees);// 添加已签收准备执行的任务(已经分配到任务的人)
		tasks.addAll(taskCandidates);// 添加还未签收的任务(任务的候选者)

		// 遍历所有的任务列表,关联实体
		for (Task task : tasks) {
			String processInstanceId = task.getProcessInstanceId();
			// 根据流程实例id查询流程实例
			ProcessInstance processInstance = runtimeService
					.createProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult();
			// 获取业务id
			String businessKey = processInstance.getBusinessKey();
			// 查询请假实体
			entityManager.setEntityClass(Leave.class);
			Leave leave = entityManager.get(businessKey);
			// 设置属性

			leave.setProcessInstanceId(processInstance.getId());

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

			entityManager.setEntityClass(Leave.class);
			Leave leave = entityManager.get(businessKey);

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

			entityManager.setEntityClass(Leave.class);
			Leave leave = entityManager.get(businessKey);

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

	/**
	 * 根据任务Id查询任务
	 */
	public TaskEntity findTaskById(String taskId) throws Exception {
		TaskEntity task = (TaskEntity) taskService.createTaskQuery()
				.taskId(taskId).singleResult();
		if (task == null) {
			throw new Exception("任务实例未找到!");
		}
		return task;
	}

}
