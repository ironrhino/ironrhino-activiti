package org.ironrhino.activiti.model;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Row {

	private String id;

	private Deployment deployment;

	private ProcessDefinition processDefinition;

	private ProcessInstance processInstance;

	private HistoricProcessInstance historicProcessInstance;

	private HistoricActivityInstance historicActivityInstance;

	private Task task;

}
