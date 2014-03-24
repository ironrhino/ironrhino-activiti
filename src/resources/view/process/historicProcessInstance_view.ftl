<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>查看流程历史</title>
</head>
<body>
<table class="table table-bordered">
	<thead>
	<tr>
		<th style="width:100px;">${action.getText('taskName')}</th>
		<th>${action.getText('assignee')}</th>
		<th>${action.getText('startTime')}</th>
		<th>${action.getText('endTime')}</th>
	</tr>
	</thead>
	<tbody>
	<#list activityDetails as ad>
	<tr class="success">
		<td>${action.getText(ad.name)}</td>
		<td><span class="user" data-username="${ad.assignee!}">${(statics['org.ironrhino.core.util.ApplicationContextUtils'].getBean('userManager').loadUserByUsername(ad.assignee))!}</span></td>
		<td>${ad.startTime?datetime}</td>
		<td>${(ad.endTime?datetime)!}</td>
	</tr>
	<#list ad.data.entrySet() as entry>
	<tr>
		<td>${action.getText(entry.key)}</td>
		<td colspan="3"><div style="white-space:pre-wrap;word-break:break-all;">${entry.value}</div></td>
	</tr>
	</#list>
	</#list>
	</tbody>
</table>
</body>
</html></#escape>