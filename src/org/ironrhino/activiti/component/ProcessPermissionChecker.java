package org.ironrhino.activiti.component;

import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ProcessPermissionChecker {

	@Autowired
	private TaskService taskService;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private HistoryService historyService;

	public boolean canStart(String processDefinitionKey) {
		List<ProcessDefinition> list = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionKey(processDefinitionKey).active()
				.orderByProcessDefinitionVersion().desc().listPage(0, 1);
		ProcessDefinition processDefinition = null;
		if (!list.isEmpty())
			processDefinition = list.get(0);
		return canStart(processDefinition);
	}

	protected boolean canStart(ProcessDefinition processDefinition) {
		if (processDefinition == null)
			return false;
		return true;
	}

	public boolean canClaim(String taskId) {
		Task task = taskService.createTaskQuery().taskId(taskId).active()
				.singleResult();
		return canClaim(task);
	}

	public boolean canClaim(Task task) {
		if (task == null)
			return false;
		HistoricProcessInstance hpi = historyService
				.createHistoricProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).unfinished()
				.singleResult();
		if (hpi == null)
			return false;
		return canClaim(task, hpi.getStartUserId());
	}

	protected boolean canClaim(Task task, String startUserId) {
		return true;
	}
}
