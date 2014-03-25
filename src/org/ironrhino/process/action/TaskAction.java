package org.ironrhino.process.action;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.IdentityLinkType;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.ironrhino.core.metadata.Authorize;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.model.ResultPage;
import org.ironrhino.core.security.role.UserRole;
import org.ironrhino.core.sequence.CyclicSequence;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.core.util.AuthzUtils;
import org.ironrhino.core.util.RequestUtils;
import org.ironrhino.process.form.FormRenderer;
import org.ironrhino.process.form.FormRenderer.FormElement;
import org.ironrhino.process.model.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import com.opensymphony.xwork2.interceptor.annotations.InputConfig;

@AutoConfig
@Authorize(ifAnyGranted = UserRole.ROLE_BUILTIN_USER)
public class TaskAction extends BaseAction {

	private static final long serialVersionUID = 314143213105332544L;

	@Autowired(required = false)
	@Qualifier("businessKeySequence")
	private CyclicSequence businessKeySequence;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private FormService formService;

	@Autowired
	private FormRenderer formRenderer;

	private String assignee;

	private String title;

	private Map<String, FormElement> formElements;

	private String formTemplate;

	private ResultPage<Row> resultPage;

	private List<Row> list;

	private String processDefinitionId;

	private String processDefinitionKey;

	private Task task;

	private ProcessDefinition processDefinition;

	private HistoricProcessInstance historicProcessInstance;

	private List<HistoricTaskInstance> historicTaskInstances;

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

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, FormElement> getFormElements() {
		return formElements;
	}

	public String getFormTemplate() {
		return formTemplate;
	}

	public ResultPage<Row> getResultPage() {
		return resultPage;
	}

	public void setResultPage(ResultPage<Row> resultPage) {
		this.resultPage = resultPage;
	}

	public List<Row> getList() {
		return list;
	}

