<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>流程部署</title>
</head>
<body>
<#assign columns={"key.id":{"alias":"流程定义ID","width":"120px"},"key.key":{"alias":"KEY","width":"100px"},"key.name":{},"key.version":{"width":"60px"},"value.deploymentTime":{"width":"130px"},"key.suspended":{"width":"60px"},
"key.resourceName":{"alias":"流程定义XML","width":"120px","template":r"<a href='${actionBaseUrl}/download?deploymentId=${entity.key.deploymentId}&resourceName=${value}' download='${value}'>${value}</a>"},
"key.diagramResourceName":{"alias":"流程图","width":"120px","template":r"<#if value?has_content><a href='${actionBaseUrl}/download?deploymentId=${entity.key.deploymentId}&resourceName=${value}' download='${value}'>${value}</a></#if>"}}>
<#assign bottomButtons=r'
<button type="button" class="btn noajax deploy">${action.getText("deploy")}</button>
<button type="button" class="btn confirm" data-action="delete" data-shown="selected" style="display: none;">${action.getText("delete")}</button>
<button type="button" class="btn reload">${action.getText("reload")}</button>
'>
<#assign actionColumnButtons='
<button type="button" class="btn" data-view="view" data-windowoptions="{\'width\':\'80%\',\'height\':650}">${action.getText("view")}</button>
<#if entity.key.suspended>
<button type="button" class="btn confirm" data-action="activate">${action.getText("activate")}</button>
<#else>
<button type="button" class="btn confirm" data-action="suspend">${action.getText("suspend")}</button>
</#if>
'
+
r' <a class="btn" href="<@url value="/process/processInstance/list/${entity.id}"/>">实例</a>
'>
<@richtable formid="processDefinition-form" entityName="processDefinition" columns=columns actionColumnButtons=actionColumnButtons bottomButtons=bottomButtons searchable=true celleditable=false/>
</body>
</html></#escape>