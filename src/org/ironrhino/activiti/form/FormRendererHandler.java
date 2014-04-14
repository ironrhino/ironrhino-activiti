package org.ironrhino.activiti.form;

import java.util.Map;

public interface FormRendererHandler {

	public void handle(Map<String, FormElement> formElements,
			String processDefinitionKey, String taskDefinitionKey);

}
