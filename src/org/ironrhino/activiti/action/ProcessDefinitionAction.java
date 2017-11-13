package org.ironrhino.activiti.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletOutputStream;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.ironrhino.activiti.model.Row;
import org.ironrhino.activiti.service.ProcessTraceService;
import org.ironrhino.core.metadata.Authorize;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.metadata.JsonConfig;
import org.ironrhino.core.model.ResultPage;
import org.ironrhino.core.security.role.UserRole;
import org.ironrhino.core.struts.BaseAction;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.Setter;

@AutoConfig(fileupload = "text/xml,application/zip,application/octet-stream")
@Authorize(ifAnyGranted = UserRole.ROLE_ADMINISTRATOR)
public class ProcessDefinitionAction extends BaseAction {

	private static final long serialVersionUID = 8529576691612260306L;

	@Autowired
	private ProcessTraceService processTraceService;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private HistoryService historyService;

	@Setter
	private File file;

	@Setter
	private String fileFileName;

	@Getter
	@Setter
	private ResultPage<Row> resultPage;

	@Getter
	@Setter
	private String deploymentId;

	@Getter
	@Setter
	private String resourceName;

	@Getter
	@Setter
	private String key;

	@Getter
	private ProcessDefinition processDefinition;

	@Getter
	private List<Map<String, Object>> activities;

	@Override
	public String execute() {
		if (resultPage == null)
			resultPage = new ResultPage<Row>();
		ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
		if (StringUtils.isNotBlank(keyword))
			query.processDefinitionNameLike(keyword);
		if (StringUtils.isNotBlank(key))
			query.processDefinitionKey(key);
		long count = query.count();
		resultPage.setTotalResults(count);
		if (count > 0) {
			List<ProcessDefinition> processDefinitions = query.orderByProcessDefinitionKey().asc()
					.orderByProcessDefinitionVersion().desc().listPage(resultPage.getStart(), resultPage.getPageSize());

			List<Row> list = new ArrayList<Row>(processDefinitions.size());
			for (ProcessDefinition pd : processDefinitions) {
				Row row = new Row();
				row.setId(pd.getId());
				row.setProcessDefinition(pd);
				row.setDeployment(
						repositoryService.createDeploymentQuery().deploymentId(pd.getDeploymentId()).singleResult());
				list.add(row);
			}
			resultPage.setResult(list);
		}
		return LIST;
	}

	@Override
	public String view() {
		processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(getUid())
				.singleResult();
		if (processDefinition == null)
			return NOTFOUND;
		return VIEW;
	}

	@Override
	public String delete() {
		String[] id = getId();
		if (id != null) {
			boolean deletable = true;
			for (String processDefinitionId : id) {
				long count = historyService.createHistoricProcessInstanceQuery()
						.processDefinitionId(processDefinitionId).count();
				if (count > 0) {
					deletable = false;
					addActionError(processDefinitionId + " 已经启动过流程了,不能删除!");
					break;
				}
			}
			if (!deletable) {
				return ERROR;
			}
			for (String processDefinitionId : id) {
				deploymentId = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId)
						.singleResult().getDeploymentId();
				repositoryService.deleteDeployment(deploymentId, true);
			}
			addActionMessage(getText("delete.success"));
		}
		return SUCCESS;
	}

	public String upload() throws Exception {
		if (file == null || fileFileName == null) {
			addActionError("请上传zip文件或者xml文件");
			return ERROR;
		}
		if (fileFileName.endsWith(".zip")) {
			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file));
			repositoryService.createDeployment().enableDuplicateFiltering().name(fileFileName)
					.addZipInputStream(zipInputStream).deploy();
		} else if (fileFileName.endsWith(".xml") || fileFileName.endsWith(".bpmn")) {
			repositoryService.createDeployment().enableDuplicateFiltering().name(fileFileName)
					.addInputStream(fileFileName, new FileInputStream(file)).deploy();
		}
		addActionMessage("部署成功");
		return SUCCESS;
	}

	public String download() throws Exception {
		InputStream resourceAsStream = repositoryService.getResourceAsStream(deploymentId, resourceName);
		byte[] byteArray = IOUtils.toByteArray(resourceAsStream);
		ServletOutputStream servletOutputStream = ServletActionContext.getResponse().getOutputStream();
		servletOutputStream.write(byteArray, 0, byteArray.length);
		servletOutputStream.flush();
		servletOutputStream.close();
		return NONE;
	}

	public String diagram() throws Exception {
		InputStream resourceAsStream = null;
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.processDefinitionId(getUid()).singleResult();
		String resourceName = processDefinition.getDiagramResourceName();
		resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
		byte[] byteArray = IOUtils.toByteArray(resourceAsStream);
		ServletOutputStream servletOutputStream = ServletActionContext.getResponse().getOutputStream();
		servletOutputStream.write(byteArray, 0, byteArray.length);
		servletOutputStream.flush();
		servletOutputStream.close();
		return NONE;
	}

	@JsonConfig(root = "activities")
	public String trace() throws Exception {
		activities = processTraceService.traceProcessDefinition(getUid());
		return JSON;
	}

	public String suspend() throws Exception {
		repositoryService.suspendProcessDefinitionById(getUid());
		return SUCCESS;
	}

	public String activate() throws Exception {
		repositoryService.activateProcessDefinitionById(getUid());
		return SUCCESS;
	}

}
