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
"historicProcessInstance.startUserId":{"alias","startUser","width":"100px","template":r'<#if value?has_content><span class="user" data-username="${value}">${beans["userDetailsService"].loadUserByUsername(value,true)!}</span></#if>'},
"historicProcessInstance.startTime":{"alias":"发起时间","width":"130px"},
"task.name":{"alias":"任务名","width":"120px"},
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
<form method="post" class="ajax view criteria form-horizontal" style="display:none;" data-columns="2">
	<@s.textfield label="%{getText('processInstanceId')}" name="criteria.processInstanceId"/>
	<@s.textfield label="%{getText('processDefinitionName')}" name="criteria.processDefinitionName"/>
	<@s.textfield label="%{getText('processInstanceBusinessKey')}" name="criteria.processInstanceBusinessKey"/>
	<@s.textfield label="%{getText('taskName')}" name="criteria.taskName"/>
	<@s.textfield label="%{getText('createdBefore')}" name="criteria.taskCreatedBefore" class="date"/>
	<@s.textfield label="%{getText('createdAfter')}" name="criteria.taskCreatedAfter" class="date"/>
	<@s.textfield label="%{getText('taskDueBefore')}" name="criteria.taskDueBefore" class="date"/>
	<@s.textfield label="%{getText('taskDueAfter')}" name="criteria.taskDueAfter" class="date"/>
	<div class="row">
		<div class="span12" style="text-align:center;">
			<button type="submit" class="btn btn-primary">${action.getText('search')}</button> <button type="button" class="btn restore">${action.getText('restore')}</button>
		</div>
	</div>
</form>
</body>
</html></#escape>