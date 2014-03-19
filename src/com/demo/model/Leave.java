package com.demo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.ironrhino.core.metadata.AutoConfig;
import org.ironrhino.core.metadata.Hidden;
import org.ironrhino.core.metadata.Owner;
import org.ironrhino.core.metadata.Readonly;
import org.ironrhino.core.metadata.Richtable;
import org.ironrhino.core.metadata.UiConfig;
import org.ironrhino.core.model.BaseEntity;
import org.ironrhino.security.model.User;

@Entity
@Table(name = "`leave`")
@AutoConfig
@Owner(propertyName = "user")
@Richtable(readonly = @Readonly(true), bottomButtons = "<button type='button' class='btn' data-view='input'>请假</button> <button type='button' class='btn reload'>${action.getText('reload')}</button> <button type='button' class='btn filter'>${action.getText('filter')}</button>", order = "applyTime desc")
public class Leave extends BaseEntity {

	private static final long serialVersionUID = -3509600479976901201L;

	@ManyToOne(optional = false)
	@JoinColumn(updatable = false)
	@UiConfig(displayOrder = 1, alias = "申请人", hiddenInList = @Hidden(expression = "!Parameters.user??"), hiddenInInput = @Hidden(true))
	private User user;

	@UiConfig(displayOrder = 2, alias = "流程", listTemplate = "<a href=\"<@url value='/process/processInstance/view/${value}'/>\" target=\"_blank\">跟踪</a>", hiddenInInput = @Hidden(true))
	private String processInstanceId;

	@UiConfig(displayOrder = 3)
	@Temporal(TemporalType.DATE)
	@Column(nullable = false, updatable = false)
	private Date startTime;

	@UiConfig(displayOrder = 4)
	@Temporal(TemporalType.DATE)
	@Column(nullable = false, updatable = false)
	private Date endTime;

	@UiConfig(displayOrder = 5, hiddenInInput = @Hidden(true))
	@Temporal(TemporalType.DATE)
	private Date realityStartTime;

	@UiConfig(displayOrder = 6, hiddenInInput = @Hidden(true))
	@Temporal(TemporalType.DATE)
	private Date realityEndTime;

	@UiConfig(displayOrder = 7, hiddenInInput = @Hidden(true))
	@Column(nullable = false, updatable = false)
	private Date applyTime = new Date();

	@UiConfig(displayOrder = 8, type = "dictionary", templateName = "leaveType")
	@Column(nullable = false, updatable = false)
	private String leaveType;

	@UiConfig(displayOrder = 9, type = "textarea")
	@Column(length = 4000, nullable = false, updatable = false)
	private String reason;

	@Transient
	@UiConfig(hidden = true)
	private ProcessInstance processInstance;

	@Transient
	@UiConfig(hidden = true)
	private Task task;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getRealityStartTime() {
		return realityStartTime;
	}

	public void setRealityStartTime(Date realityStartTime) {
		this.realityStartTime = realityStartTime;
	}

	public Date getRealityEndTime() {
		return realityEndTime;
	}

	public void setRealityEndTime(Date realityEndTime) {
		this.realityEndTime = realityEndTime;
	}

	public Date getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(Date applyTime) {
		this.applyTime = applyTime;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
