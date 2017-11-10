<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>流程部署</title>
</head>
<body>
<#assign columns={"processDefinition.id":{"alias":"流程定义ID","width":"150px","template":r'<a href="<@url value="/process/historicProcessInstance/list?processDefinitionId=${value}"/>" class="ajax view">${value}</a>'},
"processDefinition.key":{"alias":"KEY","width":"120px","template":r'<#if Parameters.key??>${value}<#else><a href="${actionBaseUrl}?key=${value}" class="ajax view">${value}</a></#if>'},
"processDefinition.name":{"alias":"流程名"},
"processDefinition.version":{"width":"60px"},
"deployment.deploymentTime":{"width":"130px"},
"processDefinition.resourceName":{"alias":"流程定义XML","width":"140px","template":r'<a href="${actionBaseUrl}/download?deploymentId=${entity.processDefinition.deploymentId}&resourceName=${value}" download="${value}">${value}</a>'},
"processDefinition.diagramResourceName":{"alias":"流程图","width":"140px","template":r'<#if value?has_content><a href="${actionBaseUrl}/download?deploymentId=${entity.processDefinition.deploymentId}&resourceName=${value}" download="${value}">${value}</a></#if>'},
"processDefinition.suspended":{"width":"60px"}}>
<#assign bottomButtons=r'
<button type="button" class="btn upload" data-maxsize="4194304">${getText("deploy")}</button>
<button type="button" class="btn confirm" data-action="delete" data-shown="selected">${getText("delete")}</button>
<@btn class="reload"/>
'>
<#assign actionColumnButtons='
<@btn view="view" windowoptions="{\'width\':\'90%\',\'height\':650}"/>'+r'
<#if entity.processDefinition.suspended>
<@btn action="activate" confirm=true/>
<#else>
<@btn action="suspend" confirm=true/>
</#if>
'>
<#assign rowDynamicAttributes=r'{"class":"${entity.processDefinition.suspended?then("warning","")}"}'>
<@richtable formid="processDefinition-form" entityName="processDefinition" columns=columns actionColumnButtons=actionColumnButtons bottomButtons=bottomButtons rowDynamicAttributes=rowDynamicAttributes searchable=true celleditable=false/>
</body>
</html></#escape>