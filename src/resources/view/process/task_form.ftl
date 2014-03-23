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
		<td><span class="user" data-username="${historicProcessInstance.startUserId!}">${(statics['org.ironrhino.core.util.ApplicationContextUtils'].getBean('userManager').loadUserByUsername(historicProcessInstance.startUserId!))!}</span></td>
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
<#if formTemplate?has_content>
	<@formTemplate?interpret/>
<#else>
<form action="${actionBaseUrl}/submit<#if uid?has_content>/${uid}</#if>" method="post" class="ajax form-horizontal disposable">
	<#if processDefinitionId?has_content>
	<input type="hidden" name="processDefinitionId" value="${processDefinitionId}"/>
	</#if>
	<#if formElements??>
	<#list formElements.entrySet() as entry>
	<#assign id='form_'+entry.key/>
	<#assign fe=entry.value/>
	<#if !fe.disabled || fe.value?has_content>
	<div class="control-group">
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
	<#if !historicProcessInstance??>
	<@s.submit value="%{getText('start')}" cssClass="btn-primary"/>
	<#else>
	<@s.submit value="%{getText('submit')}" cssClass="btn-primary"/>
	</#if>
</form>
</#if>
</body>
</html></#escape>