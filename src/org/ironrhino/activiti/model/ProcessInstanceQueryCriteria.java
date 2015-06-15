package org.ironrhino.activiti.model;

import java.io.Serializable;

import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.apache.commons.lang3.StringUtils;

public class ProcessInstanceQueryCriteria implements Serializable {

	private static final long serialVersionUID = -3962751725813685648L;

	private String processDefinitionId;

	private String processDefinitionKey;

	private String processDefinitionName;

	private String processInstanceId;

	private String processInstanceBusinessKey;

	private String involvedUser;

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

	public String getInvolvedUser() {
		return involvedUser;
	}

	public void setInvolvedUser(String involvedUser) {
		this.involvedUser = involvedUser;
	}

	public ProcessInstanceQuery filter(ProcessInstanceQuery query, boolean admin) {
		if (StringUtils.isNotBlank(processDefinitionId))
			query.processDefinitionId(processDefinitionId);
		if (StringUtils.isNotBlank(processDefinitionKey))
			query.processDefinitionKey(processDefinitionKey);
		if (StringUtils.isNotBlank(processDefinitionName))
			query.processDefinitionName(processDefinitionName);
		if (StringUtils.isNotBlank(processInstanceId))
			query.processInstanceId(processInstanceId);
		if (StringUtils.isNotBlank(processInstanceBusinessKey))
			query.processInstanceBusinessKey(processInstanceBusinessKey);
		if (admin) {
			if (StringUtils.isNotBlank(involvedUser))
				query.involvedUser(involvedUser);
		}
		return query;
	}
}
