<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>流程实例</title>
</head>
<body>
<#assign columns={"id":{"alias":"流程实例ID","width":"120px"}}>
<#if !id?has_content>
<#assign columns=columns+{"processDefinitionId":{"alias":"流程定义ID","width":"100px"}}>
</#if>
<#assign columns=columns+{"name":{},"businessKey":{"alias":"业务键值","width":"100px"},"suspended":{"width":"80px"},"ended":{"width":"80px"}}>

<#assign bottomButtons='
<button type="button" class="btn reload">${action.getText("reload")}</button>
'>
<#assign actionColumnButtons=r'
<button type="button" class="btn" data-view="view">${action.getText("view")}</button>
'>
<@richtable entityName="processInstance" columns=columns actionColumnButtons=actionColumnButtons bottomButtons=bottomButtons searchable=false celleditable=false/>
</body>
</html></#escape>