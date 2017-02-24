package org.ironrhino.activiti.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.DelegationState;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.ironrhino.activiti.form.CheckboxFormType;
import org.ironrhino.core.sequence.CyclicSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FormSubmissionService {

	public static final String PARAMETER_NAME_PROCESS_DEFINITION_ID = "processDefinitionId";
	public static final String PARAMETER_NAME_COMMENT = "_comment_";
	public static final String PARAMETER_NAME_ATTACHMENT = "_attachment_";
	public static final String PARAMETER_NAME_ATTACHMENT_DESCRIPTION = "_attachment_description_";

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FormService formService;

	@Autowired
	private TaskService taskService;

	@Autowired(required = false)
	@Qualifier("businessKeySequence")
	private CyclicSequence businessKeySequence;

	@Autowired
	private CheckboxFormType checkboxFormType;

	@Transactional
	public ProcessInstance submitStartForm(String processDefinitionId, Map<String, String[]> parametersMap,
			String[] fileFileName, String[] attachmentDescription, File[] file) throws IOException {
		Map<String, String> properties = getFormProperties(
				formService.getStartFormData(processDefinitionId).getFormProperties(), parametersMap);
		String businessKey = businessKeySequence != null ? businessKeySequence.nextStringValue()
				: String.valueOf(System.currentTimeMillis());
		ProcessInstance processInstance = formService.submitStartFormData(processDefinitionId, businessKey, properties);
		String comment = parametersMap.get(PARAMETER_NAME_COMMENT)[0];
		if (StringUtils.isNotBlank(comment))
			taskService.addComment(null, processInstance.getId(), comment);
		if (fileFileName != null) {
			for (int i = 0; i < file.length; i++) {
				String description = attachmentDescription.length > i ? attachmentDescription[i] : null;
				taskService.createAttachment(null, null, processInstance.getId(), fileFileName[i], description,
						new FileInputStream(file[i]));
			}
		}
		return processInstance;
	}

	@Transactional
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void submitTaskForm(String taskId, Map<String, String[]> parametersMap, String[] fileFileName,
			String[] attachmentDescription, File[] file) throws IOException {
		Map properties = getFormProperties(formService.getTaskFormData(taskId).getFormProperties(), parametersMap);
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		String comment = parametersMap.get(PARAMETER_NAME_COMMENT)[0];
		if (StringUtils.isNotBlank(comment))
			taskService.addComment(task.getId(), task.getProcessInstanceId(), comment);
		if (fileFileName != null) {
			for (int i = 0; i < file.length; i++) {
				String description = attachmentDescription.length > i ? attachmentDescription[i] : null;
				taskService.createAttachment(null, task.getId(), task.getProcessInstanceId(), fileFileName[i],
						description, new FileInputStream(file[i]));
			}
		}
		DelegationState delegationState = task.getDelegationState();
		if (DelegationState.PENDING == delegationState) {
			taskService.resolveTask(taskId, (Map) properties);
		} else {
			formService.submitTaskFormData(taskId, properties);
		}
	}

	private Map<String, String> getFormProperties(List<FormProperty> formProperties,
			Map<String, String[]> parametersMap) {
		Map<String, String> properties = new HashMap<>();
		for (FormProperty fp : formProperties) {
			if (!fp.isWritable())
				continue;
			String[] values = parametersMap.get(fp.getId());
			if (values == null)
				continue;
			if (fp.getType() instanceof CheckboxFormType) {
				properties.put(fp.getId(), checkboxFormType.convertModelValueToFormValue(values));
			} else {
				properties.put(fp.getId(), values[0]);
			}
		}
		for (Map.Entry<String, String[]> entry : parametersMap.entrySet()) {
			String name = entry.getKey();
			if (!properties.containsKey(name) && !name.equals(PARAMETER_NAME_PROCESS_DEFINITION_ID)
					&& !name.equals(PARAMETER_NAME_COMMENT) && !name.equals(PARAMETER_NAME_ATTACHMENT)
					&& !name.equals(PARAMETER_NAME_ATTACHMENT_DESCRIPTION))
				properties.put(name, entry.getValue()[0]);
		}
		return properties;
	}

}