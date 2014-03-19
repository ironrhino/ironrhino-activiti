<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>流程实例</title>
</head>
<body>
<#assign columns={"user":{"alias":"申请人","width":"100px"},"leaveType":{"width":"100px"},"reason":{},"startTime":{"width":"130px"},"endTime":{"width":"130px"}}>
<#assign actionColumnButtons=r'
<#if !entity.task.assignee??>
<a class="btn ajax view" href="${actionBaseUrl}/claim/${entity.task.id}">签收</a>
<#else>
<button type="button" class="btn" data-view="view">办理</button>
</#if>
'>
<@richtable entityName="leave" columns=columns actionColumnButtons=actionColumnButtons searchable=false celleditable=false/>
</body>
</html></#escape>