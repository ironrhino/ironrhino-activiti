package com.demo.service;

import java.util.List;

import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.repository.ProcessDefinition;

import com.demo.model.Leave;

public interface LeaveService {

	public List<Leave> findTask(String userid, String processDefinitionKey);

	public List<Leave> findRunningProcessInstaces(String processDefinitionKey);

	public List<Leave> findFinishedProcessInstaces(String processDefinitionKey);

	public ProcessDefinition getProcessDefinition(String processDefinitionId);

	public TaskEntity findTaskById(String taskId) throws Exception;

}
