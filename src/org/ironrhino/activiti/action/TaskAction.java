package org.ironrhino.activiti.action;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.CommentEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.IdentityLinkType;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.ironrhino.activiti.component.ProcessPermissionChecker;
import org.ironrhino.activiti.form.FormElement;
import org.ironrhino.activiti.form.FormRenderer;
import org.ironrhino.activiti.form.FormRendererHandler;
import org.ironrhino.activiti.model.Row;
import org.ironrhino.activiti.model.TaskQueryCriteria;
import org.ironrhino.activiti.service.FormSubmissionService;
import org.ironrhino.activiti.service.ProcessHelper;
import org.ironrhino.core.metadata.Authorize;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.model.ResultPage;
import org.ironrhino.core.security.role.UserRole;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.core.util.AuthzUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import com.opensymphony.xwork2.util.ValueStack;

import lombok.Getter;
import lombok.Setter;

@AutoConfig(fileupload = "*/*")
@Authorize(ifAnyGranted = UserRole.ROLE_BUILTIN_USER)
public class TaskAction extends BaseAction {

	private static final long serialVersionUID = 314143213105332544L;

	@Autowired(required = false)
	private ProcessPermissionChecker processPermissionChecker;

	@Autowired(required = false)
	private List<FormRendererHandler> formRendererHandlers;

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

	@Autowired
	private FormSubmissionService formSubmissionService;

	@Autowired
	private ProcessHelper processHelper;

	@Getter
	@Setter
	private String assignee;

	@Getter
	@Setter
	private String title;

	@Getter
	private Map<String, FormElement> formElements;

	@Getter
	private String formTemplate;

	@Getter
	@Setter
	private ResultPage<Row> resultPage;

	@Getter
	private List<Row> list;

	@Getter
	@Setter
	private TaskQueryCriteria criteria;

	@Getter
	@Setter
	private String processDefinitionId;

	@Getter
	@Setter
	private String processInstanceId;

	@Getter
	@Setter
	private String processDefinitionKey;

	@Getter
	@Setter
	private String formKey;

	@Getter
	private Task task;

	@Getter
	private ProcessDefinition processDefinition;

	@Getter
	private HistoricProcessInstance historicProcessInstance;

	@Getter
	private List<HistoricTaskInstance> historicTaskInstances;

	@Getter
	private List<Attachment> attachments;

	@Getter
	private List<Comment> comments;

	@Getter
	@Setter
	private File[] _attachment_;

	@Getter
	@Setter
	private String[] _attachment_FileName;

	@Getter
	@Setter
	private String[] _attachment_description_;

	@Getter
	private Map<String, Object> taskVariables;

	@Override
	@Authorize(ifAnyGranted = UserRole.ROLE_ADMINISTRATOR)
	public String execute() {
		if (resultPage == null)
			resultPage = new ResultPage<Row>();
		TaskQuery query = taskService.createTaskQuery();
		if (criteria != null)
			criteria.filter(query, true);
		long count = query.count();
		resultPage.setTotalResults(count);
		if (count > 0) {
			List<Task> tasks = query.orderByTaskCreateTime().desc().listPage(resultPage.getStart(),
					resultPage.getPageSize());
			List<Row> list = new ArrayList<Row>(tasks.size());
			for (Task task : tasks) {
				Row row = new Row();
				list.add(row);
				row.setId(task.getId());
				row.setTask(task);
				row.setProcessDefinition(repositoryService.createProcessDefinitionQuery()
						.processDefinitionId(task.getProcessDefinitionId()).singleResult());
				row.setHistoricProcessInstance(historyService.createHistoricProcessInstanceQuery()
						.processInstanceId(task.getProcessInstanceId()).singleResult());
			}
			resultPage.setResult(list);
		}
		return LIST;
	}

	@Override
	@Authorize(ifAnyGranted = UserRole.ROLE_ADMINISTRATOR)
	public String delete() {
		if (getId() != null) {
			taskService.deleteTasks(Arrays.asList(getId()), true);
			addActionMessage(getText("delete.success"));
		}
		return SUCCESS;
	}

	@Authorize(ifAnyGranted = UserRole.ROLE_ADMINISTRATOR)
	@InputConfig(resultName = "reassign")
	public String reassign() {
		String taskId = getUid();
		User user = null;
		if (assignee != null) {
			user = identityService.createUserQuery().userId(assignee).singleResult();
		}
		if (user == null) {
			addFieldError("assignee", "该用户不存在");
			return "delegate";
		}
		taskService.setAssignee(taskId, assignee);
		return todolist();
	}

