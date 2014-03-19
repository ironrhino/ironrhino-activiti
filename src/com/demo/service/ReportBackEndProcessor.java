package com.demo.service;

import java.util.Date;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.demo.model.Leave;

@Component
@Transactional
public class ReportBackEndProcessor implements TaskListener {

	private static final long serialVersionUID = 3124655854713421538L;

	@Autowired
	private LeaveManager leaveManager;

	@Autowired
	RuntimeService runtimeService;

	public void notify(DelegateTask delegateTask) {
		String processInstanceId = delegateTask.getProcessInstanceId();
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		Leave leave = leaveManager.get(processInstance.getBusinessKey());

		Object realityStartTime = delegateTask.getVariable("realityStartTime");
		leave.setRealityStartTime((Date) realityStartTime);

		Object realityEndTime = delegateTask.getVariable("realityEndTime");
		leave.setRealityEndTime((Date) realityEndTime);

		leaveManager.save(leave);
	}

}
