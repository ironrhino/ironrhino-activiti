<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>查看流程</title>
</head>
<body>
<table class="table table-bordered">
	<thead>
	<tr>
		<th style="width:150px;">${action.getText('taskName')}</th>
		<th>${action.getText('assignee')}</th>
		<th>${action.getText('startTime')}</th>
		<th>${action.getText('endTime')}</th>
	</tr>
	</thead>
	<tbody>
	<#list activityDetails as ad>
	<tr class="<#if !historicProcessInstance.endTime??&&!ad_has_next>info<#else>success</#if>">
		<td>${action.getText(ad.name)}</td>
		<td><#if ad.assignee?has_content><span class="user" data-username="${ad.assignee}">${(statics['org.ironrhino.core.util.ApplicationContextUtils'].getBean('userDetailsService').loadUserByUsername(ad.assignee))!}</span></#if></td>
		<td>${ad.startTime?datetime}</td>
		<td>${(ad.endTime?datetime)!}</td>
	</tr>
	<#if ad.data?? && !ad.data.empty>
	<tr>
		<td colspan="4" style="text-align:center;font-weight:bold;">表单数据</td>
	</tr>
	<#list ad.data.entrySet() as entry>
	<tr>
		<td>${action.getText(entry.key)}</td>
		<td colspan="3"><div style="white-space:pre-wrap;word-break:break-all;">${entry.value}</div></td>
	</tr>
	</#list>
	</#if>
	<#if ad.comments?? && !ad.comments.empty>
	<tr>
		<td colspan="4" style="text-align:center;font-weight:bold;">${action.getText('comment')}</td>
	</tr>
	<#list ad.comments as comment>
	<tr>
		<td colspan="4"><div style="white-space:pre-wrap;word-break:break-all;">${comment.fullMessage!}</div></td>
	</tr>
	</#list>
	</#if>
	<#if ad.attachments?? && !ad.attachments.empty>
	<tr>
		<td colspan="4" style="text-align:center;font-weight:bold;">${action.getText('attachment')}</td>
	</tr>
	<#list ad.attachments as attachment>
	<tr>
		<td>
		<a href="<@url value="/process/task/downloadAttachment/${attachment.id}"/>" target="_blank">${attachment.name}</a>
		<a href="<@url value="/process/task/downloadAttachment/${attachment.id}"/>" download="${attachment.name}" style="margin-left:10px;"><i class="glyphicon glyphicon-download-alt"></i></a>
		</td>
		<td colspan="3">${attachment.description!}</td>
	</tr>
	</#list>
	</#if>
	</#list>
	</tbody>
</table>
</body>
</html></#escape>