package org.ironrhino.activiti.action;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
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
@SuppressWarnings({ "rawtypes", "unchecked" })
public class HistoricProcessInstanceAction extends BaseAction {

	private static final long serialVersionUID = -6657349245825745444L;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private HistoryService historyService;

	private ResultPage resultPage;

	private HistoricProcessInstance historicProcessInstance;

	public ResultPage getResultPage() {
		return resultPage;
	}

	public void setResultPage(ResultPage resultPage) {
		this.resultPage = resultPage;
	}

	public HistoricProcessInstance getHistoricProcessInstance() {
		return historicProcessInstance;
	}

	public String execute() {
		return started();
	}

	public String started() {
		if (resultPage == null)
			resultPage = new ResultPage<Tuple<HistoricProcessInstance, ProcessDefinition>>();
		HistoricProcessInstanceQuery query = historyService
				.createHistoricProcessInstanceQuery()
				.variableValueEquals("applyUserId", AuthzUtils.getUsername())
				.excludeSubprocesses(true).finished();
		if (StringUtils.isNotBlank(keyword))
			query = query.processDefinitionKey(keyword);
		long count = query.count();
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
		resultPage.setTotalResults(count);
		resultPage.setResult(list);
		return "started";
	}

	public String involved() {
		if (resultPage == null)
			resultPage = new ResultPage<Tuple<HistoricProcessInstance, ProcessDefinition>>();
		HistoricProcessInstanceQuery query = historyService
				.createHistoricProcessInstanceQuery()
				.involvedUser(AuthzUtils.getUsername())
				.excludeSubprocesses(true).finished();
		if (StringUtils.isNotBlank(keyword))
			query = query.processDefinitionKey(keyword);
		long count = query.count();
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
		resultPage.setTotalResults(count);
		resultPage.setResult(list);
		return "involved";
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
		//TODO check auth
		return VIEW;
	}

}
