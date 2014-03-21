<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>发起的流程</title>
</head>
<body>
<#assign columns={"key.id":{"alias":"流程实例ID","width":"120px"},"key.processDefinitionId":{"alias":"流程定义ID","width":"100px","template":r"<#if id?has_content>${value}<#else><a href='${actionBaseUrl}/list/${value}' class='ajax view'>${value}</a></#if>"},"value.key":{"alias":"KEY","width":"100px"},"value.name":{},"key.businessKey":{"alias":"业务键值","width":"200px"},"key.suspended":{"width":"80px"},"key.ended":{"width":"80px"}}>

<#assign bottomButtons='
<button type="button" class="btn reload">${action.getText("reload")}</button>
'>
<#assign actionColumnButtons='
<button type="button" class="btn" data-view="view" data-windowoptions="{\'width\':\'80%\',\'height\':650}">${action.getText("view")}</button>
'>
<@authorize ifAnyGranted="ROLE_ADMINISTRATOR">
<#assign actionColumnButtons=actionColumnButtons+r'
<#if entity.key.suspended>
<button type="button" class="btn confirm" data-action="activate">${action.getText("activate")}</button>
<#else>
<button type="button" class="btn confirm" data-action="suspend">${action.getText("suspend")}</button>
</#if>
'>
</@authorize>

<@richtable entityName="processInstance" action="${actionBaseUrl}/started" columns=columns actionColumnButtons=actionColumnButtons bottomButtons=bottomButtons searchable=false celleditable=false/>
</body>
</html></#escape>