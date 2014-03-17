<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>查看流程定义</title>
</head>
<body>
<#if processDefinition.diagramResourceName?has_content>
<img src="${actionBaseUrl}/download?deploymentId=${processDefinition.deploymentId}&resourceName=${processDefinition.diagramResourceName}"/>
</#if>
</body>
</html></#escape>