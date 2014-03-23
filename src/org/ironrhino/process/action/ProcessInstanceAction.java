package org.ironrhino.process.action;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.ironrhino.core.metadata.Authorize;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.metadata.JsonConfig;
import org.ironrhino.core.model.ResultPage;
import org.ironrhino.core.security.role.UserRole;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.core.util.AuthzUtils;
import org.ironrhino.process.service.ProcessTraceService;
import org.springframework.beans.factory.annotation.Autowired;

@AutoConfig
@Authorize(ifAnyGranted = UserRole.ROLE_BUILTIN_USER)
public class ProcessInstanceAction extends BaseAction {

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

	private ProcessInstance processInstance;

	private List<Map<String, Object>> activities;

	public ResultPage<Row> getResultPage() {
		return resultPage;
	}

	public void setResultPage(ResultPage<Row> resultPage) {
		this.resultPage = resultPage;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public List<Map<String, Object>> getActivities() {
		return activities;
	}

	public String execute() {
		return list();
	}

	public String list() {
		if (resultPage == null)
			resultPage = new ResultPage<Row>();
		ProcessInstanceQuery query = runtimeService
				.createProcessInstanceQuery();
		if (!AuthzUtils.authorize(null, UserRole.ROLE_ADMINISTRATOR, null))
			query.involvedUser(AuthzUtils.getUsername());
		String processDefinitionId = getUid();
		if (StringUtils.isNotBlank(processDefinitionId))
			query = query.processDefinitionId(processDefinitionId);
		long count = query.count();
		resultPage.setTotalResults(count);
		if (count > 0) {
			List<ProcessInstance> processInstances = query
					.orderByProcessInstanceId().desc()
					.listPage(resultPage.getStart(), resultPage.getPageSize());
			List<Row> list = new ArrayList<Row>(processInstances.size());
			for (ProcessInstance pi : processInstances) {
				Row row = new Row();
				row.setId(pi.getId());
				row.setProcessInstance(pi);
				row.setHistoricProcessInstance(historyService
						.createHistoricProcessInstanceQuery()
						.processInstanceId(pi.getProcessInstanceId())
						.singleResult());
				row.setProcessDefinition(repositoryService
						.createProcessDefinitionQuery()
						.processDefinitionId(pi.getProcessDefinitionId())
						.singleResult());
				String activityId = pi.getActivityId();
				if (activityId != null)
					row.setHistoricActivityInstance(historyService
							.createHistoricActivityInstanceQuery()
							.executionId(pi.getId()).activityId(activityId)
							.singleResult());
				list.add(row);
			}
			resultPage.setResult(list);
		}
		return LIST;
	}

	public String view() {
		processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(getUid()).singleResult();
		if (processInstance == null)
			processInstance = runtimeService.createProcessInstanceQuery()
					.processInstanceBusinessKey(getUid()).singleResult();
		if (processInstance == null)
			return NOTFOUND;
		return canView(processInstance.getId()) ? VIEW : ACCESSDENIED;
	}

	public String diagram() throws Exception {
		if (!canView(getUid()))
			return NOTFOUND;
		InputStream resourceAsStream = null;
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery().processInstanceId(getUid())
				.singleResult();
		if (processInstance == null)
			processInstance = runtimeService.createProcessInstanceQuery()
					.processInstanceId(getUid()).singleResult();
		if (processInstance == null)
			processInstance = runtimeService.createProcessInstanceQuery()
					.processInstanceBusinessKey(getUid()).singleResult();
		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionId(processInstance.getProcessDefinitionId())
				.singleResult();
		String resourceName = processDefinition.getDiagramResourceName();
		resourceAsStream = repositoryService.getResourceAsStream(
				processDefinition.getDeploymentId(), resourceName);
		runtimeService.getActiveActivityIds(processInstance.getId());
		byte[] byteArray = IOUtils.toByteArray(resourceAsStream);
		ServletOutputStream servletOutputStream = ServletActionContext
				.getResponse().getOutputStream();
		servletOutputStream.write(byteArray, 0, byteArray.length);
		servletOutputStream.flush();
		servletOutputStream.close();
		return NONE;
	}

	@JsonConfig(root = "activities")
	public String trace() throws Exception {
		if (!canView(getUid()))
			return NOTFOUND;
		activities = processTraceService.traceProcessInstance(getUid());
		return JSON;
	}

	@Authorize(ifAnyGranted = UserRole.ROLE_ADMINISTRATOR)
	public String suspend() throws Exception {
		runtimeService.suspendProcessInstanceById(getUid());
		return SUCCESS;
	}

	@Authorize(ifAnyGranted = UserRole.ROLE_ADMINISTRATOR)
	public String activate() throws Exception {
		runtimeService.activateProcessInstanceById(getUid());
		return SUCCESS;
	}

	private boolean canView(String processInstanceId) {
		String userId = AuthzUtils.getUsername();
		List<IdentityLink> identityLinks = runtimeService
				.getIdentityLinksForProcessInstance(processInstanceId);
		for (IdentityLink identityLink : identityLinks)
			if (userId.equals(identityLink.getUserId()))
				return true;
		return false;
	}
}
