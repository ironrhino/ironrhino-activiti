<#ftl output_format='HTML'>
<!DOCTYPE html>
<html>
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
<a class="btn" rel="richtable" href="<@url value="/process/historicProcessInstance/view/${entity.task.processInstanceId}"/>">${getText("view")}</a>
'+'
<#if entity.task.assignee??>
<@btn view="form" label="办理" windowoptions="{\'width\':\'80%\'}"/>
'+r'
<#if !entity.task.delegationState??>
<@btn view="delegate"/>
</#if>
<#else>
<@btn view="claim"/>
</#if>
'>
<#assign bottomButtons='
<button type="button" class="btn confirm" data-action="claim" data-shown="selected" data-filterselector=":not([data-assigned=\'true\'])">${getText("claim")}</button>
<button type="button" class="btn confirm" data-action="unclaim" data-shown="selected" data-filterselector="[data-assigned=\'true\']">${getText("unclaim")}</button>
'+r'
<@btn class="reload"/>
<@btn class="filter"/>
'>
<@richtable entityName="task" columns=columns bottomButtons=bottomButtons actionColumnButtons=actionColumnButtons searchable=false celleditable=false rowDynamicAttributes=r"<#if entity.task.assignee??>{'data-assigned':'true'}</#if>"/>
<form method="post" class="ajax view criteria form-horizontal" style="display:none;" data-columns="2">
	<@s.textfield name="criteria.processInstanceId"/>
	<@s.textfield name="criteria.processDefinitionName"/>
	<@s.textfield name="criteria.processInstanceBusinessKey"/>
	<@s.textfield name="criteria.taskName"/>
	<@s.textfield name="criteria.taskCreatedBefore" class="date"/>
	<@s.textfield name="criteria.taskCreatedAfter" class="date"/>
	<@s.textfield name="criteria.taskDueBefore" class="date"/>
	<@s.textfield name="criteria.taskDueAfter" class="date"/>
	<div class="center">
		<button type="submit" class="btn btn-primary">${getText('search')}</button> <button type="button" class="btn restore">${getText('restore')}</button>
	</div>
</form>
</body>
</html>