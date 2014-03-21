<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>${title}</title>
</head>
<body>
<#if !historicProcessInstance??>
<#if processDefinition?? && processDefinition.description?has_content>
<div class="alert alert-info">
<button type="button" class="close" data-dismiss="alert">&times;</button>
${processDefinition.description}
</div>
</#if>
<#else>
<div>
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
		<td>${(statics['org.ironrhino.core.util.ApplicationContextUtils'].getBean('userManager').loadUserByUsername(historicProcessInstance.startUserId))!}</td>
		<td>${historicProcessInstance.startTime?datetime}</td>
		<td></td>
		<td></td>
	</tr>
	<#if historicTaskInstances?? && historicTaskInstances?size gt 0>
	<#list historicTaskInstances as task>
	<tr>
		<td>${task.name}</td>
		<td>${(statics['org.ironrhino.core.util.ApplicationContextUtils'].getBean('userManager').loadUserByUsername(task.assignee))!}</td>
		<td>${task.startTime?datetime}</td>
		<td>${(task.claimTime?datetime)!}</td>
		<td>${task.endTime?datetime}</td>
	</tr>
	</#list>
	</#if>
	</tbody>
</table>
</div>


</#if>
<form action="${actionBaseUrl}/submit<#if uid?has_content>/${uid}</#if>" method="post" class="ajax form-horizontal disposable">
	<#if processDefinitionId?has_content>
	<input type="hidden" name="processDefinitionId" value="${processDefinitionId}"/>
	</#if>
	<#if formElements??>
	<#list formElements as fe>
	<#assign id='_'+fe.name/>
	<#assign type=fe.type/>
	<#if !fe.disabled || fe.value?has_content>
	<div class="control-group">
		<label class="control-label" for="${id}">${action.getText(fe.label)}</label>
		<div class="controls">
		<#if type=='textarea'>
		<textarea id="${id}" name="${fe.name}"<#if fe.readonly> readonly</#if><#if fe.disabled> disabled</#if> <#if fe.cssClass?has_content> class="${fe.cssClass}"</#if><#list fe.dynamicAttributes.entrySet() as entry> ${entry.key}="${entry.value}"</#list>>${fe.value!}</textarea>
		<#elseif type=='select'>
		<select id="${id}" name="${fe.name}"<#if fe.readonly> readonly</#if><#if fe.disabled> disabled</#if> <#if fe.cssClass?has_content> class="${fe.cssClass}"</#if><#list fe.dynamicAttributes.entrySet() as entry> ${entry.key}="${entry.value}"</#list>>
		<option></option>
		<#list fe.values.entrySet() as entry>
		<option value="${entry.key}"<#if fe.value??&&fe.value==entry.key> selected</#if>>${entry.value}</option>
		</#list>
		</select>
		<#elseif type=='radio'>
		<#list fe.values.entrySet() as entry>
		<label for="${id}_${entry.key}" class="radio inline"><input id="${id}_${entry.key}" type="radio" name="${fe.name}" value="${entry.key}"<#if fe.value??&&fe.value==entry.key> checked</#if> class="custom <#if fe.cssClass?has_content> ${fe.cssClass}</#if>"> ${action.getText(entry.value)}</label>
		</#list>
		<#else>
		<input id="${id}" type="${fe.inputType}" name="${fe.name}" value="${fe.value!}"<#if fe.readonly> readonly</#if><#if fe.disabled> disabled</#if> <#if fe.cssClass?has_content> class="${fe.cssClass}"</#if><#list fe.dynamicAttributes.entrySet() as entry> ${entry.key}="${entry.value}"</#list>/>
		</#if>
		</div>
	</div>
	</#if>
	</#list>
	</#if>
	<@s.submit value="%{getText('submit')}" cssClass="btn-primary"/>
</form>
</body>
</html></#escape>