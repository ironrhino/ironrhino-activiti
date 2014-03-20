package org.ironrhino.activiti.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.FormService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.struts2.ServletActionContext;
import org.ironrhino.activiti.form.FormRenderer;
import org.ironrhino.activiti.form.FormRenderer.FormElement;
import org.ironrhino.core.metadata.Authorize;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.security.role.UserRole;
import org.ironrhino.core.sequence.CyclicSequence;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.core.util.AuthzUtils;
import org.ironrhino.core.util.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@AutoConfig
@Authorize(ifAnyGranted = UserRole.ROLE_BUILTIN_USER)
public class TaskAction extends BaseAction {

	private static final long serialVersionUID = 314143213105332544L;

	@Autowired
	@Qualifier("businessKeySequence")
	private CyclicSequence businessKeySequence;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;

	@Autowired
	private FormRenderer formRenderer;

	private String title;

	private List<FormElement> formElements;

	private List<Tuple<Task, ProcessDefinition>> list;

	private String processDefinitionId;

	private String processDefinitionKey;

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}

	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<FormElement> getFormElements() {
		return formElements;
	}

	public List<Tuple<Task, ProcessDefinition>> getList() {
		return list;
	}

	public String execute() {
		String userid = AuthzUtils.getUsername();
		List<Task> taskAssignees = taskService.createTaskQuery()
				.taskAssignee(userid).orderByTaskPriority().desc()
				.orderByTaskCreateTime().desc().list();
		List<Task> taskCandidates = taskService.createTaskQuery()
				.taskCandidateUser(userid).orderByTaskPriority().desc()
				.orderByTaskCreateTime().desc().list();
		List<Task> all = new ArrayList<Task>();
		all.addAll(taskAssignees);
		all.addAll(taskCandidates);
		list = new ArrayList<Tuple<Task, ProcessDefinition>>();
		for (Task task : all) {
			Tuple<Task, ProcessDefinition> tuple = new Tuple<Task, ProcessDefinition>();
			list.add(tuple);
			tuple.setId(task.getId());
			tuple.setKey(task);
			tuple.setValue(repositoryService.createProcessDefinitionQuery()
					.processDefinitionId(task.getProcessDefinitionId())
					.singleResult());
		}
		return LIST;
	}

	public String form() {
		String taskId = getUid();
		if (taskId == null) {
			ProcessDefinition processDefinition = null;
			if (processDefinitionId == null) {
				if (processDefinitionKey != null) {
					processDefinition = repositoryService
							.createProcessDefinitionQuery()
							.processDefinitionKey(processDefinitionKey)
							.latestVersion().singleResult();
					if (processDefinition != null)
						processDefinitionId = processDefinition.getId();
					else
						return ACCESSDENIED;
				} else
					return ACCESSDENIED;
			} else {
				processDefinition = repositoryService
						.createProcessDefinitionQuery()
						.processDefinitionId(processDefinitionId)
						.singleResult();
			}
			if (processDefinition == null)
				return ACCESSDENIED;
			title = processDefinition.getName();
			StartFormData startFormData = formService
					.getStartFormData(processDefinitionId);
			formElements = formRenderer.render(startFormData);
		} else {
			Task task = taskService.createTaskQuery().taskId(taskId)
					.singleResult();
			if (task == null)
				return ACCESSDENIED;
			title = task.getName();
			ProcessDefinition processDefinition = repositoryService
					.createProcessDefinitionQuery()
					.processDefinitionId(task.getProcessDefinitionId())
					.singleResult();
			if (processDefinition != null)
				title += " - " + processDefinition.getName();
			TaskFormData taskFormData = formService.getTaskFormData(taskId);
			formElements = formRenderer.render(taskFormData);
			// ProcessInstance processInstance = runtimeService
			// .createProcessInstanceQuery()
			// .processInstanceId(task.getProcessInstanceId())
			// .singleResult();
			// Map<String, Object> vars = processInstance.getProcessVariables();
			// System.out.println(vars);
			// TODO show info
		}
		return "form";
	}

	public String submit() {
		String taskId = getUid();
		Map<String, String> properties = RequestUtils
				.getParametersMap(ServletActionContext.getRequest());
		properties.remove("processDefinitionId");
		try {
			if (taskId == null) {
				ProcessDefinition processDefinition = null;
				if (processDefinitionId == null) {
					if (processDefinitionKey != null) {
						processDefinition = repositoryService
								.createProcessDefinitionQuery()
								.processDefinitionKey(processDefinitionKey)
								.latestVersion().singleResult();
						if (processDefinition != null)
							processDefinitionId = processDefinition.getId();
						else
							return ACCESSDENIED;
					} else
						return ACCESSDENIED;
				} else {
					processDefinition = repositoryService
							.createProcessDefinitionQuery()
							.processDefinitionId(processDefinitionId)
							.singleResult();
				}
				if (processDefinition == null)
					return ACCESSDENIED;
				ProcessInstance processInstance = formService
						.submitStartFormData(processDefinitionId,
								businessKeySequence.nextStringValue(),
								properties);
				addActionMessage("启动流程: " + processInstance.getId());
			} else {
				Task task = taskService.createTaskQuery().taskId(taskId)
						.singleResult();
				if (task == null
						|| !AuthzUtils.getUsername().equals(task.getAssignee()))
					return ACCESSDENIED;
				formService.submitTaskFormData(taskId, properties);
				addActionMessage(getText("operate.success"));
			}
		} catch (ActivitiException e) {
			String message = e.getMessage();
			if (message != null && message.startsWith("form property '")
					&& message.endsWith("' is required")) {
				String fieldName = message.substring(message.indexOf('\'') + 1);
				fieldName = fieldName.substring(0, fieldName.indexOf('\''));
				addFieldError(fieldName, getText("validation.required"));
			} else {
				throw e;
			}
		}
		return execute();
	}

	public String claim() {
		taskService.claim(getUid(), AuthzUtils.getUsername());
		return execute();
	}

	public String unclaim() {
		taskService.unclaim(getUid());
		return execute();
	}

}
