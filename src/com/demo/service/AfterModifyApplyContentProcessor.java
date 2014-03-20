package com.demo.service;

import java.util.Date;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.demo.model.Leave;

@Component
public class AfterModifyApplyContentProcessor implements TaskListener {

	private static final long serialVersionUID = -523387540206288280L;

	private LeaveManager leaveManager;

	@Autowired
	RuntimeService runtimeService;

	public void notify(DelegateTask delegateTask) {
		String processInstanceId = delegateTask.getProcessInstanceId();
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		Leave leave = leaveManager.get(processInstance.getBusinessKey());
		if (leave == null)
			leave = new Leave();
		leave.setLeaveType((String) delegateTask.getVariable("leaveType"));
		leave.setStartTime((Date) delegateTask.getVariable("startTime"));
		leave.setEndTime((Date) delegateTask.getVariable("endTime"));
		leave.setReason((String) delegateTask.getVariable("reason"));
		leaveManager.save(leave);
	}

}
