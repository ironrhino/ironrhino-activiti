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
		<td><#if historicProcessInstance.startUserId?has_content><span class="user" data-username="${historicProcessInstance.startUserId}">${(statics['org.ironrhino.core.util.ApplicationContextUtils'].getBean('userDetailsService').loadUserByUsername(historicProcessInstance.startUserId,true))!}</span></#if></td>
		<td>${historicProcessInstance.startTime?datetime}</td>
		<td></td>
		<td></td>
	</tr>
	<#if historicTaskInstances?? && !historicTaskInstances.empty>
	<#list historicTaskInstances as task>
	<tr>
		<td>${action.getText(task.name)}</td>
		<td>
		<#if task.assignee?has_content><span class="user" data-username="${task.assignee}">${(statics['org.ironrhino.core.util.ApplicationContextUtils'].getBean('userDetailsService').loadUserByUsername(task.assignee,true))!}</span></#if>
		<#if task.owner?? && task.assignee?? && task.owner!=task.assignee> (<span class="user" data-username="${task.owner!}">${(statics['org.ironrhino.core.util.ApplicationContextUtils'].getBean('userDetailsService').loadUserByUsername(task.owner,true))!}</span>)</#if>
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
				<span class="user" data-username="${comment.userId}">${(statics['org.ironrhino.core.util.ApplicationContextUtils'].getBean('userDetailsService').loadUserByUsername(comment.userId,true))!}</span>
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
				<span class="user" data-username="${attachment.userId}">${(statics['org.ironrhino.core.util.ApplicationContextUtils'].getBean('userDetailsService').loadUserByUsername(attachment.userId,true))!}</span>
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
<#if formTemplate?has_content && form?index_of('<form') gt -1>
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
		<#assign fe=entry.value/>
		<#assign id=fe.id!/>
		<#if !id?has_content>
		<#assign id='form_'+entry.key/>
		</#if>
		<#assign hidden=fe.disabled&&!fe.value?has_content/>
		<#if fe.type=='listpick'>
			<div<#if hidden> style="display:none;"</#if> class="control-group <#if fe.readonly||fe.disabled>_</#if>listpick" data-options="{'url':'<@url value=fe.dynamicAttributes['pickUrl']/>'}">
				<@s.hidden id=id name=entry.key value=fe.value! cssClass="listpick-id "+fe.cssClass/>
				<label class="control-label">${action.getText(fe.label)}</label>
				<div class="controls<#if fe.readonly||fe.disabled> text</#if>">
				<span class="listpick-name"><#if taskVariables?? && taskVariables[entry.key]??><#if taskVariables[entry.key].fullname??>${taskVariables[entry.key].fullname!}<#else>${taskVariables[entry.key]!}</#if></#if></span>
				</div>
			</div>
		<#else>
		<div<#if hidden> style="display:none;"</#if> class="control-group">
			<label class="control-label" for="${id}">${action.getText(fe.label)}</label>
			<div class="controls">
			<#if fe.type=='textarea'>
			<textarea id="${id}" name="${entry.key}"<#if fe.readonly> readonly</#if><#if fe.disabled> disabled</#if> <#if fe.cssClass?has_content> class="${fe.cssClass}"</#if><#list fe.dynamicAttributes.entrySet() as en> ${en.key}="${en.value}"</#list>>${fe.value!}</textarea>
			<#elseif fe.type=='select'>
			<select id="${id}" name="${entry.key}"<#if fe.readonly> readonly</#if><#if fe.disabled> disabled</#if> <#if fe.cssClass?has_content> class="${fe.cssClass}"</#if><#list fe.dynamicAttributes.entrySet() as en> ${en.key}="${en.value}"</#list>>
			<option></option>
			<#list fe.values.entrySet() as en>
			<option value="${en.key}"<#if fe.value??&&fe.value==en.key> selected</#if>>${en.value}</option>
			</#list>
			</select>
			<#elseif fe.type=='enum'>
			<select id="${id}" name="${entry.key}"<#if fe.readonly> readonly</#if><#if fe.disabled> disabled</#if> <#if fe.cssClass?has_content> class="${fe.cssClass}"</#if>>
			<option></option>
			<#list statics[fe.dynamicAttributes['enumType']].values() as en>
			<option value="${en.name()}"<#if fe.value??&&fe.value==en.name()> selected</#if>>${en}</option>
			</#list>
			</select>
			<#elseif fe.type=='radio'>
			<#list fe.values.entrySet() as en>
			<label for="${id}_${en.key}" class="radio inline"><input id="${id}_${en.key}" type="radio" name="${entry.key}" value="${en.key}"<#if fe.value??&&fe.value==en.key> checked</#if> class="custom <#if fe.cssClass?has_content> ${fe.cssClass}</#if>"> ${action.getText(en.value)}</label>
			</#list>
			<#else>
			<input id="${id}" type="${fe.inputType}" name="${entry.key}"<#if fe.value?has_content> value="${fe.value}"</#if><#if fe.readonly> readonly</#if><#if fe.disabled> disabled</#if> <#if fe.cssClass?has_content> class="${fe.cssClass}"</#if><#list fe.dynamicAttributes.entrySet() as en> ${en.key}="${en.value}"</#list>/>
			</#if>
			</div>
		</div>
		</#if>
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
	<@s.submit value="%{getText('start')}" cssClass="btn-primary">
	<@s.param name="after">
	<button type="button" class="btn toggle-control-group" data-groupclass="comment">${action.getText('comment')}</button>
	<button type="button" class="btn toggle-control-group" data-groupclass="attachment">${action.getText('attachment')}</button>
	</@s.param>
	</@s.submit>
	<#else>
	<@s.submit value="%{getText('submit')}" cssClass="btn-primary">
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