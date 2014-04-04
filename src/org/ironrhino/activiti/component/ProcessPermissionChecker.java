package org.ironrhino.activiti.component;

public interface ProcessPermissionChecker {

	public boolean canStart(String processDefinitionKey);

}
