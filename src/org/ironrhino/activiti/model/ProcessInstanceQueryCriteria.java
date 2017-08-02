package org.ironrhino.activiti.model;

import java.io.Serializable;

import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
public class ProcessInstanceQueryCriteria implements Serializable {

	private static final long serialVersionUID = -3962751725813685648L;

	private String processDefinitionId;

	private String processDefinitionKey;

	private String processDefinitionName;

	private String processInstanceId;

	private String processInstanceBusinessKey;

	private String involvedUser;

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
