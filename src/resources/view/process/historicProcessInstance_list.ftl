<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title><#if request.requestURI?ends_with('/involved')><#if startedBy??&&startedBy>发起的流程<#else>经办的流程</#if><#else>流程列表</#if></title>
</head>
<body>
<#assign columns={"value.name":{"width":"150px"},"key.startUserId":{"alias","startUser","width":"100px","template":r"${statics['org.ironrhino.core.util.ApplicationContextUtils'].getBean('userManager').loadUserByUsername(value)!}"},"key.startTime":{"width":"130px"},"key.endTime":{"width":"130px"},"key.deleteReason":{}}>

<#assign bottomButtons='
<button type="button" class="btn reload">${action.getText("reload")}</button>
'>
<#assign actionColumnButtons='
<button type="button" class="btn" data-view="view">${action.getText("view")}</button>
<#if !entity.key.endTime??>
<button type="button" class="btn" data-view="trace" data-windowoptions="{\'width\':\'80%\',\'height\':650}">${action.getText("trace")}</button>
</#if>
'>

<@richtable entityName="historicProcessInstance" action="${getUrl(request.requestURI)}" columns=columns actionColumnButtons=actionColumnButtons bottomButtons=bottomButtons searchable=false celleditable=false/>
</body>
</html></#escape>