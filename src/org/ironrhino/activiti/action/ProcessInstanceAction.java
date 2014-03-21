package org.ironrhino.activiti.action;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.ironrhino.activiti.service.ProcessTraceService;
import org.ironrhino.core.metadata.Authorize;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.metadata.JsonConfig;
import org.ironrhino.core.model.ResultPage;
import org.ironrhino.core.security.role.UserRole;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.core.util.AuthzUtils;
import org.springframework.beans.factory.annotation.Autowired;

@AutoConfig
@Authorize(ifAnyGranted = UserRole.ROLE_BUILTIN_USER)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ProcessInstanceAction extends BaseAction {

	private static final long serialVersionUID = -6657349245825745444L;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private ProcessTraceService processTraceService;

	private ResultPage resultPage;

	private ProcessInstance processInstance;

	private List<Map<String, Object>> activities;

	public ResultPage getResultPage() {
		return resultPage;
	}

	public void setResultPage(ResultPage resultPage) {
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
			resultPage = new ResultPage<Tuple<ProcessInstance, ProcessDefinition>>();
		ProcessInstanceQuery query = runtimeService
				.createProcessInstanceQuery();
		if (!AuthzUtils.authorize(null, UserRole.ROLE_ADMINISTRATOR, null))
			query.involvedUser(AuthzUtils.getUsername());
		String processDefinitionId = getUid();
		if (StringUtils.isNotBlank(processDefinitionId))
			query = query.processDefinitionId(processDefinitionId);
		long count = query.count();
		List<ProcessInstance> processInstances = query
				.orderByProcessInstanceId().desc()
				.listPage(resultPage.getStart(), resultPage.getPageSize());
		List<Tuple<ProcessInstance, ProcessDefinition>> list = new ArrayList<Tuple<ProcessInstance, ProcessDefinition>>(
				processInstances.size());
		for (ProcessInstance pi : processInstances) {
			Tuple<ProcessInstance, ProcessDefinition> tuple = new Tuple<ProcessInstance, ProcessDefinition>();
			tuple.setId(pi.getId());
			tuple.setKey(pi);
			tuple.setValue(repositoryService.createProcessDefinitionQuery()
					.processDefinitionId(pi.getProcessDefinitionId())
					.singleResult());
			list.add(tuple);
		}
		resultPage.setTotalResults(count);
		resultPage.setResult(list);
		return LIST;
	}

	public String started() {
		if (resultPage == null)
			resultPage = new ResultPage<Tuple<ProcessInstance, ProcessDefinition>>();
		ProcessInstanceQuery query = runtimeService
				.createProcessInstanceQuery()
				.variableValueEquals("applyUserId", AuthzUtils.getUsername())
				.excludeSubprocesses(true);
		if (StringUtils.isNotBlank(keyword))
			query = query.processDefinitionName(keyword);
		long count = query.count();
		List<ProcessInstance> processInstances = query
				.orderByProcessInstanceId().desc()
				.listPage(resultPage.getStart(), resultPage.getPageSize());
		List<Tuple<ProcessInstance, ProcessDefinition>> list = new ArrayList<Tuple<ProcessInstance, ProcessDefinition>>(
				processInstances.size());
		for (ProcessInstance pi : processInstances) {
			Tuple<ProcessInstance, ProcessDefinition> tuple = new Tuple<ProcessInstance, ProcessDefinition>();
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
			resultPage = new ResultPage<Tuple<ProcessInstance, ProcessDefinition>>();
		ProcessInstanceQuery query = runtimeService
				.createProcessInstanceQuery()
				.involvedUser(AuthzUtils.getUsername())
				.excludeSubprocesses(true);
		if (StringUtils.isNotBlank(keyword))
			query = query.processDefinitionName(keyword);
		long count = query.count();
		List<ProcessInstance> processInstances = query
				.orderByProcessInstanceId().desc()
				.listPage(resultPage.getStart(), resultPage.getPageSize());
		List<Tuple<ProcessInstance, ProcessDefinition>> list = new ArrayList<Tuple<ProcessInstance, ProcessDefinition>>(
				processInstances.size());
		for (ProcessInstance pi : processInstances) {
			Tuple<ProcessInstance, ProcessDefinition> tuple = new Tuple<ProcessInstance, ProcessDefinition>();
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
		processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(getUid()).singleResult();
		if (processInstance == null)
			processInstance = runtimeService.createProcessInstanceQuery()
					.processInstanceBusinessKey(getUid()).singleResult();
		if (processInstance == null)
			return NOTFOUND;
		return VIEW;
	}

	public String diagram() throws Exception {
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
}
