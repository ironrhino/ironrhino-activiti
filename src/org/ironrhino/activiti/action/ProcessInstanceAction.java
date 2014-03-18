package org.ironrhino.activiti.action;

import java.io.InputStream;
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
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.metadata.JsonConfig;
import org.ironrhino.core.model.ResultPage;
import org.ironrhino.core.struts.BaseAction;
import org.springframework.beans.factory.annotation.Autowired;

@AutoConfig
public class ProcessInstanceAction extends BaseAction {

	private static final long serialVersionUID = -6657349245825745444L;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private ProcessTraceService processTraceService;

	private ResultPage<ProcessInstance> resultPage;

	private ProcessInstance processInstance;

	private String resourceType;

	private String executionId;

	private List<Map<String, Object>> activities;

	public ResultPage<ProcessInstance> getResultPage() {
		return resultPage;
	}

	public void setResultPage(ResultPage<ProcessInstance> resultPage) {
		this.resultPage = resultPage;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public List<Map<String, Object>> getActivities() {
		return activities;
	}

	public String execute() {
		return list();
	}

	public String list() {
		if (resultPage == null)
			resultPage = new ResultPage<ProcessInstance>();
		ProcessInstanceQuery query = runtimeService
				.createProcessInstanceQuery();
		String processDefinitionId = getUid();
		if (StringUtils.isNotBlank(processDefinitionId))
			query = query.processDefinitionId(processDefinitionId);
		long count = query.count();
		List<ProcessInstance> list = query.orderByProcessInstanceId().desc()
				.listPage(resultPage.getStart(), resultPage.getPageSize());
		resultPage.setTotalResults(count);
		resultPage.setResult(list);
		return LIST;
	}

	public String view() {
		processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(getUid()).singleResult();
		if (processInstance == null)
			return NOTFOUND;
		return VIEW;
	}

	public String diagram() throws Exception {
		InputStream resourceAsStream = null;
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery().processInstanceId(getUid())
				.singleResult();
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
}
