package org.ironrhino.activiti.model;

import java.io.Serializable;
import java.util.Date;

import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;

public class TaskQueryCriteria implements Serializable {

	private static final long serialVersionUID = -3473696529171033192L;

	private String processDefinitionId;

	private String processDefinitionKey;

	private String processDefinitionName;

	private String processInstanceId;

	private String processInstanceBusinessKey;

	private String taskId;

	private String taskName;

	private String taskDefinitionKey;

	private DelegationState taskDelegationState;

	private Boolean active;

	private Boolean suspended;

	private Boolean taskUnassigned;

	private Date taskCreatedBefore;

	private Date taskCreatedAfter;

	private Date taskDueBefore;

	private Date taskDueAfter;

	private String taskAssignee;

	private String taskInvolvedUser;

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

	public String getProcessDefinitionName() {
		return processDefinitionName;
	}

	public void setProcessDefinitionName(String processDefinitionName) {
		this.processDefinitionName = processDefinitionName;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getProcessInstanceBusinessKey() {
		return processInstanceBusinessKey;
	}

	public void setProcessInstanceBusinessKey(String processInstanceBusinessKey) {
		this.processInstanceBusinessKey = processInstanceBusinessKey;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskDefinitionKey() {
		return taskDefinitionKey;
	}

	public void setTaskDefinitionKey(String taskDefinitionKey) {
		this.taskDefinitionKey = taskDefinitionKey;
	}

	public DelegationState getTaskDelegationState() {
		return taskDelegationState;
	}

	public void setTaskDelegationState(DelegationState taskDelegationState) {
		this.taskDelegationState = taskDelegationState;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getSuspended() {
		return suspended;
	}

	public void setSuspended(Boolean suspended) {
		this.suspended = suspended;
	}

	public Boolean getTaskUnassigned() {
		return taskUnassigned;
	}

	public void setTaskUnassigned(Boolean taskUnassigned) {
		this.taskUnassigned = taskUnassigned;
	}

	public Date getTaskCreatedBefore() {
		return taskCreatedBefore;
	}

	public void setTaskCreatedBefore(Date taskCreatedBefore) {
		this.taskCreatedBefore = taskCreatedBefore;
	}

	public Date getTaskCreatedAfter() {
		return taskCreatedAfter;
	}

	public void setTaskCreatedAfter(Date taskCreatedAfter) {
		this.taskCreatedAfter = taskCreatedAfter;
	}

	public Date getTaskDueBefore() {
		return taskDueBefore;
	}

	public void setTaskDueBefore(Date taskDueBefore) {
		this.taskDueBefore = taskDueBefore;
	}

	public Date getTaskDueAfter() {
		return taskDueAfter;
	}

	public void setTaskDueAfter(Date taskDueAfter) {
		this.taskDueAfter = taskDueAfter;
	}

	public String getTaskAssignee() {
		return taskAssignee;
	}

	public void setTaskAssignee(String taskAssignee) {
		this.taskAssignee = taskAssignee;
	}

	public String getTaskInvolvedUser() {
		return taskInvolvedUser;
	}

	public void setTaskInvolvedUser(String taskInvolvedUser) {
		this.taskInvolvedUser = taskInvolvedUser;
	}

	public TaskQuery filter(TaskQuery query, boolean admin) {
		if (StringUtils.isNotBlank(processDefinitionId))
			query.processDefinitionId(processDefinitionId);
		if (StringUtils.isNotBlank(processDefinitionKey))
			query.processDefinitionKey(processDefinitionKey);
		if (StringUtils.isNotBlank(processDefinitionName))
			query.processDefinitionNameLike("%" + processDefinitionName + "%");
		if (StringUtils.isNotBlank(processInstanceId))
			query.processInstanceId(processInstanceId);
		if (StringUtils.isNotBlank(processInstanceBusinessKey))
			query.processInstanceBusinessKey(processInstanceBusinessKey);
		if (StringUtils.isNotBlank(taskId))
			query.taskId(taskId);
		if (StringUtils.isNotBlank(taskName))
			query.taskNameLike("%" + taskName + "%");
		if (StringUtils.isNotBlank(taskDefinitionKey))
			query.taskDefinitionKey(taskDefinitionKey);
		if (taskDelegationState != null)
			query.taskDelegationState(taskDelegationState);
		if (active != null && active)
			query.active();
		if (suspended != null && suspended)
			query.suspended();
		if (taskUnassigned != null && taskUnassigned)
			query.taskUnassigned();
		if (taskCreatedBefore != null)
			query.taskCreatedBefore(taskCreatedBefore);
		if (taskCreatedAfter != null)
			query.taskCreatedAfter(taskCreatedAfter);
		if (taskDueBefore != null)
			query.taskDueBefore(taskDueBefore);
		if (taskDueAfter != null)
			query.taskDueAfter(taskDueAfter);
		if (admin) {
			if (StringUtils.isNotBlank(taskAssignee))
				query.taskAssignee(taskAssignee);
			if (StringUtils.isNotBlank(taskInvolvedUser))
				query.taskInvolvedUser(taskInvolvedUser);
		}
		return query;
	}

}
