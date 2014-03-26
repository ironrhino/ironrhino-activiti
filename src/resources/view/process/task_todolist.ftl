<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>我的任务</title>
</head>
<body>
<#assign columns={
"historicProcessInstance.id":{"alias":"流程ID","width":"100px"},
"processDefinition.name":{"alias":"流程名"},
"historicProcessInstance.startUserId":{"alias","startUser","width":"80px","template":r'<span class="user" data-username="${value}">${statics["org.ironrhino.core.util.ApplicationContextUtils"].getBean("userManager").loadUserByUsername(value)!}</span>'},
"historicProcessInstance.startTime":{"alias":"发起时间","width":"130px"},
"task.name":{"alias":"任务名","width":"100px"},
"task.createTime":{"alias":"任务创建时间","width":"130px"},
"task.dueDate":{"width":"80px"},
"task.suspended":{"width":"60px"}}>
<#assign actionColumnButtons=r'
<#if !entity.task.suspended>
<#if !entity.task.assignee??>
<button type="button" class="btn" data-action="claim">签收</button>
<#else>
'+'
<button type="button" class="btn" data-view="form" data-windowoptions="{\'width\':\'80%\'}">办理</button>
'+r'
<#if !entity.task.delegationState??>
<button type="button" class="btn" data-view="delegate">${action.getText("delegate")}</button>
<button type="button" class="btn" data-action="unclaim">撤销</button>
</#if>
</#if>
</#if>
'>
<#assign bottomButtons='
<button type="button" class="btn reload">${action.getText("reload")}</button>
<button type="button" class="btn filter">${action.getText("filter")}</button>
'>
<@richtable entityName="task" action="${actionBaseUrl}/todolist" columns=columns bottomButtons=bottomButtons actionColumnButtons=actionColumnButtons searchable=false celleditable=false/>

<form method="post" class="ajax view criteria form-horizontal" style="display:none;">
<style>
	.row [class*="span"] .control-label{
		width: 200px;
		padding-right: 20px;
	}
</style>
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
			<label class="control-label" for="criteria_dueBefore">${action.getText('dueBefore')}</label>
			<div class="controls">
				<input id="criteria_dueBefore" type="text" name="criteria.dueBefore" class="date"/>
			</div>
		</div>
	</div>
	<div class="span6">
		<div class="control-group">
			<label class="control-label" for="criteria_dueAfter">${action.getText('dueAfter')}</label>
			<div class="controls">
				<input id="criteria_dueAfter" type="text" name="criteria.dueAfter" class="date"/>
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