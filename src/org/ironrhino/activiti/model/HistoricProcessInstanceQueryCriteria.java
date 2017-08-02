package org.ironrhino.activiti.model;

import java.io.Serializable;
import java.util.Date;

import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
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

	public HistoricProcessInstanceQuery filter(HistoricProcessInstanceQuery query, boolean admin) {
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
