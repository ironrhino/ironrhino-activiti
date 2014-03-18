package com.demo.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.ironrhino.core.model.BaseEntity;
import org.ironrhino.security.model.User;

@Entity
@Table(name = "`leave`")
public class Leave extends BaseEntity {

	private static final long serialVersionUID = -3509600479976901201L;

	@ManyToOne(optional = false)
	private User user;

	private String processInstanceId;

	@Temporal(TemporalType.DATE)
	private Date startTime;

	@Temporal(TemporalType.DATE)
	private Date endTime;

	@Temporal(TemporalType.DATE)
	private Date realityStartTime;

	@Temporal(TemporalType.DATE)
	private Date realityEndTime;

	private Date applyTime = new Date();

	private String leaveType;

	private String reason;

	@Transient
	private ProcessInstance processInstance;

	@Transient
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
