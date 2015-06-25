<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>${title}</title>
</head>
<body>
<#if !historicProcessInstance??>
<#if processDefinition?? && processDefinition.description?has_content>
<div class="alert alert-block">
${processDefinition.description}
</div>
</#if>
<#else>
<table class="table">
	<thead>
	<tr>
		<th>${action.getText('taskName')}</th>
		<th style="width:100px;">${action.getText('assignee')}</th>
		<th style="width:130px;">${action.getText('startTime')}</th>
		<th style="width:130px;">${action.getText('claimTime')}</th>
		<th style="width:130px;">${action.getText('endTime')}</th>
	</tr>
	</thead>
	<tbody>
	<tr>
		<td>${action.getText('startProcessInstance')}</td>
		<td><#if historicProcessInstance.startUserId?has_content><span class="user" data-username="${historicProcessInstance.startUserId}">${(beans['userDetailsService'].loadUserByUsername(historicProcessInstance.startUserId,true))!}</span></#if></td>
		<td>${historicProcessInstance.startTime?datetime}</td>
		<td></td>
		<td></td>
	</tr>
	<#if historicTaskInstances?? && !historicTaskInstances.empty>
	<#list historicTaskInstances as task>
	<tr>
		<td>${action.getText(task.name)}</td>
		<td>
		<#if task.assignee?has_content><span class="user" data-username="${task.assignee}">${(beans['userDetailsService'].loadUserByUsername(task.assignee,true))!}</span></#if>
		<#if task.owner?? && task.assignee?? && task.owner!=task.assignee> (<span class="user" data-username="${task.owner!}">${(beans['userDetailsService'].loadUserByUsername(task.owner,true))!}</span>)</#if>
		</td>
		<td>${task.startTime?datetime}</td>
		<td>${(task.claimTime?datetime)!}</td>
		<td>${task.endTime?datetime}</td>
	</tr>
	</#list>
	</#if>
	</tbody>
</table>
</#if>
<#if comments?? && !comments.empty>
	<table class="table">
			<caption style="background-color: #bebec5;"><h5>${action.getText('comment')}</h5></caption>
			<thead>
			<tr>
				<th style="width:80px;">${action.getText('owner')}</th>
				<th style="width:130px;">${action.getText('date')}</th>
				<th>${action.getText('comment')}</th>
				<th style="width:80px;"></th>
			</tr>
			</thead>
			<tbody>
			<#list comments as comment>
			<tr>
				<td>
				<#if comment.userId??>
				<span class="user" data-username="${comment.userId}">${(beans['userDetailsService'].loadUserByUsername(comment.userId,true))!}</span>
				</#if>
				</td>
				<td>${comment.time?datetime}</td>
				<td style="white-space:pre-wrap;word-break:break-all;">${comment.fullMessage!}</td>
				<td>
				<#if comment.userId?? && comment.userId==authentication("principal").username>
				<a href="${actionBaseUrl}/deleteComment/${comment.id}" class="btn ajax deleteRow">${action.getText('delete')}</a>
				</#if>
				</td>
			</tr>
			</#list>
			</tbody>
	</table>	
</#if>
<#if attachments?? && !attachments.empty>
	<table class="table">
			<caption style="background-color: #bebec5;"><h5>${action.getText('attachment')}</h5></caption>
			<thead>
			<tr>
				<th style="width:200px;">${action.getText('file')}</th>
				<th style="width:100px;">${action.getText('owner')}</th>
				<th>${action.getText('description')}</th>
				<th style="width:80px;"></th>
			</tr>
			</thead>
			<tbody>
			<#list attachments as attachment>
			<tr>
				<td>
				<a href="${actionBaseUrl}/downloadAttachment/${attachment.id}" target="_blank">${attachment.name}</a>
				<a href="${actionBaseUrl}/downloadAttachment/${attachment.id}" download="${attachment.name}" style="margin-left:10px;"><i class="glyphicon glyphicon-download-alt"></i></a>
				</td>
				<td>
				<#if attachment.userId??>
				<span class="user" data-username="${attachment.userId}">${(beans['userDetailsService'].loadUserByUsername(attachment.userId,true))!}</span>
				</#if>
				</td>
				<td>${attachment.description!}</td>
				<td>
				<#if attachment.userId?? && attachment.userId==authentication("principal").username>
				<a href="${actionBaseUrl}/deleteAttachment/${attachment.id}" class="btn ajax deleteRow">${action.getText('delete')}</a>
				</#if>
				</td>
			</tr>
			</#list>
			</tbody>
	</table>	
</#if>
<#if formTemplate?has_content && (formTemplate?index_of('<form') gt -1 || formTemplate?index_of('<@s.form') gt -1)>
	<@formTemplate?interpret/>
<#else>
<form id="${processDefinition.key}<#if task??>_${task.taskDefinitionKey}</#if>" action="${actionBaseUrl}/submit<#if uid?has_content>/${uid}</#if>" method="post" class="ajax form-horizontal disposable<#if task??> ${task.taskDefinitionKey}</#if>" enctype="multipart/form-data">
	<#if task?? && task.description?has_content>
	<div class="alert alert-block">
	${task.description}
	</div>
	</#if>
	<#if processDefinitionId?has_content>
	<input type="hidden" name="processDefinitionId" value="${processDefinitionId}"/>
	</#if>
	<#if formTemplate?has_content>
		<@formTemplate?interpret/>
	<#else>
		<#if formElements??>
		<#list formElements.entrySet() as entry>
		<@processFormElement name=entry.key />
		</#list>
		</#if>
	</#if>
	<div class="control-group comment" style="display:none;">
			<label class="control-label" for="_comment_">${action.getText('comment')}</label>
			<div class="controls">
			<textarea id="_comment_" name="_comment_" class="input-xxlarge"></textarea>
			</div>
	</div>
	<div class="control-group attachment" style="display:none;">
			<label class="control-label">${action.getText('attachment')}</label>
			<div class="controls">
			<table class="table datagrid">
				<thead>
				<tr>
					<th style="width:300px;">${action.getText('file')}</th>
					<th>${action.getText('description')}</th>
					<th class="manipulate"></th>
				</tr>
				</thead>
				<tbody>
				<tr>
					<td><input type="file" name="file" style="width:100%;"/></td>
					<td><input type="text" name="attachmentDescription" style="width:100%;"/></td>
					<td class="manipulate"></td>
				</tr>
				</tbody>
			</table>	
			</div>
	</div>
	<#if !historicProcessInstance??>
	<@s.submit value="%{getText('start')}" class="btn-primary">
	<@s.param name="after">
	<button type="button" class="btn toggle-control-group" data-groupclass="comment">${action.getText('comment')}</button>
	<button type="button" class="btn toggle-control-group" data-groupclass="attachment">${action.getText('attachment')}</button>
	</@s.param>
	</@s.submit>
	<#else>
	<@s.submit value="%{getText('submit')}" class="btn-primary">
	<@s.param name="after">
	<button type="button" class="btn toggle-control-group" data-groupclass="comment">${action.getText('comment')}</button>
	<button type="button" class="btn toggle-control-group" data-groupclass="attachment">${action.getText('attachment')}</button>
	</@s.param>
	</@s.submit>
	</#if>
</form>
</#if>
</body>
</html></#escape>