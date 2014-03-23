package org.ironrhino.activiti.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.metadata.Authorize;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.model.ResultPage;
import org.ironrhino.core.security.role.UserRole;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.core.util.AuthzUtils;
import org.springframework.beans.factory.annotation.Autowired;

@AutoConfig
@Authorize(ifAnyGranted = UserRole.ROLE_BUILTIN_USER)
public class HistoricProcessInstanceAction extends BaseAction {

	private static final long serialVersionUID = -6657349245825745444L;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private HistoryService historyService;

	private ResultPage<Tuple<HistoricProcessInstance, ProcessDefinition>> resultPage;

	private HistoricProcessInstance historicProcessInstance;

	private String processDefinitionId;

	private Boolean startedBy;

	private Boolean finished;

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public Boolean getStartedBy() {
		return startedBy;
	}

	public void setStartedBy(Boolean startedBy) {
		this.startedBy = startedBy;
	}

	public Boolean getFinished() {
		return finished;
	}

	public void setFinished(Boolean finished) {
		this.finished = finished;
	}

	public ResultPage<Tuple<HistoricProcessInstance, ProcessDefinition>> getResultPage() {
		return resultPage;
	}

	public void setResultPage(
			ResultPage<Tuple<HistoricProcessInstance, ProcessDefinition>> resultPage) {
		this.resultPage = resultPage;
	}

	public HistoricProcessInstance getHistoricProcessInstance() {
		return historicProcessInstance;
	}

	@Authorize(ifAnyGranted = UserRole.ROLE_ADMINISTRATOR)
	public String execute() {
		return list();
	}

	@Authorize(ifAnyGranted = UserRole.ROLE_ADMINISTRATOR)
	public String list() {
		if (resultPage == null)
			resultPage = new ResultPage<Tuple<HistoricProcessInstance, ProcessDefinition>>();
		HistoricProcessInstanceQuery query = historyService
				.createHistoricProcessInstanceQuery();
		if (StringUtils.isNoneBlank(processDefinitionId))
			query.processDefinitionId(processDefinitionId);
		if (StringUtils.isNotBlank(keyword))
			query = query.processDefinitionKey(keyword);
		return doQuery(query);
	}

	public String involved() {
		if (resultPage == null)
			resultPage = new ResultPage<Tuple<HistoricProcessInstance, ProcessDefinition>>();
		String username = AuthzUtils.getUsername();
		HistoricProcessInstanceQuery query = historyService
				.createHistoricProcessInstanceQuery().excludeSubprocesses(true);
		if (startedBy != null && startedBy)
			query.startedBy(username);
		else
			query.involvedUser(username);
		if (finished != null)
			if (finished)
				query.finished();
			else
				query.unfinished();
		if (StringUtils.isNotBlank(keyword))
			query = query.processDefinitionKey(keyword);
		return doQuery(query);
	}

	@SuppressWarnings({ "unchecked" })
	private String doQuery(HistoricProcessInstanceQuery query) {
		long count = query.count();
		resultPage.setTotalResults(count);
		if (count > 0) {
			List<HistoricProcessInstance> historicProcessInstances = query
					.orderByProcessInstanceStartTime().desc()
					.listPage(resultPage.getStart(), resultPage.getPageSize());
			List<Tuple<HistoricProcessInstance, ProcessDefinition>> list = new ArrayList<Tuple<HistoricProcessInstance, ProcessDefinition>>(
					historicProcessInstances.size());
			for (HistoricProcessInstance pi : historicProcessInstances) {
				Tuple<HistoricProcessInstance, ProcessDefinition> tuple = new Tuple<HistoricProcessInstance, ProcessDefinition>();
				tuple.setId(pi.getId());
				tuple.setKey(pi);
				tuple.setValue(repositoryService.createProcessDefinitionQuery()
						.processDefinitionId(pi.getProcessDefinitionId())
						.singleResult());
				list.add(tuple);
			}
			resultPage.setResult(list);
		} else {
			resultPage.setResult(Collections.EMPTY_LIST);
		}
		return LIST;
	}

	public String view() {
		historicProcessInstance = historyService
				.createHistoricProcessInstanceQuery()
				.processInstanceId(getUid()).singleResult();
		if (historicProcessInstance == null)
			historicProcessInstance = historyService
					.createHistoricProcessInstanceQuery()
					.processInstanceBusinessKey(getUid()).singleResult();
		if (historicProcessInstance == null)
			return NOTFOUND;
		return canView(historicProcessInstance.getId()) ? VIEW : ACCESSDENIED;
	}

	public String trace() {
		historicProcessInstance = historyService
				.createHistoricProcessInstanceQuery()
				.processInstanceId(getUid()).singleResult();
		if (historicProcessInstance == null)
			historicProcessInstance = historyService
					.createHistoricProcessInstanceQuery()
					.processInstanceBusinessKey(getUid()).singleResult();
		if (historicProcessInstance == null)
			return NOTFOUND;
		return canView(historicProcessInstance.getId()) ? "trace"
				: ACCESSDENIED;
	}

	private boolean canView(String processInstanceId) {
		String userId = AuthzUtils.getUsername();
		List<HistoricIdentityLink> historicIdentityLinks = historyService
				.getHistoricIdentityLinksForProcessInstance(processInstanceId);
		for (HistoricIdentityLink historicIdentityLink : historicIdentityLinks)
			if (userId.equals(historicIdentityLink.getUserId()))
				return true;
		return false;
	}

}
