<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>流程实例</title>
</head>
<body>
<#assign columns={"processInstance.id":{"alias":"流程实例ID","width":"100px"},
"processInstance.processDefinitionId":{"alias":"流程定义ID","width":"100px","template":r"<#if id?has_content>${value}<#else><a href='${actionBaseUrl}/list/${value}' class='ajax view'>${value}</a></#if>"},
"processDefinition.name":{"alias":"流程名"},
"processInstance.businessKey":{"alias":"业务KEY","width":"100px"},
"historicProcessInstance.startUserId":{"alias","startUser","width":"80px","template":r'<span class="user" data-username="${value}">${statics["org.ironrhino.core.util.ApplicationContextUtils"].getBean("userManager").loadUserByUsername(value)!}</span>'},
"historicProcessInstance.startTime":{"alias":"发起时间","width":"130px"},
"activityName":{"alias":"当前活动","width":"100px","template",r'${(entity.historicActivityInstance.activityName)!}'},
"assignee":{"alias":"当前处理人","width":"100px","template":r'<#if entity.historicActivityInstance??&&entity.historicActivityInstance.assignee?has_content><span class="user" data-username="${entity.historicActivityInstance.assignee}">${statics["org.ironrhino.core.util.ApplicationContextUtils"].getBean("userManager").loadUserByUsername(entity.historicActivityInstance.assignee)!}</span></#if>'}
}>

<#assign bottomButtons='
<button type="button" class="btn reload">${action.getText("reload")}</button>
'>
<#assign actionColumnButtons='
<button type="button" class="btn" data-view="view" data-windowoptions="{\'width\':\'80%\',\'height\':650}">${action.getText("view")}</button>
'>
<@authorize ifAnyGranted="ROLE_ADMINISTRATOR">
<#assign actionColumnButtons=actionColumnButtons+r'
<#if entity.processInstance.suspended>
<button type="button" class="btn confirm" data-action="activate">${action.getText("activate")}</button>
<#else>
<button type="button" class="btn confirm" data-action="suspend">${action.getText("suspend")}</button>
</#if>
'>
</@authorize>

<@richtable entityName="processInstance" columns=columns actionColumnButtons=actionColumnButtons bottomButtons=bottomButtons searchable=false celleditable=false/>
</body>
</html></#escape>