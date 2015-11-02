package com.ironrhino.activiti.service;

import java.util.Date;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ironrhino.activiti.model.Leave;

@Component
public class AfterModifyApplyContentProcessor implements TaskListener {

	private static final long serialVersionUID = -523387540206288280L;

	@Autowired
	private LeaveManager leaveManager;

	@Autowired
	RuntimeService runtimeService;

	@Override
	public void notify(DelegateTask delegateTask) {
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(delegateTask.getProcessInstanceId())
				.singleResult();
		Leave leave = leaveManager.findOne(processInstance.getBusinessKey());
		leave.setStartTime((Date) delegateTask.getVariable("startTime"));
		leave.setEndTime((Date) delegateTask.getVariable("endTime"));
		leaveManager.save(leave);
	}

}