	public String todotabs() {
		return "todotabs";
	}

	public String todolist() {
		String userId = AuthzUtils.getUsername();
		List<Task> all = new ArrayList<Task>();
		String type = ServletActionContext.getRequest().getParameter("type");
		if ("assigned".equals(type)) {
			all.addAll(processHelper.findAssignedTasks(userId, criteria));
		} else if ("candidate".equals(type)) {
			all.addAll(processHelper.findCandidateTasks(userId, criteria));
		} else {
			all.addAll(processHelper.findAssignedTasks(userId, criteria));
			all.addAll(processHelper.findCandidateTasks(userId, criteria));
		}
		list = new ArrayList<Row>();
		for (Task task : all) {
			Row row = new Row();
			list.add(row);
			row.setId(task.getId());
			row.setTask(task);
			row.setProcessDefinition(repositoryService.createProcessDefinitionQuery()
					.processDefinitionId(task.getProcessDefinitionId()).singleResult());
			row.setHistoricProcessInstance(historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(task.getProcessInstanceId()).singleResult());
		}
		return "todolist";
	}

	public String form() {
		String taskId = getUid();
		if (taskId == null) {
			if (processDefinitionId == null) {
				if (processDefinitionKey != null) {
					List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
							.processDefinitionKey(processDefinitionKey).active().orderByProcessDefinitionVersion()
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
				processDefinition = repositoryService.createProcessDefinitionQuery()
						.processDefinitionId(processDefinitionId).active().singleResult();
			}
			if (processDefinition == null)
				return ACCESSDENIED;
			if (processPermissionChecker != null && !processPermissionChecker.canStart(processDefinition.getKey()))
				return ACCESSDENIED;
			if (!canStartProcess(processDefinitionId))
				return ACCESSDENIED;
			title = processDefinition.getName();
			StartFormData startFormData = formService.getStartFormData(processDefinitionId);
			formElements = formRenderer.render(startFormData);
			if (formRendererHandlers != null)
				for (FormRendererHandler formRendererHandler : formRendererHandlers)
					formRendererHandler.handle(formElements, processDefinition.getKey(), null);
			processDefinitionKey = processDefinition.getKey();
			formKey = formService.getStartFormKey(processDefinitionId);
			StringBuilder sb = new StringBuilder();
			sb.append("resources/view/process/form/");
			sb.append(processDefinitionKey);
			if (StringUtils.isNotBlank(formKey))
				sb.append("_").append(formKey);
			sb.append(".ftl");
			ClassPathResource cpr = new ClassPathResource(sb.toString());
			if (cpr.exists() && cpr.isReadable()) {
				try (InputStream is = cpr.getInputStream()) {
					formTemplate = StreamUtils.copyToString(is, Charset.forName("UTF-8"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			task = taskService.createTaskQuery().taskId(taskId).singleResult();
			if (task == null || !AuthzUtils.getUsername().equals(task.getAssignee()))
				return ACCESSDENIED;
			title = task.getName();
			processDefinition = repositoryService.createProcessDefinitionQuery()
					.processDefinitionId(task.getProcessDefinitionId()).singleResult();
			if (processDefinition == null)
				return ACCESSDENIED;
			title += " - " + processDefinition.getName();
			taskVariables = taskService.getVariables(task.getId());
			TaskFormData taskFormData = formService.getTaskFormData(taskId);
			formElements = formRenderer.render(taskFormData);
			if (formRendererHandlers != null)
				for (FormRendererHandler formRendererHandler : formRendererHandlers)
					formRendererHandler.handle(formElements, processDefinition.getKey(), task.getTaskDefinitionKey());
			processDefinitionKey = processDefinition.getKey();
			formKey = formService.getTaskFormKey(processDefinition.getId(), task.getTaskDefinitionKey());
			if (StringUtils.isBlank(formKey))
				formKey = task.getTaskDefinitionKey();
			StringBuilder sb = new StringBuilder();
			sb.append("resources/view/process/form/");
			sb.append(processDefinitionKey);
			sb.append("_");
			sb.append(formKey);
			sb.append(".ftl");
			ClassPathResource cpr = new ClassPathResource(sb.toString());
			if (cpr.exists() && cpr.isReadable()) {
				try (InputStream is = cpr.getInputStream()) {
					formTemplate = StreamUtils.copyToString(is, Charset.forName("UTF-8"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(task.getProcessInstanceId()).singleResult();
			historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
					.processInstanceId(task.getProcessInstanceId()).finished().list();
			attachments = taskService.getProcessInstanceAttachments(task.getProcessInstanceId());
			comments = taskService.getProcessInstanceComments(task.getProcessInstanceId(), CommentEntity.TYPE_COMMENT);
			Map<String, Object> variables = taskService.getVariables(taskId);
			if (variables != null) {
				ValueStack vs = ActionContext.getContext().getValueStack();
				for (Map.Entry<String, Object> entry : variables.entrySet()) {
					if (entry.getValue() != null)
						vs.set(entry.getKey(), entry.getValue());
				}
			}
		}
		return "form";
	}

	public String submit() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		String taskId = getUid();
		try {
			if (taskId == null) {
				if (processDefinitionId == null) {
					if (processDefinitionKey != null) {
						List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
								.processDefinitionKey(processDefinitionKey).active().orderByProcessDefinitionVersion()
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
					processDefinition = repositoryService.createProcessDefinitionQuery()
							.processDefinitionId(processDefinitionId).active().singleResult();
				}
				if (processDefinition == null)
					return ACCESSDENIED;
				if (processPermissionChecker != null && !processPermissionChecker.canStart(processDefinition.getKey()))
					return ACCESSDENIED;
				if (!canStartProcess(processDefinitionId))
					return ACCESSDENIED;
				ProcessInstance processInstance = formSubmissionService.submitStartForm(processDefinitionId,
						request.getParameterMap(), _attachment_FileName, _attachment_description_, _attachment_);
				addActionMessage(
						"启动流程 (ID=" + processInstance.getId() + " , KEY=" + processInstance.getBusinessKey() + ")");
			} else {
				task = taskService.createTaskQuery().taskId(taskId).singleResult();
				if (task == null || !AuthzUtils.getUsername().equals(task.getAssignee()))
					return ACCESSDENIED;
				formSubmissionService.submitTaskForm(taskId, request.getParameterMap(), _attachment_FileName,
						_attachment_description_, _attachment_);
				addActionMessage(getText("operate.success"));
			}
		} catch (ActivitiException e) {
			String message = e.getMessage();
			if (message != null && message.startsWith("form property '") && message.endsWith("' is required")) {
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
		String attachmentId = getUid();
		if (attachmentId == null)
			return NOTFOUND;
		Attachment attachment = taskService.getAttachment(attachmentId);
		if (attachment == null)
			return NOTFOUND;
		processInstanceId = attachment.getProcessInstanceId();
		if (!AuthzUtils.authorize(null, UserRole.ROLE_ADMINISTRATOR, null)) {
			String userId = AuthzUtils.getUsername();
			boolean auth = false;
			List<HistoricIdentityLink> historicIdentityLinks = historyService
					.getHistoricIdentityLinksForProcessInstance(processInstanceId);
			for (HistoricIdentityLink identityLink : historicIdentityLinks) {
				if (userId.equals(identityLink.getUserId())) {
					auth = true;
					break;
				}
				String groupId = identityLink.getGroupId();
				if (AuthzUtils.authorize(null, groupId, null)) {
					auth = true;
					break;
				}
			}
			if (!auth) {
				String taskId = attachment.getTaskId();
				if (taskId != null) {
					Task task = taskService.createTaskQuery().taskId(taskId).taskCandidateOrAssigned(userId)
							.singleResult();
					if (task != null) {
						auth = true;
					}
				} else {
					List<Task> tasks = taskService.createTaskQuery()
							.processInstanceId(attachment.getProcessInstanceId()).taskCandidateOrAssigned(userId)
							.list();
					if (tasks.size() > 0) {
						auth = true;
					}
				}
			}
			if (!auth)
				return ACCESSDENIED;
		}
		try (InputStream is = taskService.getAttachmentContent(attachmentId)) {
			// ServletActionContext.getResponse().setHeader("Content-Disposition",
			// "attachment;filename=" + attachment.getName());
			ServletOutputStream os = ServletActionContext.getResponse().getOutputStream();
			IOUtils.copy(is, os);
			os.flush();
			os.close();
		}
		return NONE;
	}

	public String deleteAttachment() {
		String attachmentId = getUid();
		if (attachmentId == null)
			return NOTFOUND;
		Attachment attachment = taskService.getAttachment(attachmentId);
		if (attachment == null)
			return NOTFOUND;
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(attachment.getProcessInstanceId()).singleResult();
		if (processInstance == null) {
			addActionError("结束的流程不允许删除附件");
			return ACCESSDENIED;
		}
		List<Task> list = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().list();
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

	public String deleteComment() {
		String commentId = getUid();
		if (commentId == null)
			return NOTFOUND;
		Comment comment = taskService.getComment(commentId);
		if (comment == null)
			return NOTFOUND;
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(comment.getProcessInstanceId()).singleResult();
		if (processInstance == null) {
			addActionError("结束的流程不允许删除附件");
			return ACCESSDENIED;
		}
		List<Task> list = taskService.createTaskQuery().processInstanceId(processInstance.getId()).active().list();
		boolean assigned = false;
		for (Task task : list) {
			if (AuthzUtils.getUsername().equals(task.getAssignee())) {
				assigned = true;
				break;
			}
		}
		if (assigned && AuthzUtils.getUsername().equals(comment.getUserId())) {
			taskService.deleteComment(commentId);
		} else {
			addActionError("只能在当前任务里面删除自己的备注");
			return ACCESSDENIED;
		}
		return JSON;
	}

	public String claim() {
		String[] ids = getId();
		for (String taskId : ids) {
			List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
			boolean authorized = identityLinks.isEmpty();
			for (IdentityLink identityLink : identityLinks) {
				if (identityLink.getType().equals(IdentityLinkType.CANDIDATE)) {
					String userId = identityLink.getUserId();
					String groupId = identityLink.getGroupId();
					if (userId != null && AuthzUtils.getUsername().equals(userId)) {
						authorized = true;
						break;
					}
					if (groupId != null && AuthzUtils.getRoleNames().contains(groupId)) {
						authorized = true;
						break;
					}
				}
			}
			if (!authorized || processPermissionChecker != null && !processPermissionChecker.canClaim(taskId)) {
				if (ids.length > 1)
					continue;
				return ACCESSDENIED;
			}
			try {
				taskService.claim(taskId, AuthzUtils.getUsername());
			} catch (ActivitiTaskAlreadyClaimedException e) {
				if (ids.length == 1)
					addActionError("任务已经被别人签收了");
			}
		}
		return form();
	}

	public String unclaim() {
		String[] ids = getId();
		for (String taskId : ids) {
			task = taskService.createTaskQuery().taskId(taskId).singleResult();
			if (task.getDelegationState() != null) {
				if (ids.length > 1)
					continue;
				return ACCESSDENIED;
			}
			List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
			if (identityLinks.size() == 1) {
				IdentityLink identityLink = identityLinks.get(0);
				if (identityLink.getType().equals(IdentityLinkType.ASSIGNEE)
						&& AuthzUtils.getUsername().equals(identityLink.getUserId())) {
					if (ids.length > 1)
						continue;
					addActionError("不能撤销直接指派的任务");
					return ERROR;
				}
			} else if (identityLinks.size() > 1) {
				boolean authorized = false;
				for (IdentityLink identityLink : identityLinks) {
					if (identityLink.getType().equals(IdentityLinkType.ASSIGNEE)) {
						String userId = identityLink.getUserId();
						if (userId != null && AuthzUtils.getUsername().equals(userId)) {
							authorized = true;
							break;
						}
					}
				}
				if (!authorized) {
					if (ids.length > 1)
						continue;
					return ACCESSDENIED;
				}
			}
			taskService.unclaim(taskId);
		}
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
			user = identityService.createUserQuery().userId(assignee).singleResult();
		}
		if (user == null) {
			addFieldError("assignee", "该用户不存在");
			return "delegate";
		}
		String taskId = getUid();
		List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
		if (identityLinks.size() > 1) {
			boolean authorized = false;
			for (IdentityLink identityLink : identityLinks) {
				if (identityLink.getType().equals(IdentityLinkType.ASSIGNEE)) {
					String userId = identityLink.getUserId();
					if (userId != null && AuthzUtils.getUsername().equals(userId)) {
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
		List<IdentityLink> identityLinks = repositoryService.getIdentityLinksForProcessDefinition(processDefinitionId);
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
				if (groupId != null && AuthzUtils.getRoleNames().contains(groupId)) {
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
