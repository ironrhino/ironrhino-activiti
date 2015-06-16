<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>我的任务</title>
</head>
<body>
<#assign columns={
"historicProcessInstance.id":{"alias":"流程ID","width":"100px"},
"processDefinition.name":{"alias":"流程名"},
"historicProcessInstance.businessKey":{"alias":"流程业务KEY","width":"100px"},
"historicProcessInstance.startUserId":{"alias","startUser","width":"80px","template":r'<#if value?has_content><span class="user" data-username="${value}">${statics["org.ironrhino.core.util.ApplicationContextUtils"].getBean("userDetailsService").loadUserByUsername(value,true)!}</span></#if>'},
"historicProcessInstance.startTime":{"alias":"发起时间","width":"130px"},
"task.name":{"alias":"任务名","width":"100px"},
"task.createTime":{"alias":"任务创建时间","width":"130px"}}>
<#assign actionColumnButtons=r'
<a class="btn" rel="richtable" href="<@url value="/process/historicProcessInstance/view/${entity.task.processInstanceId}"/>">${action.getText("view")}</a>
'+'
<#if entity.task.assignee??>
<@btn view="form" label="办理" windowoptions="{\'width\':\'80%\'}"/>
'+r'
<#if !entity.task.delegationState??>
<@btn view="delegate"/>
</#if>
</#if>
'>
<#assign bottomButtons='
<button type="button" class="btn confirm" data-action="claim" data-shown="selected" data-filterselector=":not([data-assigned=\'true\'])">${action.getText("claim")}</button>
<button type="button" class="btn confirm" data-action="unclaim" data-shown="selected" data-filterselector="[data-assigned=\'true\']">${action.getText("unclaim")}</button>
'+r'
<@btn class="reload"/>
<@btn class="filter"/>
'>
<@richtable entityName="task" action="${actionBaseUrl}/todolist" columns=columns bottomButtons=bottomButtons actionColumnButtons=actionColumnButtons searchable=false celleditable=false rowDynamicAttributes=r"<#if entity.task.assignee??>{'data-assigned':'true'}</#if>"/>

<form method="post" class="ajax view criteria form-horizontal" style="display:none;">
<div class="row">
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_processDefinitionName">${action.getText('processDefinitionName')}</label>
			<div class="controls">
				<input id="criteria_processDefinitionName" type="text" name="criteria.processDefinitionName"/>
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
	<div class="span12" style="text-align:center;">
		<button type="submit" class="btn btn-primary">${action.getText('search')}</button> <button type="button" class="btn restore">${action.getText('restore')}</button>
	</div>
</div>
</form>

</body>
</html></#escape>