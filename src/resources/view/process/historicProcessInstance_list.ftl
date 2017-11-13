<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title><#if request.requestURI?ends_with('/involved')><#if startedBy??&&startedBy>发起的流程<#else>经办的流程</#if><#else>流程列表</#if></title>
</head>
<body>
<#assign columns={
"historicProcessInstance.id":{"alias":"流程ID","width":"100px"},
"processDefinition.name":{"alias":"流程名"},
"historicProcessInstance.businessKey":{"alias":"流程业务KEY","width":"100px"},
"historicProcessInstance.startUserId":{"alias","startUser","width":"100px","template":r'<#if value?has_content><span class="user" data-username="${value}">${beans["userDetailsService"].loadUserByUsername(value,true)!}</span></#if>'},
"historicProcessInstance.startTime":{"alias":"发起时间","width":"130px"}}>
<#if !Parameters.finished?? || Parameters.finished != 'true'>
<#assign columns+={
"activityName":{"alias":"当前活动","width":"100px","template",r'${(entity.historicActivityInstance.activityName)!}'},
"assignee":{"alias":"当前处理人","width":"100px","template":r'<#if entity.historicActivityInstance??&&entity.historicActivityInstance.assignee?has_content><span class="user" data-username="${entity.historicActivityInstance.assignee}">${beans["userDetailsService"].loadUserByUsername(entity.historicActivityInstance.assignee,true)!}</span></#if>'}}>
</#if>
<#if !Parameters.finished?? || Parameters.finished == 'true'>
<#assign columns+={"historicProcessInstance.endTime":{"width":"130px"}}>
</#if>
<#assign bottomButtons='<@btn class="reload"/> <@btn class="filter"/>'>
<#assign actionColumnButtons=r'
<@btn view="view"/>
<#if !entity.historicProcessInstance.endTime??>
'+'
<@btn view="trace" windowoptions="{\'width\':\'90%\',\'height\':650}"/>
'+r'
</#if>
'>
<#assign formid='historicProcessInstance_form'>
<#if Parameters.finished??>
<#assign formid=((Parameters.finished=='true')?then('finished','unfinished'))+'_'+formid/>
</#if>
<@richtable formid=formid entityName="historicProcessInstance" columns=columns actionColumnButtons=actionColumnButtons bottomButtons=bottomButtons searchable=false celleditable=false/>
<form method="post" class="ajax view criteria form-horizontal" style="display:none;" data-columns="2">
	<@s.textfield name="criteria.processDefinitionKey"/>
	<@s.textfield name="criteria.processDefinitionName"/>
	<@s.textfield name="criteria.processInstanceId"/>
	<@s.textfield name="criteria.processInstanceBusinessKey"/>
<#if !request.requestURI?ends_with('/involved')>
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
</#if>
	<@s.textfield name="criteria.startedBefore" class="date"/>
	<@s.textfield name="criteria.startedAfter" class="date"/>
<#if !Parameters.finished?? || Parameters.finished == 'true'>
	<@s.textfield name="criteria.finishedBefore" class="date"/>
	<@s.textfield name="criteria.finishedAfter" class="date"/>
</#if>
	<div class="center">
		<button type="submit" class="btn btn-primary">${getText('search')}</button> <button type="button" class="btn restore">${getText('restore')}</button>
	</div>
</form>
</body>
</html></#escape>