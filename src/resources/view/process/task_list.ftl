<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>任务列表</title>
</head>
<body>
<#assign columns={
"historicProcessInstance.id":{"alias":"流程ID","width":"100px"},
"processDefinition.name":{"alias":"流程名"},
"historicProcessInstance.businessKey":{"alias":"流程业务键值","width":"100px"},
"historicProcessInstance.startUserId":{"alias","startUser","width":"80px","template":r'<#if value?has_content><span class="user" data-username="${value}">${statics["org.ironrhino.core.util.ApplicationContextUtils"].getBean("userDetailsService").loadUserByUsername(value,true)!}</span></#if>'},
"historicProcessInstance.startTime":{"alias":"发起时间","width":"130px"},
"task.name":{"alias":"任务名","width":"100px"},
"task.assignee":{"width":"80px","template":r'<#if value?has_content><span class="user" data-username="${value}">${statics["org.ironrhino.core.util.ApplicationContextUtils"].getBean("userDetailsService").loadUserByUsername(value,true)!}</span></#if>'},
"task.createTime":{"alias":"任务创建时间","width":"130px"},
"task.dueDate":{"width":"80px"},
"task.suspended":{"width":"60px"}}>
<#assign actionColumnButtons=r'
<#if !entity.task.suspended>
<@btn view="reassign"/>
</#if>
'>
<#assign bottomButtons=r'
<button type="button" class="btn confirm" data-action="delete" data-shown="selected">${action.getText("delete")}</button>
<@btn class="reload"/>
<@btn class="filter"/>
'>
<@richtable entityName="task" columns=columns bottomButtons=bottomButtons actionColumnButtons=actionColumnButtons searchable=false celleditable=false/>

<form method="post" class="ajax view criteria form-horizontal" style="display:none;">
<div class="row">
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_processDefinitionKey">${action.getText('processDefinitionKey')}</label>
			<div class="controls">
				<input id="criteria_processDefinitionKey" type="text" name="criteria.processDefinitionKey"/>
			</div>
		</div>
	</div>
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_processDefinitionName">${action.getText('processDefinitionName')}</label>
			<div class="controls">
				<input id="criteria_processDefinitionName" type="text" name="criteria.processDefinitionName"/>
			</div>
		</div>
	</div>
</div>
<div class="row">
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_processDefinitionId">${action.getText('processDefinitionId')}</label>
			<div class="controls">
				<input id="criteria_processDefinitionId" type="text" name="criteria.processDefinitionId"/>
			</div>
		</div>
	</div>
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_processInstanceId">${action.getText('processInstanceId')}</label>
			<div class="controls">
				<input id="criteria_processInstanceId" type="text" name="criteria.processInstanceId"/>
			</div>
		</div>
	</div>
</div>
<div class="row">
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_processInstanceBusinessKey">${action.getText('processInstanceBusinessKey')}</label>
			<div class="controls">
				<input id="criteria_processInstanceBusinessKey" type="text" name="criteria.processInstanceBusinessKey"/>
			</div>
		</div>
	</div>
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_taskDefinitionKey">${action.getText('taskDefinitionKey')}</label>
			<div class="controls">
				<input id="criteria_taskDefinitionKey" type="text" name="criteria.taskDefinitionKey"/>
			</div>
		</div>
	</div>
</div>
<div class="row">
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_taskId">${action.getText('taskId')}</label>
			<div class="controls">
				<input id="criteria_taskId" type="text" name="criteria.taskId"/>
			</div>
		</div>
	</div>
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_taskName">${action.getText('taskName')}</label>
			<div class="controls">
				<input id="criteria_taskName" type="text" name="criteria.taskName"/>
			</div>
		</div>
	</div>
</div>
<div class="row">
	<div class="span6">
		<div class="control-group listpick" data-options="{'url':'<@url value="/user/pick?columns=username,name&enabled=true"/>','idindex':1,'nameindex':2}">
		<@s.hidden id="criteria_taskInvolvedUser" name="criteria.taskInvolvedUser" class="listpick-id"/>
		<label class="control-label" for="criteria_taskInvolvedUser-control">${action.getText('involvedUser')}</label>
		<div class="controls">
		<span class="listpick-name"></span>
		</div>
		</div>
	</div>
	<div class="span6">
		<div class="control-group listpick" data-options="{'url':'<@url value="/user/pick?columns=username,name&enabled=true"/>','idindex':1,'nameindex':2}">
		<@s.hidden id="criteria_taskAssignee" name="criteria.taskAssignee" class="listpick-id"/>
		<label class="control-label" for="criteria_taskAssignee-control">${action.getText('assignee')}</label>
		<div class="controls">
		<span class="listpick-name"></span>
		</div>
		</div>
	</div>
</div>
<div class="row">
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_taskCreatedBefore">${action.getText('createdBefore')}</label>
			<div class="controls">
				<input id="criteria_taskCreatedBefore" type="text" name="criteria.taskCreatedBefore" class="date"/>
			</div>
		</div>
	</div>
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_taskCreatedAfter">${action.getText('createdAfter')}</label>
			<div class="controls">
				<input id="criteria_taskCreatedAfter" type="text" name="criteria.taskCreatedAfter" class="date"/>
			</div>
		</div>
	</div>
</div>
<div class="row">
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_taskDueBefore">${action.getText('taskDueBefore')}</label>
			<div class="controls">
				<input id="criteria_taskDueBefore" type="text" name="criteria.taskDueBefore" class="date"/>
			</div>
		</div>
	</div>
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_taskDueAfter">${action.getText('taskDueAfter')}</label>
			<div class="controls">
				<input id="criteria_taskDueAfter" type="text" name="criteria.taskDueAfter" class="date"/>
			</div>
		</div>
	</div>
</div>
<div class="row">
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_active">${action.getText('active')}</label>
			<div class="controls">
				<input id="criteria_active" type="checkbox" name="criteria.active" value="true" class="custom"/>
			</div>
		</div>
	</div>
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_suspended">${action.getText('suspended')}</label>
			<div class="controls">
				<input id="criteria_suspended" type="checkbox" name="criteria.suspended" value="true" class="custom"/>
			</div>
		</div>
	</div>
</div>
<div class="row">
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_taskUnassigned">${action.getText('unassigned')}</label>
			<div class="controls">
				<input id="criteria_taskUnassigned" type="checkbox" name="criteria.taskUnassigned" value="true" class="custom"/>
			</div>
		</div>
	</div>
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_taskDelegationState">${action.getText('delegationState')}</label>
			<div class="controls">
				<select id="criteria_taskDelegationState" name="criteria.taskDelegationState">
				<option></option>
				<#list statics['org.activiti.engine.task.DelegationState'].values() as en>
				<option value="${en.name()}">${action.getText(en.name())}</option>
				</#list>
				</select>
			</div>
		</div>
	</div>
</div>
<div class="row">
	<div class="span12" style="text-align:center;">
		<button type="submit" class="btn btn-primary">${action.getText('search')}</button> <button type="button" class="btn restore">${action.getText('restore')}</button>
	</div>
</div>
</form>


</body>
</html></#escape>