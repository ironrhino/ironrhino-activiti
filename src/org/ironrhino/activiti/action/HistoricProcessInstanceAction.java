package org.ironrhino.activiti.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.identity.Group;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.ironrhino.activiti.model.ActivityDetail;
import org.ironrhino.activiti.model.HistoricProcessInstanceQueryCriteria;
import org.ironrhino.activiti.model.Row;
import org.ironrhino.activiti.service.ProcessTraceService;
import org.ironrhino.core.metadata.Authorize;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.model.ResultPage;
import org.ironrhino.core.security.role.UserRole;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.core.util.AuthzUtils;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.Setter;

@AutoConfig
@Authorize(ifAnyGranted = UserRole.ROLE_BUILTIN_USER)
public class HistoricProcessInstanceAction extends BaseAction {

	private static final long serialVersionUID = -6657349245825745444L;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private ProcessTraceService processTraceService;

	@Getter
	@Setter
	private ResultPage<Row> resultPage;

	@Getter
	private HistoricProcessInstance historicProcessInstance;

	@Getter
	@Setter
	private String processDefinitionId;

	@Getter
	@Setter
	private Boolean startedBy;

	@Getter
	@Setter
	private Boolean finished;

	@Getter
	private List<ActivityDetail> activityDetails;

	@Getter
	@Setter
	private HistoricProcessInstanceQueryCriteria criteria;

	@Override
	@Authorize(ifAnyGranted = UserRole.ROLE_ADMINISTRATOR)
	public String execute() {
		return list();
	}

	@Authorize(ifAnyGranted = UserRole.ROLE_ADMINISTRATOR)
	public String list() {
		if (resultPage == null)
			resultPage = new ResultPage<Row>();
		HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
		if (StringUtils.isNotBlank(processDefinitionId))
			query.processDefinitionId(processDefinitionId);
		if (finished != null)
			if (finished)
				query.finished();
			else
				query.unfinished();
		if (criteria != null)
			criteria.filter(query, true);
		return doQuery(query);
	}

	public String involved() {
		if (resultPage == null)
			resultPage = new ResultPage<Row>();
		String username = AuthzUtils.getUsername();
		HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
				.excludeSubprocesses(true);
		if (startedBy != null && startedBy)
			query.startedBy(username);
		else
			query.involvedUser(username);
		if (finished != null)
			if (finished)
				query.finished();
			else
				query.unfinished();
		if (criteria != null)
			criteria.filter(query, false);
		return doQuery(query);
	}

	@SuppressWarnings({ "unchecked" })
	private String doQuery(HistoricProcessInstanceQuery query) {
		long count = query.count();
		resultPage.setTotalResults(count);
		if (count > 0) {
			List<HistoricProcessInstance> historicProcessInstances = query.orderByProcessInstanceStartTime().desc()
					.listPage(resultPage.getStart(), resultPage.getPageSize());
			List<Row> list = new ArrayList<Row>(historicProcessInstances.size());
			for (HistoricProcessInstance pi : historicProcessInstances) {
				Row row = new Row();
				row.setId(pi.getId());
				row.setHistoricProcessInstance(pi);
				row.setProcessDefinition(repositoryService.createProcessDefinitionQuery()
						.processDefinitionId(pi.getProcessDefinitionId()).singleResult());
				if (pi.getEndTime() == null) {
					List<HistoricActivityInstance> historicActivityInstances = historyService
							.createHistoricActivityInstanceQuery().processInstanceId(pi.getId())
							.orderByHistoricActivityInstanceStartTime().desc().list();
					if (!historicActivityInstances.isEmpty()) {
						for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
							if (historicActivityInstance.getActivityId() != null
									&& historicActivityInstance.getEndTime() == null) {
								row.setHistoricActivityInstance(historicActivityInstance);
								break;
							}
						}
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

	@Override
	public String view() {
		historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(getUid())
				.singleResult();
		if (historicProcessInstance == null)
			historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
					.processInstanceBusinessKey(getUid()).singleResult();
		if (historicProcessInstance == null)
			return NOTFOUND;

		if (!canView(historicProcessInstance.getId()))
			return ACCESSDENIED;
		activityDetails = processTraceService.traceHistoricProcessInstance(historicProcessInstance.getId());
		return VIEW;
	}

	public String trace() {
		historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(getUid())
				.singleResult();
		if (historicProcessInstance == null)
			historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
					.processInstanceBusinessKey(getUid()).singleResult();
		if (historicProcessInstance == null)
			return NOTFOUND;
		return canView(historicProcessInstance.getId()) ? "trace" : ACCESSDENIED;
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

		List<Task> tasks = taskService.createTaskQuery().active().processInstanceId(processInstanceId).list();
		for (Task task : tasks) {
			if (task.getAssignee() != null)
				continue;
			List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
			for (IdentityLink identityLink : identityLinks) {
				if (identityLink.getType().equals("candidate")) {
					if (userId.equals(identityLink.getUserId()))
						return true;
					String groupId = identityLink.getGroupId();
					if (groupId != null) {
						Group group = identityService.createGroupQuery().groupId(groupId).groupMember(userId)
								.singleResult();
						if (group != null)
							return true;
					}
				}
			}
		}
		return false;
	}

}
