package org.ironrhino.activiti.action;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
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

	private String processInstanceId;

	private String resourceType;

	private String executionId;

	private Map<String, Object> activityImageInfo;

	private List<Map<String, Object>> activityInfos;

	public ResultPage<ProcessInstance> getResultPage() {
		return resultPage;
	}

	public void setResultPage(ResultPage<ProcessInstance> resultPage) {
		this.resultPage = resultPage;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
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

	public Map<String, Object> getActivityImageInfo() {
		return activityImageInfo;
	}

	public List<Map<String, Object>> getActivityInfos() {
		return activityInfos;
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

	public String download() throws Exception {
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		ProcessDefinition processDefinition = repositoryService
				.createProcessDefinitionQuery()
				.processDefinitionId(processInstance.getProcessDefinitionId())
				.singleResult();
		String resourceName = "";
		if (resourceType.equals("xml"))
			resourceName = processDefinition.getResourceName();
		else if (resourceType.equals("image"))
			resourceName = processDefinition.getDiagramResourceName();
		if (StringUtils.isBlank(resourceName))
			return NOTFOUND;
		ServletActionContext.getResponse().setHeader("Content-Disposition",
				"attachment;filename=" + resourceName + ";");
		InputStream resourceAsStream = repositoryService.getResourceAsStream(
				processDefinition.getDeploymentId(), resourceName);
		byte[] byteArray = IOUtils.toByteArray(resourceAsStream);
		ServletOutputStream servletOutputStream = ServletActionContext
				.getResponse().getOutputStream();
		servletOutputStream.write(byteArray, 0, byteArray.length);
		servletOutputStream.flush();
		servletOutputStream.close();
		return NONE;
	}

	public String diagram() throws Exception {
		InputStream resourceAsStream = null;
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
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

	@JsonConfig(root = "activityImageInfo")
	public String trace() throws Exception {
		ExecutionEntity execution = (ExecutionEntity) runtimeService
				.createExecutionQuery().processInstanceId(processInstanceId)
				.executionId(executionId).singleResult();
		String activityId = execution.getActivityId();
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(processInstance
						.getProcessDefinitionId());
		List<ActivityImpl> activities = processDefinitionEntity.getActivities();
		activityImageInfo = new HashMap<String, Object>();
		for (ActivityImpl activityImpl : activities) {
			String id = activityImpl.getId();
			if (id.equals(activityId)) {
				activityImageInfo.put("x", activityImpl.getX());
				activityImageInfo.put("y", activityImpl.getY());
				activityImageInfo.put("width", activityImpl.getWidth());
				activityImageInfo.put("height", activityImpl.getHeight());
				break;
			}
		}
		return JSON;
	}

	@JsonConfig(root = "activityInfos")
	public String traceProcess() throws Exception {
		activityInfos = processTraceService.traceProcess(processInstanceId);
		return JSON;
	}
}
