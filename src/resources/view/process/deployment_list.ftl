<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>流程部署</title>
</head>
<body>
<#assign columns={"id":{"alias":"流程定义ID","width":"120px"},"deploymentId":{"alias":"部署ID","width":"100px"},"key":{"alias":"KEY","width":"100px"},"name":{},"version":{"width":"100px"},
"resourceName":{"alias":"流程定义XML","width":"120px","template":r"<a href='${actionBaseUrl}/download?deploymentId=${entity.deploymentId}&resourceName=${value}' download='${value}'>${value}</a>"},
"diagramResourceName":{"alias":"流程图","width":"120px","template":r"<#if value?has_content><a href='${actionBaseUrl}/download?deploymentId=${entity.deploymentId}&resourceName=${value}' download='${value}'>${value}</a></#if>"}}>
<#assign bottomButtons=r'
<button type="button" class="btn noajax deploy">${action.getText("deploy")}</button>
<button type="button" class="btn" data-action="delete" data-shown="selected" style="display: none;">${action.getText("delete")}</button>
<button type="button" class="btn reload">${action.getText("reload")}</button>
'>
<#assign actionColumnButtons='
<button type="button" class="btn" data-view="view" data-windowoptions="{\'width\':\'80%\'}">${action.getText("view")}</button>'
+
r' <a class="btn" href="<@url value="/process/processInstance/list/${entity.id}"/>">实例</a>
'>
<@richtable formid="deployment-form" entityName="deployment" columns=columns actionColumnButtons=actionColumnButtons bottomButtons=bottomButtons searchable=false celleditable=false/>
</body>
</html></#escape>