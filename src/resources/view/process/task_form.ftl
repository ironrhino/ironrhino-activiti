<#if formTemplate?has_content && formTemplate?index_of('<body>') gt -1>
<@formTemplate?interpret/>
<#else>
<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>${title}</title>
</head>
<body>
<#include "/resources/view/process/task_form.prepare.ftl">
<#include "/resources/view/process/task_form.history.ftl">
<#include "/resources/view/process/task_form.comments.ftl">
<#include "/resources/view/process/task_form.attachments.ftl">
<#if formTemplate?has_content && (formTemplate?index_of('<form') gt -1 || formTemplate?index_of('<@s.form') gt -1)>
	<@formTemplate?interpret/>
<#else>
<#if !inputGridColumns??>
<#assign inputGridColumns=0>
<#if Parameters.inputGridColumns??>
<#assign inputGridColumns=Parameters.inputGridColumns?number>
</#if>
</#if>
<form id="${processDefinition.key}<#if task??>_${task.taskDefinitionKey}</#if>" action="${actionBaseUrl}/submit<#if uid?has_content>/${uid}</#if>" method="post" class="ajax form-horizontal disposable<#if task??> ${task.taskDefinitionKey}</#if>" enctype="multipart/form-data"<#if inputGridColumns gt 0> data-columns="${inputGridColumns}"</#if>>
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
		<#if !submitFormPropertyName?has_content||submitFormPropertyName!=entry.key>
		<@processFormElement name=entry.key />
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
	<#include "/resources/view/process/task_form.buttons.ftl">
</form>
</#if>
</body>
</html>
</#escape>
</#if>