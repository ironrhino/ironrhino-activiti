<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>任务列表</title>
</head>
<body>
<#assign columns={
"historicProcessInstance.id":{"alias":"流程ID","width":"100px"},
"processDefinition.name":{"alias":"流程名"},
"historicProcessInstance.businessKey":{"alias":"流程业务KEY","width":"100px"},
"historicProcessInstance.startUserId":{"alias","startUser","width":"80px","template":r'<#if value?has_content><span class="user" data-username="${value}">${beans["userDetailsService"].loadUserByUsername(value,true)!}</span></#if>'},
"historicProcessInstance.startTime":{"alias":"发起时间","width":"130px"},
"task.name":{"alias":"任务名","width":"100px"},
"task.assignee":{"width":"80px","template":r'<#if value?has_content><span class="user" data-username="${value}">${beans["userDetailsService"].loadUserByUsername(value,true)!}</span></#if>'},
"task.createTime":{"alias":"任务创建时间","width":"130px"},
"task.dueDate":{"width":"80px"},
"task.suspended":{"width":"60px"}}>
<#assign actionColumnButtons=r'
<#if !entity.task.suspended>
<@btn view="reassign"/>
</#if>
'>
<#assign bottomButtons=r'
<button type="button" class="btn confirm" data-action="delete" data-shown="selected">${getText("delete")}</button>
<@btn class="reload"/>
<@btn class="filter"/>
'>
<#assign rowDynamicAttributes=r'{"class":"${entity.task.suspended?then("warning","")}"}'>
<@richtable entityName="task" columns=columns bottomButtons=bottomButtons actionColumnButtons=actionColumnButtons rowDynamicAttributes=rowDynamicAttributes searchable=false celleditable=false/>
<form method="post" class="ajax view criteria form-horizontal" style="display:none;" data-columns="2">
	<@s.textfield label=getText('processDefinitionKey') name="criteria.processDefinitionKey"/>
	<@s.textfield label=getText('processDefinitionName') name="criteria.processDefinitionName"/>
	<@s.textfield label=getText('processDefinitionId') name="criteria.processDefinitionId"/>
	<@s.textfield label=getText('processInstanceId') name="criteria.processInstanceId"/>
	<@s.textfield label=getText('processInstanceBusinessKey') name="criteria.processInstanceBusinessKey"/>
	<@s.textfield label=getText('taskDefinitionKey') name="criteria.taskDefinitionKey"/>
	<@s.textfield label=getText('taskId') name="criteria.taskId"/>
	<@s.textfield label=getText('taskName') name="criteria.taskName"/>
	<div class="control-group listpick" data-options="{'url':'<@url value="/user/pick?columns=username,name&enabled=true"/>','idindex':1,'nameindex':2}">
	<@s.hidden id="criteria_taskInvolvedUser" name="criteria.taskInvolvedUser" class="listpick-id"/>
	<label class="control-label" for="criteria_taskInvolvedUser-control">${getText('involvedUser')}</label>
	<div class="controls">
	<span class="listpick-name"></span>
	</div>
	</div>
	<div class="control-group listpick" data-options="{'url':'<@url value="/user/pick?columns=username,name&enabled=true"/>','idindex':1,'nameindex':2}">
	<@s.hidden id="criteria_taskAssignee" name="criteria.taskAssignee" class="listpick-id"/>
	<label class="control-label" for="criteria_taskAssignee-control">${getText('assignee')}</label>
	<div class="controls">
	<span class="listpick-name"></span>
	</div>
	</div>
	<@s.textfield label=getText('createdBefore') name="criteria.taskCreatedBefore" class="date"/>
	<@s.textfield label=getText('createdAfter') name="criteria.taskCreatedAfter" class="date"/>
	<@s.textfield label=getText('taskDueBefore') name="criteria.taskDueBefore" class="date"/>
	<@s.textfield label=getText('taskDueAfter') name="criteria.taskDueAfter" class="date"/>
	<@s.checkbox label=getText('active') name="criteria.active" class="custom"/>
	<@s.checkbox label=getText('suspended') name="criteria.suspended" class="custom"/>
	<@s.checkbox label=getText('unassigned') name="criteria.taskUnassigned" class="custom"/>
	<@s.select label=getText('delegationState') name="criteria.taskDelegationState" list="@org.activiti.engine.task.DelegationState@values()" headerKey="" headerValue=""/>
  	<div class="row">
		<div class="span12" style="text-align:center;">
		<button type="submit" class="btn btn-primary">${getText('search')}</button> <button type="button" class="btn restore">${getText('restore')}</button>
		</div>
	</div>
</form>
</body>
</html></#escape>