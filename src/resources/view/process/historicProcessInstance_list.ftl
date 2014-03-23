<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title><#if request.requestURI?ends_with('/involved')><#if startedBy??&&startedBy>发起的流程<#else>经办的流程</#if><#else>流程列表</#if></title>
</head>
<body>
<#assign columns={"processDefinition.name":{"width":"150px"},
"historicProcessInstance.startUserId":{"alias","startUser","width":"100px","template":r'<span class="user" data-username="${value}">${statics["org.ironrhino.core.util.ApplicationContextUtils"].getBean("userManager").loadUserByUsername(value)!}</span>'},
"historicProcessInstance.startTime":{"alias":"发起时间","width":"130px"},
"historicProcessInstance.endTime":{"width":"130px"},
"historicProcessInstance.deleteReason":{}}>

<#assign bottomButtons='
<button type="button" class="btn reload">${action.getText("reload")}</button>
'>
<#assign actionColumnButtons='
<button type="button" class="btn" data-view="view">${action.getText("view")}</button>
<#if !entity.historicProcessInstance.endTime??>
<button type="button" class="btn" data-view="trace" data-windowoptions="{\'width\':\'80%\',\'height\':650}">${action.getText("trace")}</button>
</#if>
'>

<@richtable entityName="historicProcessInstance" action="${getUrl(request.requestURI)}" columns=columns actionColumnButtons=actionColumnButtons bottomButtons=bottomButtons searchable=false celleditable=false/>
</body>
</html></#escape>