package org.ironrhino.activiti.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.LocalizedTextUtil;

@Component
public class ProcessTraceService {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	protected RuntimeService runtimeService;

	@Autowired
	protected TaskService taskService;

	@Autowired
	protected RepositoryService repositoryService;

	@Autowired
	protected IdentityService identityService;

	@Autowired
	protected UserDetailsService userDetailsService;

	public List<Map<String, Object>> traceProcessDefinition(
			String processDefinitionId) throws Exception {
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(processDefinitionId);
		Deployment deployment = repositoryService.createDeploymentQuery()
				.deploymentId(processDefinition.getDeploymentId())
				.singleResult();
		List<ActivityImpl> activitiList = processDefinition.getActivities();
		List<Map<String, Object>> activities = new ArrayList<Map<String, Object>>();
		boolean offset = deployment != null && deployment.getName() != null
				&& !deployment.getName().endsWith(".zip");
		for (ActivityImpl activity : activitiList) {
			Map<String, Object> activityImageInfo = packageSingleActivitiInfo(
					activity, null, false, offset);
			activities.add(activityImageInfo);
		}
		return activities;
	}

	public List<Map<String, Object>> traceProcessInstance(
			String processInstanceId) throws Exception {
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		Execution execution = runtimeService.createExecutionQuery()
				.executionId(processInstanceId).singleResult();
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(processInstance
						.getProcessDefinitionId());
		Deployment deployment = repositoryService.createDeploymentQuery()
				.deploymentId(processDefinition.getDeploymentId())
				.singleResult();
		List<ActivityImpl> activitiList = processDefinition.getActivities();
		List<Map<String, Object>> activities = new ArrayList<Map<String, Object>>();
		for (ActivityImpl activity : activitiList) {
			boolean current = false;
			String id = activity.getId();
			if (id.equals(execution.getActivityId()))
				current = true;
			Map<String, Object> activityImageInfo = packageSingleActivitiInfo(
					activity, processInstance, current, deployment != null
							&& deployment.getName() != null
							&& !deployment.getName().endsWith(".zip"));
			activities.add(activityImageInfo);
		}
		return activities;
	}

	private Map<String, Object> packageSingleActivitiInfo(
			ActivityImpl activity, ProcessInstance processInstance,
			boolean current, boolean offset) throws Exception {
		Map<String, Object> vars = new HashMap<String, Object>();
		Map<String, Object> activityInfo = new HashMap<String, Object>();
		activityInfo.put("current", current);
		setPosition(activity, activityInfo, offset);
		setWidthAndHeight(activity, activityInfo);

		Map<String, Object> properties = activity.getProperties();
		String type = (String) properties.get("type");
		vars.put("任务类型", LocalizedTextUtil.findText(getClass(), type,
				ActionContext.getContext().getLocale(), type, null));
		ActivityBehavior activityBehavior = activity.getActivityBehavior();
		if (activityBehavior instanceof UserTaskActivityBehavior) {
			Task currentTask = null;
			if (current) {
				currentTask = getCurrentTaskInfo(processInstance);
				setCurrentTaskAssignee(vars, currentTask);
			}
			UserTaskActivityBehavior userTaskActivityBehavior = (UserTaskActivityBehavior) activityBehavior;
			TaskDefinition taskDefinition = userTaskActivityBehavior
					.getTaskDefinition();
			setTaskGroup(vars, taskDefinition);

		}
		vars.put("节点说明", properties.get("documentation"));
		String description = activity.getProcessDefinition().getDescription();
		vars.put("描述", description);
		activityInfo.put("vars", vars);
		return activityInfo;
	}

	private void setTaskGroup(Map<String, Object> vars,
			TaskDefinition taskDefinition) {
		Set<Expression> candidateGroupIdExpressions = taskDefinition
				.getCandidateGroupIdExpressions();
		StringBuilder roles = new StringBuilder();
		for (Expression expression : candidateGroupIdExpressions) {
			String expressionText = expression.getExpressionText();
			if (expressionText.indexOf("${") < 0) {
				appendRoles(roles, expressionText);
			}
		}
		if (roles.length() > 0) {
			roles.deleteCharAt(roles.length() - 1);
			vars.put("任务所属角色", roles.toString());
		}
	}

	private void appendRoles(StringBuilder roles, String text) {
		String[] groups = text.split("\\s*,\\s*");
		for (String s : groups) {
			appendRole(roles, s);
		}
	}

	private void appendRole(StringBuilder roles, String role) {
		Group g = identityService.createGroupQuery().groupId(role)
				.singleResult();
		if (g != null) {
			String roleName = g.getName();
			if (roleName == null) {
				roleName = g.getId();
				roleName = LocalizedTextUtil.findText(getClass(), roleName,
						ActionContext.getContext().getLocale(), roleName, null);
			}
			roles.append(roleName).append(" ");
		}
	}

	private void setCurrentTaskAssignee(Map<String, Object> vars,
			Task currentTask) {
		String assignee = currentTask.getAssignee();
		if (assignee != null) {
			User assigneeUser = identityService.createUserQuery()
					.userId(assignee).singleResult();
			try {
				UserDetails userDetails = userDetailsService
						.loadUserByUsername(assigneeUser.getId());
				vars.put("当前处理人", userDetails.toString());
			} catch (UsernameNotFoundException e) {
				vars.put("当前处理人", assigneeUser.getFirstName());
			}
		}
	}

	private Task getCurrentTaskInfo(ProcessInstance processInstance) {
		Task currentTask = null;
		try {
			String activitiId = processInstance.getActivityId();
			currentTask = taskService.createTaskQuery()
					.processInstanceId(processInstance.getId())
					.taskDefinitionKey(activitiId).singleResult();
		} catch (Exception e) {
			logger.error(
					"can not get property activityId from processInstance: {}",
					processInstance);
		}
		return currentTask;
	}

	private void setWidthAndHeight(ActivityImpl activity,
			Map<String, Object> activityInfo) {
		activityInfo.put("width", activity.getWidth());
		activityInfo.put("height", activity.getHeight());
	}

	private void setPosition(ActivityImpl activity,
			Map<String, Object> activityInfo, boolean offset) {
		activityInfo.put("x", offset ? activity.getX() - 164 : activity.getX());
		activityInfo.put("y", offset ? activity.getY() - 134 : activity.getY());
	}
}