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
public class ReportBackEndProcessor implements TaskListener {

	private static final long serialVersionUID = 3124655854713421538L;

	@Autowired
	private LeaveManager leaveManager;

	@Autowired
	RuntimeService runtimeService;

	@Override
	public void notify(DelegateTask delegateTask) {
		String processInstanceId = delegateTask.getProcessInstanceId();
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		Leave leave = leaveManager.findOne(processInstance.getBusinessKey());
		leave.setRealityStartTime((Date) delegateTask.getVariable("realityStartTime"));
		leave.setRealityEndTime((Date)  delegateTask.getVariable("realityEndTime"));
		leaveManager.save(leave);
	}

}
