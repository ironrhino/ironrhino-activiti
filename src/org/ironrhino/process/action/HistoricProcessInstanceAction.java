package org.ironrhino.process.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.ironrhino.core.metadata.Authorize;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.model.ResultPage;
import org.ironrhino.core.security.role.UserRole;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.core.util.AuthzUtils;
import org.ironrhino.process.model.ActivityDetail;
import org.ironrhino.process.model.Row;
import org.ironrhino.process.service.ProcessTraceService;
import org.springframework.beans.factory.annotation.Autowired;

@AutoConfig
@Authorize(ifAnyGranted = UserRole.ROLE_BUILTIN_USER)
public class HistoricProcessInstanceAction extends BaseAction {

	private static final long serialVersionUID = -6657349245825745444L;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private ProcessTraceService processTraceService;

	private ResultPage<Row> resultPage;

	private HistoricProcessInstance historicProcessInstance;

	private String processDefinitionId;

	private Boolean startedBy;

	private Boolean finished;

	private List<ActivityDetail> activityDetails;

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

	public ResultPage<Row> getResultPage() {
		return resultPage;
	}

	public void setResultPage(ResultPage<Row> resultPage) {
		this.resultPage = resultPage;
	}

	public HistoricProcessInstance getHistoricProcessInstance() {
		return historicProcessInstance;
	}

	public List<ActivityDetail> getActivityDetails() {
		return activityDetails;
	}

	@Authorize(ifAnyGranted = UserRole.ROLE_ADMINISTRATOR)
	public String execute() {
		return list();
	}

	@Authorize(ifAnyGranted = UserRole.ROLE_ADMINISTRATOR)
	public String list() {
		if (resultPage == null)
			resultPage = new ResultPage<Row>();
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
			resultPage = new ResultPage<Row>();
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
			List<Row> list = new ArrayList<Row>(historicProcessInstances.size());
			for (HistoricProcessInstance pi : historicProcessInstances) {
				Row row = new Row();
				row.setId(pi.getId());
				row.setHistoricProcessInstance(pi);
				row.setProcessDefinition(repositoryService
						.createProcessDefinitionQuery()
						.processDefinitionId(pi.getProcessDefinitionId())
						.singleResult());
				if (pi.getEndTime() == null) {
					ProcessInstance instance = runtimeService
							.createProcessInstanceQuery()
							.processInstanceId(pi.getId()).singleResult();
					String activityId = instance.getActivityId();
					if (activityId != null) {
						List<HistoricActivityInstance> historicActivityInstances = historyService
								.createHistoricActivityInstanceQuery()
								.executionId(pi.getId()).activityId(activityId)
								.orderByHistoricActivityInstanceStartTime()
								.desc().list();
						if (!historicActivityInstances.isEmpty())
							row.setHistoricActivityInstance(historicActivityInstances
									.get(0));
					}

				}
				list.add(row);
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

		if (!canView(historicProcessInstance.getId()))
			return ACCESSDENIED;
		activityDetails = processTraceService
				.traceHistoricProcessInstance(historicProcessInstance.getId());
		return VIEW;
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
		if (AuthzUtils.authorize(null, UserRole.ROLE_ADMINISTRATOR, null))
			return true;
		String userId = AuthzUtils.getUsername();
		List<HistoricIdentityLink> historicIdentityLinks = historyService
				.getHistoricIdentityLinksForProcessInstance(processInstanceId);
		for (HistoricIdentityLink historicIdentityLink : historicIdentityLinks)
			if (userId.equals(historicIdentityLink.getUserId()))
				return true;
		return false;
	}

}
