<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>流程实例</title>
</head>
<body>
<#assign columns={"id":{"alias":"流程实例ID","width":"120px"},"processDefinitionId":{"alias":"流程定义ID","width":"100px","template":r"<#if id?has_content>${value}<#else><a href='${actionBaseUrl}/list/${value}' class='ajax view'>${value}</a></#if>"},"name":{},"businessKey":{"alias":"业务键值","width":"200px"},"suspended":{"width":"80px"},"ended":{"width":"80px"}}>

<#assign bottomButtons='
<button type="button" class="btn reload">${action.getText("reload")}</button>
'>
<#assign actionColumnButtons='
<button type="button" class="btn" data-view="view" data-windowoptions="{\'width\':\'80%\',\'height\':650}">${action.getText("view")}</button>
'>
<@richtable entityName="processInstance" columns=columns actionColumnButtons=actionColumnButtons bottomButtons=bottomButtons searchable=false celleditable=false/>
</body>
</html></#escape>