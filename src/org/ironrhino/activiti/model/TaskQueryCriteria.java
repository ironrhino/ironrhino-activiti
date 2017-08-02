package org.ironrhino.activiti.model;

import java.io.Serializable;
import java.util.Date;

import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
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
