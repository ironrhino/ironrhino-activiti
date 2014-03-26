package org.ironrhino.process.model;

import java.io.Serializable;
import java.util.Date;

import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.apache.commons.lang3.StringUtils;

public class HistoricProcessInstanceQueryCriteria implements Serializable {

	private static final long serialVersionUID = -3473696529171033192L;

	private String processDefinitionId;

	private String processDefinitionKey;

	private String processInstanceId;

	private String processInstanceBusinessKey;

	private String involvedUser;

	private String startedBy;

	private Date startedBefore;

	private Date startedAfter;

	private Date finishedBefore;

	private Date finishedAfter;

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

	public String getStartedBy() {
		return startedBy;
	}

	public void setStartedBy(String startedBy) {
		this.startedBy = startedBy;
	}

	public Date getStartedBefore() {
		return startedBefore;
	}

	public void setStartedBefore(Date startedBefore) {
		this.startedBefore = startedBefore;
	}

	public Date getStartedAfter() {
		return startedAfter;
	}

	public void setStartedAfter(Date startedAfter) {
		this.startedAfter = startedAfter;
	}

	public Date getFinishedBefore() {
		return finishedBefore;
	}

	public void setFinishedBefore(Date finishedBefore) {
		this.finishedBefore = finishedBefore;
	}

	public Date getFinishedAfter() {
		return finishedAfter;
	}

	public void setFinishedAfter(Date finishedAfter) {
		this.finishedAfter = finishedAfter;
	}

	public HistoricProcessInstanceQuery filter(
			HistoricProcessInstanceQuery query, boolean admin) {
		if (StringUtils.isNotBlank(processDefinitionId))
			query.processDefinitionId(processDefinitionId);
		if (StringUtils.isNotBlank(processDefinitionKey))
			query.processDefinitionKey(processDefinitionKey);
		if (StringUtils.isNotBlank(processInstanceId))
			query.processInstanceId(processInstanceId);
		if (StringUtils.isNotBlank(processInstanceBusinessKey))
			query.processInstanceBusinessKey(processInstanceBusinessKey);
		if (admin) {
			if (StringUtils.isNotBlank(involvedUser))
				query.involvedUser(involvedUser);
			if (StringUtils.isNotBlank(startedBy))
				query.startedBy(startedBy);
		}
		if (startedBefore != null)
			query.startedBefore(startedBefore);
		if (startedAfter != null)
			query.startedAfter(startedAfter);
		if (finishedBefore != null)
			query.finishedBefore(finishedBefore);
		if (finishedAfter != null)
			query.finishedAfter(finishedAfter);
		return query;
	}

}
