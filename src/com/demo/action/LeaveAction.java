package com.demo.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.service.EntityManager;
import org.ironrhino.core.struts.BaseAction;
import org.ironrhino.core.util.AuthzUtils;
import org.ironrhino.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import com.demo.model.Leave;
import com.demo.service.LeaveService;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import com.opensymphony.xwork2.validator.annotations.ValidatorType;

@AutoConfig
public class LeaveAction extends BaseAction {

	private static final long serialVersionUID = 314143213105332544L;

	@Resource
	private EntityManager<Leave> entityManager;

	@Autowired
	private LeaveService leaveService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private TaskService taskService;

	private Leave leave;

	private List<Leave> list;

	public Leave getLeave() {
		return leave;
	}

	public void setLeave(Leave leave) {
		this.leave = leave;
	}

	public List<Leave> getList() {
		return list;
	}

	@InputConfig(resultName = "start")
	@Validations(requiredFields = {
			@RequiredFieldValidator(type = ValidatorType.FIELD, fieldName = "leave.startTime", key = "validation.required"),
			@RequiredFieldValidator(type = ValidatorType.FIELD, fieldName = "leave.endTime", key = "validation.required") })
	public String start() {
		User user = AuthzUtils.getUserDetails();
		Leave temp = leave;
		Leave leave = new Leave();
		leave.setUser(user);
		leave.setStartTime(temp.getStartTime());
		leave.setEndTime(temp.getEndTime());
		leave.setReason(temp.getReason());
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("to", user.getEmail());
		entityManager.save(leave);
		String businessKey = leave.getId().toString();
		identityService.setAuthenticatedUserId(user.getId());
		ProcessInstance processInstance = runtimeService
				.startProcessInstanceByKey("leave", businessKey, variables);
		leave.setProcessInstanceId(processInstance.getId());
		entityManager.save(leave);
		addActionMessage("流程已启动，流程ID：" + processInstance.getId());
		return SUCCESS;
	}

	public String tasks() {
		list = leaveService.findTask(AuthzUtils.getUsername(), "leave");
		return "tasks";
	}

	public String claim() {
		taskService.claim(getUid(), AuthzUtils.getUsername());
		return tasks();
	}
}