	public Task getTask() {
		return task;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public HistoricProcessInstance getHistoricProcessInstance() {
		return historicProcessInstance;
	}

	public List<HistoricTaskInstance> getHistoricTaskInstances() {
		return historicTaskInstances;
	}

	@Authorize(ifAnyGranted = UserRole.ROLE_ADMINISTRATOR)
	public String execute() {
		if (resultPage == null)
			resultPage = new ResultPage<Row>();
		TaskQuery query = taskService.createTaskQuery();
		String processDefinitionId = getUid();
		if (StringUtils.isNotBlank(processDefinitionId))
			query = query.processDefinitionId(processDefinitionId);
		if (StringUtils.isNotBlank(processDefinitionKey))
			query = query.processDefinitionKey(processDefinitionKey);
		long count = query.count();
		resultPage.setTotalResults(count);
		if (count > 0) {
			List<Task> tasks = query.orderByTaskCreateTime().desc()
					.listPage(resultPage.getStart(), resultPage.getPageSize());
			List<Row> list = new ArrayList<Row>(tasks.size());
			for (Task task : tasks) {
				Row row = new Row();
				list.add(row);
				row.setId(task.getId());
				row.setTask(task);
				row.setProcessDefinition(repositoryService
						.createProcessDefinitionQuery()
						.processDefinitionId(task.getProcessDefinitionId())
						.singleResult());
				row.setHistoricProcessInstance(historyService
						.createHistoricProcessInstanceQuery()
						.processInstanceId(task.getProcessInstanceId())
						.singleResult());
			}
			resultPage.setResult(list);
		}
		return LIST;
	}

	@Authorize(ifAnyGranted = UserRole.ROLE_ADMINISTRATOR)
	@InputConfig(resultName = "reassign")
	public String reassign() {
		String taskId = getUid();
		User user = null;
		if (assignee != null) {
			user = identityService.createUserQuery().userId(assignee)
					.singleResult();
		}
		if (user == null) {
			addFieldError("assignee", "该用户不存在");
			return "delegate";
		}
		taskService.setAssignee(taskId, assignee);
		return execute();
	}

	public String todolist() {
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
		list = new ArrayList<Row>();
		for (Task task : all) {
			Row row = new Row();
			list.add(row);
			row.setId(task.getId());
			row.setTask(task);
			row.setProcessDefinition(repositoryService
					.createProcessDefinitionQuery()
					.processDefinitionId(task.getProcessDefinitionId())
					.singleResult());
			row.setHistoricProcessInstance(historyService
					.createHistoricProcessInstanceQuery()
					.processInstanceId(task.getProcessInstanceId())
					.singleResult());
		}
		return "todolist";
	}

	public String form() {
		String taskId = getUid();
		if (taskId == null) {
			if (processDefinitionId == null) {
				if (processDefinitionKey != null) {
					List<ProcessDefinition> processDefinitions = repositoryService
							.createProcessDefinitionQuery()
							.processDefinitionKey(processDefinitionKey)
							.active().orderByProcessDefinitionVersion().desc()
							.listPage(0, 1);
					if (!processDefinitions.isEmpty()) {
						processDefinition = processDefinitions.get(0);
						processDefinitionId = processDefinition.getId();
					} else {
						return ACCESSDENIED;
					}
				} else {
					return ACCESSDENIED;
				}
			} else {
				processDefinition = repositoryService
						.createProcessDefinitionQuery()
						.processDefinitionId(processDefinitionId).active()
						.singleResult();
			}
			if (processDefinition == null)
				return ACCESSDENIED;
			if (!canStartProcess(processDefinitionId))
				return ACCESSDENIED;
			title = processDefinition.getName();
			StartFormData startFormData = formService
					.getStartFormData(processDefinitionId);
			formElements = formRenderer.render(startFormData);
			StringBuilder sb = new StringBuilder();
			sb.append("resources/view/process/form/");
			sb.append(processDefinition.getKey());
			sb.append(".ftl");
			ClassPathResource cpr = new ClassPathResource(sb.toString());
			if (cpr.exists() && cpr.isReadable()) {
				try (InputStream is = cpr.getInputStream()) {
					formTemplate = StreamUtils.copyToString(is,
							Charset.forName("UTF-8"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			task = taskService.createTaskQuery().taskId(taskId).singleResult();
			if (task == null
					|| !AuthzUtils.getUsername().equals(task.getAssignee()))
				return ACCESSDENIED;
			title = task.getName();
			processDefinition = repositoryService
					.createProcessDefinitionQuery()
					.processDefinitionId(task.getProcessDefinitionId())
					.singleResult();
			if (processDefinition != null)
				title += " - " + processDefinition.getName();
			TaskFormData taskFormData = formService.getTaskFormData(taskId);
			formElements = formRenderer.render(taskFormData);
			StringBuilder sb = new StringBuilder();
			sb.append("resources/view/process/form/");
			sb.append(processDefinition.getKey());
			sb.append("_");
			sb.append(task.getTaskDefinitionKey());
			sb.append(".ftl");
			ClassPathResource cpr = new ClassPathResource(sb.toString());
			if (cpr.exists() && cpr.isReadable()) {
				try (InputStream is = cpr.getInputStream()) {
					formTemplate = StreamUtils.copyToString(is,
							Charset.forName("UTF-8"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			historicProcessInstance = historyService
					.createHistoricProcessInstanceQuery()
					.processInstanceId(task.getProcessInstanceId())
					.singleResult();
			historicTaskInstances = historyService
					.createHistoricTaskInstanceQuery()
					.processInstanceId(task.getProcessInstanceId()).finished()
					.list();
		}
		return "form";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String submit() {
		String taskId = getUid();
		Map properties = RequestUtils.getParametersMap(ServletActionContext
				.getRequest());
		properties.remove("processDefinitionId");
		try {
			if (taskId == null) {
				if (processDefinitionId == null) {
					if (processDefinitionKey != null) {
						List<ProcessDefinition> processDefinitions = repositoryService
								.createProcessDefinitionQuery()
								.processDefinitionKey(processDefinitionKey)
								.active().orderByProcessDefinitionVersion()
								.desc().listPage(0, 1);
						if (!processDefinitions.isEmpty()) {
							processDefinition = processDefinitions.get(0);
							processDefinitionId = processDefinition.getId();
						} else {
							return ACCESSDENIED;
						}
					} else {
						return ACCESSDENIED;
					}
				} else {
					processDefinition = repositoryService
							.createProcessDefinitionQuery()
							.processDefinitionId(processDefinitionId).active()
							.singleResult();
				}
				if (processDefinition == null)
					return ACCESSDENIED;
				if (!canStartProcess(processDefinitionId))
					return ACCESSDENIED;
				String businessKey = businessKeySequence != null ? businessKeySequence
						.nextStringValue() : String.valueOf(System
						.currentTimeMillis());
				ProcessInstance processInstance = formService
						.submitStartFormData(processDefinitionId, businessKey,
								properties);
				addActionMessage("启动流程: " + processInstance.getId());
			} else {
				task = taskService.createTaskQuery().taskId(taskId)
						.singleResult();
				if (task == null
						|| !AuthzUtils.getUsername().equals(task.getAssignee()))
					return ACCESSDENIED;
				DelegationState delegationState = task.getDelegationState();
				if (DelegationState.PENDING == delegationState) {
					taskService.resolveTask(taskId, properties);
				} else {
					formService.submitTaskFormData(taskId, properties);
				}
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
		String taskId = getUid();
		List<IdentityLink> identityLinks = taskService
				.getIdentityLinksForTask(taskId);
		boolean authorized = identityLinks.isEmpty();
		for (IdentityLink identityLink : identityLinks) {
			if (identityLink.getType().equals(IdentityLinkType.CANDIDATE)) {
				String userId = identityLink.getUserId();
				String groupId = identityLink.getGroupId();
				if (userId != null && AuthzUtils.getUsername().equals(userId)) {
					authorized = true;
					break;
				}
				if (groupId != null
						&& AuthzUtils.getRoleNames().contains(groupId)) {
					authorized = true;
					break;
				}
			}
		}
		if (!authorized)
			return ACCESSDENIED;
		taskService.claim(taskId, AuthzUtils.getUsername());
		return execute();
	}

	public String unclaim() {
		String taskId = getUid();
		task = taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task.getDelegationState() != null)
			return ACCESSDENIED;
		List<IdentityLink> identityLinks = taskService
				.getIdentityLinksForTask(taskId);
		if (identityLinks.size() == 1) {
			IdentityLink identityLink = identityLinks.get(0);
			if (identityLink.getType().equals(IdentityLinkType.ASSIGNEE)
					&& AuthzUtils.getUsername()
							.equals(identityLink.getUserId())) {
				addActionError("不能撤销非签收的任务");
				return ERROR;
			}
		} else if (identityLinks.size() > 1) {
			boolean authorized = false;
			for (IdentityLink identityLink : identityLinks) {
				if (identityLink.getType().equals(IdentityLinkType.ASSIGNEE)) {
					String userId = identityLink.getUserId();
					if (userId != null
							&& AuthzUtils.getUsername().equals(userId)) {
						authorized = true;
						break;
					}
				}
			}
			if (!authorized)
				return ACCESSDENIED;
		}
		taskService.unclaim(taskId);
		return execute();
	}

	@InputConfig(resultName = "delegate")
	public String delegate() {
		User user = null;
		if (assignee != null) {
			if (assignee.equals(AuthzUtils.getUsername())) {
				addFieldError("assignee", "不能委派给自己");
				return "delegate";
			}
			user = identityService.createUserQuery().userId(assignee)
					.singleResult();
		}
		if (user == null) {
			addFieldError("assignee", "该用户不存在");
			return "delegate";
		}
		String taskId = getUid();
		List<IdentityLink> identityLinks = taskService
				.getIdentityLinksForTask(taskId);
		if (identityLinks.size() > 1) {
			boolean authorized = false;
			for (IdentityLink identityLink : identityLinks) {
				if (identityLink.getType().equals(IdentityLinkType.ASSIGNEE)) {
					String userId = identityLink.getUserId();
					if (userId != null
							&& AuthzUtils.getUsername().equals(userId)) {
						authorized = true;
						break;
					}
				}
			}
			if (!authorized)
				return ACCESSDENIED;
		}
		taskService.delegateTask(taskId, assignee);
		return execute();
	}

	private boolean canStartProcess(String processDefinitionId) {
		List<IdentityLink> identityLinks = repositoryService
				.getIdentityLinksForProcessDefinition(processDefinitionId);
		boolean authorized;
		if (!identityLinks.isEmpty()) {
			authorized = false;
			for (IdentityLink identityLink : identityLinks) {
				String userId = identityLink.getUserId();
				String groupId = identityLink.getGroupId();
				if (userId != null && AuthzUtils.getUsername().equals(userId)) {
					authorized = true;
					break;
				}
				if (groupId != null
						&& AuthzUtils.getRoleNames().contains(groupId)) {
					authorized = true;
					break;
				}
			}

		} else {
			authorized = true;
		}
		return authorized;
	}

}
