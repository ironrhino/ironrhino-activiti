package org.ironrhino.activiti.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;

public interface ActivitiService {

	/**
	 * 退回流程
	 */
	public void backProcess(String taskId, String activityId,
			Map<String, Object> variables) throws Exception;

	/**
	 * 取回流程
	 */
	public void callBackProcess(String taskId, String activityId)
			throws Exception;

	/**
	 * 中止流程(特权人直接审批通过等)
	 */
	public void endProcess(String taskId) throws Exception;

	/**
	 * 根据当前任务ID，查询可以驳回的任务节点
	 */
	public List<ActivityImpl> findBackAvtivity(String taskId) throws Exception;

	/**
	 * 查询指定任务节点的最新记录
	 */
	public HistoricActivityInstance findHistoricUserTask(
			ProcessInstance processInstance, String activityId);

	/**
	 * 根据当前节点，查询输出流向是否为并行终点，如果为并行终点，则拼装对应的并行起点ID
	 */
	public String findParallelGatewayId(ActivityImpl activityImpl);

	/**
	 * 根据任务ID获取流程定义
	 */
	public ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(
			String taskId) throws Exception;

	/**
	 * 根据任务ID获取对应的流程实例
	 */
	public ProcessInstance findProcessInstanceByTaskId(String taskId)
			throws Exception;

	/**
	 * 迭代循环流程树结构，查询当前节点可驳回的任务节点
	 */
	public List<ActivityImpl> iteratorBackActivity(String taskId,
			ActivityImpl currActivity, List<ActivityImpl> rtnList,
			List<ActivityImpl> tempList) throws Exception;

	/**
	 * 还原指定活动节点流向
	 */
	public void restoreTransition(ActivityImpl activityImpl,
			List<PvmTransition> oriPvmTransitionList);

	/**
	 * 反向排序list集合，便于驳回节点按顺序显示
	 */
	public List<ActivityImpl> reverList(List<ActivityImpl> list);

	/**
	 * 转办流程
	 */
	public void transferAssignee(String taskId, String userId);

	/**
	 * 流程转向操作
	 */
	public void turnTransition(String taskId, String activityId,
			Map<String, Object> variables) throws Exception;

}