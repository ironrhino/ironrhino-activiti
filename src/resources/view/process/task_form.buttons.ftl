<#ftl output_format='HTML'>
<#assign templateName="/resources/view/process/form/"+processDefinitionKey/>
<#if formKey?has_content>
	<#assign templateName+="_"+formKey/>
</#if>
<#assign templateName+=".buttons.ftl"/>
<@resourcePresentConditional value=templateName>
<#include templateName>
</@resourcePresentConditional>
<@resourcePresentConditional value=templateName negated=true>
	<div class="form-actions">
	<#if submitFormPropertyName?has_content>
	<#if !submitFormPropertyOptions??>
		<#assign submitFormPropertyOptions={}/>
		<#assign fe=formElements[submitFormPropertyName]!/>
		<#if fe.type=='select'>
		<#assign submitFormPropertyOptions=fe.values/>
		<#elseif fe.type=='radio'>
		<#list fe.values.entrySet() as en>
			<#assign submitFormPropertyOptions+={en.key:((fe.label!getText(submitFormPropertyName))+getText(en.value))}/>
		</#list>
		<#elseif fe.type=='enum'>
		<#list statics[fe.dynamicAttributes['enumType']].values() as en>
			<#assign submitFormPropertyOptions+={en.name():en?string}/>
		</#list>
		</#if>
	</#if>
	<div class="form-actions">
	<#list submitFormPropertyOptions?keys as key>
	<button type="submit" class="btn<#if key?is_first> btn-primary</#if>" name="${submitFormPropertyName}" value="${key}">${submitFormPropertyOptions[key]}</button>
	</#list>
	<#else>
	<button type="submit" class="btn btn-primary">${getText((historicProcessInstance??)?then('submit','start'))}</button>	
	</#if>
	<span class="pull-right" style="margin-right:200px;">
	<button type="button" class="btn toggle-control-group" data-groupclass="comment">${getText('comment')}</button>
	<button type="button" class="btn toggle-control-group" data-groupclass="attachment">${getText('attachment')}</button>
	</span>
	</div>
</@resourcePresentConditional>