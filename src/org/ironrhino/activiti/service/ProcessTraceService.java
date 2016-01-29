package org.ironrhino.activiti.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricFormProperty;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.ironrhino.activiti.form.FormRenderer;
import org.ironrhino.activiti.model.ActivityDetail;
import org.ironrhino.core.struts.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ProcessTraceService {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected RuntimeService runtimeService;

	@Autowired
	protected HistoryService historyService;

	@Autowired
	protected FormService formService;

	@Autowired
	protected FormRenderer formRenderer;

	@Autowired
	protected TaskService taskService;

	@Autowired
	protected RepositoryService repositoryService;

	@Autowired
	protected IdentityService identityService;

	@Autowired
	protected UserDetailsService userDetailsService;

	public List<ActivityDetail> traceHistoricProcessInstance(String processInstanceId) {
		List<ActivityDetail> details = new ArrayList<ActivityDetail>();
		List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
		for (HistoricActivityInstance activity : activities) {
			if (!"userTask".equals(activity.getActivityType()) && !"startEvent".equals(activity.getActivityType()))
				continue;
			ActivityDetail detail = new ActivityDetail();
			detail.setStartTime(activity.getStartTime());
			detail.setEndTime(activity.getEndTime());
			details.add(detail);
			if ("startEvent".equals(activity.getActivityType())) {
				HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
						.processInstanceId(processInstanceId).singleResult();
				detail.setName("startProcessInstance");
				detail.setAssignee(hpi.getStartUserId());
				List<Attachment> attachments = taskService.getProcessInstanceAttachments(hpi.getId());
				Iterator<Attachment> it = attachments.iterator();
				while (it.hasNext()) {
					Attachment attachment = it.next();
					if (!hpi.getStartUserId().equals(attachment.getUserId()))
						it.remove();
				}
				detail.setAttachments(attachments);

				List<Comment> comments = taskService.getProcessInstanceComments(hpi.getId());
				Iterator<Comment> itc = comments.iterator();
				while (itc.hasNext()) {
					Comment comment = itc.next();
					if (!hpi.getStartUserId().equals(comment.getUserId()))
						itc.remove();
				}
				detail.setComments(comments);
			} else {
				detail.setName(activity.getActivityName());
				detail.setAssignee(activity.getAssignee());
				HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery()
						.processInstanceId(processInstanceId).taskId(activity.getTaskId()).singleResult();
				List<Attachment> attachments = taskService.getTaskAttachments(task.getId());
				Iterator<Attachment> it = attachments.iterator();
				while (it.hasNext()) {
					Attachment attachment = it.next();
					if (!task.getAssignee().equals(attachment.getUserId()))
						it.remove();
				}
				detail.setAttachments(attachments);
				List<Comment> comments = taskService.getTaskComments(task.getId());
				Iterator<Comment> itc = comments.iterator();
				while (itc.hasNext()) {
					Comment comment = itc.next();
					if (!task.getAssignee().equals(comment.getUserId()))
						itc.remove();
				}
				detail.setComments(comments);
			}
			List<HistoricDetail> list = historyService.createHistoricDetailQuery().activityInstanceId(activity.getId())
					.list();
			for (HistoricDetail hd : list) {
				if (hd instanceof HistoricFormProperty) {
					HistoricFormProperty hfp = (HistoricFormProperty) hd;
					if (hfp.getPropertyValue() != null)
						detail.getData().put(hfp.getPropertyId(), hfp.getPropertyValue());
				}
			}
			detail.setData(formRenderer.display(activity.getProcessDefinitionId(), activity.getActivityId(),
					detail.getData()));

		}
		return details;
	}

	public List<Map<String, Object>> traceProcessDefinition(String processDefinitionId) throws Exception {
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(processDefinitionId);
		List<ActivityImpl> activitiList = processDefinition.getActivities();
		List<Map<String, Object>> activities = new ArrayList<Map<String, Object>>();
		for (ActivityImpl activity : activitiList) {
			Map<String, Object> activityImageInfo = packageSingleActivitiInfo(activity, null, processDefinition, false);
			activities.add(activityImageInfo);
		}
		return activities;
	}

	public List<Map<String, Object>> traceProcessInstance(String processInstanceId) throws Exception {
		HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
		List<String> activityIds = new ArrayList<>(executions.size());
		for (Execution execution : executions) {
			if ((execution instanceof ExecutionEntity) && ((ExecutionEntity) execution).isActive())
				activityIds.add(execution.getActivityId());
		}
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
		List<ActivityImpl> activitiList = processDefinition.getActivities();
		List<Map<String, Object>> activities = new ArrayList<Map<String, Object>>();
		for (ActivityImpl activity : activitiList) {
			boolean current = activityIds.contains(activity.getId());
			Map<String, Object> activityImageInfo = packageSingleActivitiInfo(activity, processInstanceId,
					processDefinition, current);
			activities.add(activityImageInfo);
		}
		return activities;
	}

	private Map<String, Object> packageSingleActivitiInfo(ActivityImpl activity, String processInstanceId,
			ProcessDefinitionEntity processDefinition, boolean current) throws Exception {
		Map<String, Object> activityInfo = new HashMap<String, Object>();
		if (current) {
			activityInfo.put("current", current);
			String key = processDefinition.getKey();
			String diagramResourceName = processDefinition.getDiagramResourceName();
			if (diagramResourceName.equals(key + "." + key + ".png"))
				activityInfo.put("border-radius", 5);
		}
		setPosition(activity, activityInfo);
		setWidthAndHeight(activity, activityInfo);
		Map<String, Object> vars = new LinkedHashMap<String, Object>();
		if (processInstanceId != null) {
			List<HistoricActivityInstance> historicActivityInstances = historyService
					.createHistoricActivityInstanceQuery().executionId(processInstanceId).activityId(activity.getId())
					.orderByHistoricActivityInstanceStartTime().desc().list();
			if (!historicActivityInstances.isEmpty()) {
				HistoricActivityInstance hai = historicActivityInstances.get(0);
				if (hai.getAssignee() != null) {
					User assigneeUser = identityService.createUserQuery().userId(hai.getAssignee()).singleResult();
					try {
						UserDetails userDetails = userDetailsService.loadUserByUsername(assigneeUser.getId());
						vars.put(translate("assignee"), userDetails.toString());
					} catch (UsernameNotFoundException e) {
						vars.put(translate("assignee"), assigneeUser.getFirstName());
					}
				}
				if (hai.getStartTime() != null)
					vars.put(translate("startTime"), hai.getStartTime());
				if (hai.getEndTime() != null)
					vars.put(translate("endTime"), hai.getEndTime());
			}
		}

		Map<String, Object> properties = activity.getProperties();
		String type = (String) properties.get("type");
		vars.put(translate("taskType"), translate(type));
		ActivityBehavior activityBehavior = activity.getActivityBehavior();
		if (activityBehavior instanceof UserTaskActivityBehavior) {
			Task currentTask = null;
			if (current) {
				currentTask = getCurrentTaskInfo(processInstanceId);
				if (currentTask != null)
					setCurrentTaskAssignee(vars, currentTask);
			}
			UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
			TaskDefinition taskDefinition = userTaskActivityBehavior.getTaskDefinition();
			setTaskGroup(vars, taskDefinition);

		}

		vars.put(translate("documentation"), properties.get("documentation"));

		activityInfo.put("vars", vars);
		return activityInfo;
	}

	private void setTaskGroup(Map<String, Object> vars, TaskDefinition taskDefinition) {
		Set<Expression> candidateGroupIdExpressions = taskDefinition.getCandidateGroupIdExpressions();
		StringBuilder roles = new StringBuilder();
		for (Expression expression : candidateGroupIdExpressions) {
			String expressionText = expression.getExpressionText();
			if (expressionText.indexOf("${") < 0) {
				appendRoles(roles, expressionText);
			}
		}
		if (roles.length() > 0) {
			roles.deleteCharAt(roles.length() - 1);
			vars.put(translate("candidateGroup"), roles.toString());
		}
	}

	private void appendRoles(StringBuilder roles, String text) {
		String[] groups = text.split("\\s*,\\s*");
		for (String s : groups) {
			appendRole(roles, s);
		}
	}

	private void appendRole(StringBuilder roles, String role) {
		Group g = identityService.createGroupQuery().groupId(role).singleResult();
		if (g != null) {
			String roleName = g.getName();
			if (roleName == null) {
				roleName = g.getId();
				roleName = translate(roleName);
			}
			roles.append(roleName).append(" ");
		}
	}

	private void setCurrentTaskAssignee(Map<String, Object> vars, Task currentTask) {
		String assignee = currentTask.getAssignee();
		if (assignee != null) {
			User assigneeUser = identityService.createUserQuery().userId(assignee).singleResult();
			try {
				UserDetails userDetails = userDetailsService.loadUserByUsername(assigneeUser.getId());
				vars.put(translate("assignee"), userDetails.toString());
			} catch (UsernameNotFoundException e) {
				vars.put(translate("assignee"), assigneeUser.getFirstName());
			}
		}
	}

	private Task getCurrentTaskInfo(String processInstanceId) {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		if (processInstance != null) {
			String activitiId = processInstance.getActivityId();
			return taskService.createTaskQuery().processInstanceId(processInstance.getId())
					.taskDefinitionKey(activitiId).singleResult();
		} else {
			return null;
		}
	}

	private void setWidthAndHeight(ActivityImpl activity, Map<String, Object> activityInfo) {
		activityInfo.put("width", activity.getWidth());
		activityInfo.put("height", activity.getHeight());
	}

	private void setPosition(ActivityImpl activity, Map<String, Object> activityInfo) {
		activityInfo.put("x", activity.getX());
		activityInfo.put("y", activity.getY());
	}

	private String translate(String key) {
		return I18N.getText(key);
	}
}