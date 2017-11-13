<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>流程实例</title>
</head>
<body>
<#assign columns={"processInstance.id":{"alias":"流程实例ID","width":"100px"},
"processInstance.processDefinitionId":{"alias":"流程定义ID","width":"150px","template":r"<#if id?has_content>${value}<#else><a href='${actionBaseUrl}/list/${value}' class='ajax view'>${value}</a></#if>"},
"processDefinition.name":{"alias":"流程名"},
"processInstance.businessKey":{"alias":"流程业务KEY","width":"100px"},
"historicProcessInstance.startUserId":{"alias","startUser","width":"80px","template":r'<#if value?has_content><span class="user" data-username="${value}">${beans["userDetailsService"].loadUserByUsername(value,true)!}</span></#if>'},
"historicProcessInstance.startTime":{"alias":"发起时间","width":"130px"},
"activityName":{"alias":"当前活动","width":"80px","template",r'${(entity.historicActivityInstance.activityName)!}'},
"assignee":{"alias":"当前处理人","width":"100px","template":r'<#if entity.historicActivityInstance??&&entity.historicActivityInstance.assignee?has_content><span class="user" data-username="${entity.historicActivityInstance.assignee}">${beans["userDetailsService"].loadUserByUsername(entity.historicActivityInstance.assignee,true)!}</span></#if>'},
"processInstance.suspended":{"alias":"挂起","width":"50px"}
}>

<#assign bottomButtons='
<button type="button" class="btn confirm" data-action="delete" data-shown="selected" data-filterselector="[data-suspended=\'true\']:not([data-deletable=\'false\'])">${getText("delete")}</button>
'+r'<@authorize ifAnyGranted="ROLE_ADMINISTRATOR">'+'
<button type="button" class="btn confirm" data-action="activate" data-shown="selected" data-filterselector="[data-suspended=\'true\']">${getText("activate")}</button>
<button type="button" class="btn confirm" data-action="suspend" data-shown="selected" data-filterselector=":not([data-suspended=\'true\'])">${getText("suspend")}</button>
'+r'</@authorize>'
+r'<@btn class="reload"/> <@btn class="filter"/>'>
<#assign actionColumnButtons=r'<@btn view="view" '+'windowoptions="{\'width\':\'90%\',\'height\':650}"/>'>
<#assign rowDynamicAttributes=r'{"data-deletable":"${entity.processInstance.suspended?string}","class":"${entity.processInstance.suspended?then("warning","")}"}'>
<@richtable entityName="processInstance" columns=columns actionColumnButtons=actionColumnButtons bottomButtons=bottomButtons rowDynamicAttributes=rowDynamicAttributes searchable=false celleditable=false/>
<form method="post" class="ajax view criteria form-horizontal" style="display:none;" data-columns="2">
	<@s.textfield name="criteria.processDefinitionKey"/>
	<@s.textfield name="criteria.processDefinitionName"/>
	<@s.textfield name="criteria.processInstanceId"/>
	<@s.textfield name="criteria.processInstanceBusinessKey"/>
<@authorize ifAnyGranted="ROLE_ADMINISTRATOR">
	<div class="control-group listpick" data-options="{'url':'<@url value="/user/pick?columns=username,name&enabled=true"/>','idindex':1,'nameindex':2}">
	<@s.hidden id="criteria_involvedUser" name="criteria.involvedUser" class="listpick-id"/>
	<label class="control-label" for="criteria_involvedUser-control">${getText('involvedUser')}</label>
	<div class="controls">
	<span class="listpick-name"></span>
	</div>
	</div>
	<div class="control-group listpick" data-options="{'url':'<@url value="/user/pick?columns=username,name&enabled=true"/>','idindex':1,'nameindex':2}">
	<@s.hidden id="criteria_startedBy" name="criteria.startedBy" class="listpick-id"/>
	<label class="control-label" for="criteria_startedBy-control">${getText('startedBy')}</label>
	<div class="controls">
	<span class="listpick-name"></span>
	</div>
	</div>
</@authorize>
<div class="center">
	<button type="submit" class="btn btn-primary">${getText('search')}</button> <button type="button" class="btn restore">${getText('restore')}</button>
</div>
</form>
</body>
</html></#escape>