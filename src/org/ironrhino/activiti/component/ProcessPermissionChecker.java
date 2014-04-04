package org.ironrhino.activiti.component;

import org.activiti.engine.repository.ProcessDefinition;

public interface ProcessPermissionChecker {

	public boolean canStart(ProcessDefinition processDefinition);

}
