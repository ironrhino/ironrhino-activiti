package org.ironrhino.process.action;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public class Row {

	private String id;

	private Deployment deployment;

	private ProcessDefinition processDefinition;

	private ProcessInstance processInstance;

	private HistoricProcessInstance historicProcessInstance;

	private HistoricActivityInstance historicActivityInstance;

	private Task task;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Deployment getDeployment() {
		return deployment;
	}

	public void setDeployment(Deployment deployment) {
		this.deployment = deployment;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public HistoricProcessInstance getHistoricProcessInstance() {
		return historicProcessInstance;
	}

	public void setHistoricProcessInstance(
			HistoricProcessInstance historicProcessInstance) {
		this.historicProcessInstance = historicProcessInstance;
	}

	public HistoricActivityInstance getHistoricActivityInstance() {
		return historicActivityInstance;
	}

	public void setHistoricActivityInstance(
			HistoricActivityInstance historicActivityInstance) {
		this.historicActivityInstance = historicActivityInstance;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
