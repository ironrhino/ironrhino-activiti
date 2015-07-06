<#assign templateName="/resources/view/process/form/"+processDefinitionKey/>
<#if formKey?has_content>
	<#assign templateName+="_"+formKey/>
</#if>
<#assign templateName+=".buttons.ftl"/>
<@resourcePresentConditional value=templateName>
<#include templateName>
</@resourcePresentConditional>
<@resourcePresentConditional value=templateName negated=true>
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
</@resourcePresentConditional>