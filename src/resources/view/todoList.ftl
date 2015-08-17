<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>待办任务</title>
</head>
<body>
<ul>
	<#assign processHelper=beans['processHelper']>
	<#assign userId=authentication('principal').username>
	<li>
		<#assign count=processHelper.countAssignedTasks(userId)>
		已分配的任务 <#if count gt 0><a class="ajax view" href="<@url value="/process/task/todotabs"/>#todolist_assigned"></#if><span class="badge<#if count gt 0> badge-important</#if>">${count}</span><#if count gt 0></a></#if> 
	</li>
	<li>
		<#assign count=processHelper.countCandidateTasks(userId)>
		待签收的任务 <#if count gt 0><a class="ajax view" href="<@url value="/process/task/todotabs"/>#todolist_candidate"></#if><span class="badge<#if count gt 0> badge-important</#if>">${count}</span><#if count gt 0></a></#if> 
	</li>
</ul>
</body>
</html></#escape>