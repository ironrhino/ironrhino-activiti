package org.ironrhino.process.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

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
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.IdentityLinkType;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.io.IOUtils;
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

@AutoConfig(fileupload = "*/*")
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

	private String processInstanceId;

	private String processDefinitionKey;

	private Task task;

	private ProcessDefinition processDefinition;

	private HistoricProcessInstance historicProcessInstance;

	private List<HistoricTaskInstance> historicTaskInstances;

	private List<Attachment> attachments;

	private File[] file;

	private String[] fileFileName;

	private String[] attachmentDescription;

	private String attachmentId;

	public File[] getFile() {
		return file;
	}

	public void setFile(File[] file) {
		this.file = file;
	}

	public String[] getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String[] fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String[] getAttachmentDescription() {
		return attachmentDescription;
	}

	public void setAttachmentDescription(String[] attachmentDescription) {
		this.attachmentDescription = attachmentDescription;
	}

	public String getAttachmentId() {
		return attachmentId;
	}

	public void setAttachmentId(String attachmentId) {
		this.attachmentId = attachmentId;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
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

	public List<Attachment> getAttachments() {
		return attachments;
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
		return todolist();
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
			attachments = taskService.getProcessInstanceAttachments(task
					.getProcessInstanceId());
		}
		return "form";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String submit() throws IOException {
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
				if (fileFileName != null) {
					for (int i = 0; i < file.length; i++) {
						String description = attachmentDescription.length > i ? attachmentDescription[i]
								: null;
						taskService.createAttachment(null, null,
								processInstance.getId(), fileFileName[i],
								description, new FileInputStream(file[i]));
					}
				}
			} else {
				task = taskService.createTaskQuery().taskId(taskId)
						.singleResult();
				if (task == null
						|| !AuthzUtils.getUsername().equals(task.getAssignee()))
					return ACCESSDENIED;

				if (file != null) {
					for (int i = 0; i < file.length; i++) {
						String description = attachmentDescription.length > i ? attachmentDescription[i]
								: null;
						taskService.createAttachment(null, task.getId(),
								task.getProcessInstanceId(), fileFileName[i],
								description, new FileInputStream(file[i]));
					}
				}
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
		return todolist();
	}

	public String downloadAttachment() throws IOException {
		if (attachmentId == null)
			return NOTFOUND;
		Attachment attachment = taskService.getAttachment(attachmentId);
		if (attachment == null)
			return NOTFOUND;
		processInstanceId = attachment.getProcessInstanceId();
		String taskId = attachment.getTaskId();
		HistoricProcessInstance processInstance = null;
		if (processInstanceId == null && taskId != null) {
			task = taskService.createTaskQuery().taskId(taskId).singleResult();
			if (task == null)
				processInstanceId = task.getProcessInstanceId();
		}
		if (processInstanceId != null)
			processInstance = historyService
					.createHistoricProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult();
		if (processInstance == null)
			return NOTFOUND;
		if (!AuthzUtils.authorize(null, UserRole.ROLE_ADMINISTRATOR, null)) {
			String userId = AuthzUtils.getUsername();
			boolean auth = false;
			List<IdentityLink> identityLinks = runtimeService
					.getIdentityLinksForProcessInstance(processInstanceId);
			for (IdentityLink identityLink : identityLinks)
				if (userId.equals(identityLink.getUserId())) {
					auth = true;
					break;
				}
			if (!auth)
				return ACCESSDENIED;
		}
		try (InputStream is = taskService.getAttachmentContent(attachmentId)) {
			// ServletActionContext.getResponse().setHeader("Content-Disposition",
			// "attachment;filename=" + attachment.getName());
			ServletOutputStream os = ServletActionContext.getResponse()
					.getOutputStream();
			IOUtils.copy(is, os);
			os.flush();
			os.close();
		}
		return NONE;
	}

	public String deleteAttachment() {
		if (attachmentId == null)
			return NOTFOUND;
		Attachment attachment = taskService.getAttachment(attachmentId);
		if (attachment == null)
			return NOTFOUND;
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(attachment.getProcessInstanceId())
				.singleResult();
		if (processInstance == null) {
			addActionError("结束的流程不允许删除附件");
			return ACCESSDENIED;
		}
		List<Task> list = taskService.createTaskQuery()
				.processInstanceId(processInstance.getId()).active().list();
		boolean assigned = false;
		for (Task task : list) {
			if (AuthzUtils.getUsername().equals(task.getAssignee())) {
				assigned = true;
				break;
			}
		}
		if (assigned && AuthzUtils.getUsername().equals(attachment.getUserId())) {
			taskService.deleteAttachment(attachmentId);
		} else {
			addActionError("只能在当前任务里面删除自己的附件");
			return ACCESSDENIED;
		}
		return JSON;
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
		return todolist();
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
		return todolist();
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
		return todolist();
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
