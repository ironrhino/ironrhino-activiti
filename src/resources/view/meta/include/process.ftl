<#macro processFormElement name>
<#assign element=formElements[name]/>
<#assign templateName="/resources/view/process/form/"+processDefinitionKey/>
<#if formKey?has_content>
	<#assign templateName+="_"+formKey/>
</#if>
<#assign templateName+="_"+name/>
<#assign templateName+=".element.ftl"/>
<@resourcePresentConditional value=templateName>
<#include templateName>
</@resourcePresentConditional>
<@resourcePresentConditional value=templateName negated=true>
<#assign id=element.id!/>
<#if !id?has_content>
<#assign id='form_'+name/>
</#if>
<#assign hidden=element.disabled&&!element.value?has_content/>
<#if element.type=='listpick'>
	<div<#if hidden> style="display:none;"</#if> class="control-group <#if element.readonly||element.disabled>_</#if>listpick" data-options="{'url':'<@url value=element.dynamicAttributes['pickUrl']/>'}">
		<@s.hidden id=id name=name value=element.value! disabled=element.disabled class="listpick-id "+element.cssClass/>
		<label class="control-label">${getText(element.label)}</label>
		<div class="controls<#if element.readonly||element.disabled> text</#if>">
		<span class="listpick-name"><#if taskVariables?? && taskVariables[name]??><#if taskVariables[name].fullname??>${taskVariables[name].fullname!}<#else>${taskVariables[name]!}</#if></#if></span>
		</div>
	</div>
<#else>
<div<#if hidden> style="display:none;"</#if> class="control-group">
	<label class="control-label" for="${id}">${getText(element.label)}</label>
	<div class="controls">
	<#if element.type=='textarea'>
	<textarea id="${id}" name="${name}"<#if element.readonly> readonly</#if><#if element.disabled> disabled</#if> <#if element.cssClass?has_content> class="${element.cssClass}"</#if><#list element.dynamicAttributes.entrySet() as en> ${en.key}="${en.value}"</#list>>${element.value!}</textarea>
	<#elseif element.type=='select'>
	<#if element.readonly><input id="${id}" type="hidden" name="${name}"<#if element.value?has_content> value="${element.value}"</#if><#if element.cssClass?has_content> class="${element.cssClass}"</#if><#list element.dynamicAttributes.entrySet() as en> ${en.key}="${en.value}"</#list>/></#if>
	<select id="${id}" name="${name}"<#if element.readonly||element.disabled> disabled</#if> <#if element.cssClass?has_content> class="${element.cssClass}"</#if><#list element.dynamicAttributes.entrySet() as en> ${en.key}="${en.value}"</#list>>
	<option></option>
	<#list element.values.entrySet() as en>
	<option value="${en.key}"<#if element.value??&&element.value==en.key> selected</#if>>${en.value}</option>
	</#list>
	</select>
	<#elseif element.type=='enum'>
	<#if element.readonly><input id="${id}" type="hidden" name="${name}"<#if element.value?has_content> value="${element.value}"</#if><#if element.cssClass?has_content> class="${element.cssClass}"</#if><#list element.dynamicAttributes.entrySet() as en> ${en.key}="${en.value}"</#list>/></#if>
	<select id="${id}" name="${name}"<#if element.readonly||element.disabled> disabled</#if> <#if element.cssClass?has_content> class="${element.cssClass}"</#if>>
	<option></option>
	<#list statics[element.dynamicAttributes['enumType']].values() as en>
	<option value="${en.name()}"<#if element.value??&&element.value==en.name()> selected</#if>>${en}</option>
	</#list>
	</select>
	<#elseif element.type=='radio'>
	<#list element.values.entrySet() as en>
	<label for="${id}_${en.key}" class="radio inline"><input id="${id}_${en.key}" type="radio" name="${name}" value="${en.key}"<#if element.readonly> readonly</#if><#if element.disabled> disabled</#if><#if element.value??&&element.value==en.key> checked</#if> class="custom <#if element.cssClass?has_content> ${element.cssClass}</#if>"> ${getText(en.value)}</label>
	</#list>
	<#elseif element.type=='checkbox'>
	<#list element.values.entrySet() as en>
	<label for="${id}_${en.key}" class="checkbox inline"><input id="${id}_${en.key}" type="checkbox" name="${name}" value="${en.key}"<#if element.readonly> readonly</#if><#if element.disabled> disabled</#if><#if element.arrayValues??&&element.arrayValues?seq_contains(en.key)> checked</#if> class="custom <#if element.cssClass?has_content> ${element.cssClass}</#if>"> ${getText(en.value)}</label>
	</#list>
	<#else>
	<input id="${id}" type="${element.inputType}" name="${name}"<#if element.value?has_content> value="${element.value}"</#if><#if element.readonly> readonly</#if><#if element.disabled> disabled</#if> <#if element.cssClass?has_content> class="${element.cssClass}"</#if><#list element.dynamicAttributes.entrySet() as en> ${en.key}="${en.value}"</#list>/>
	</#if>
	</div>
</div>
</#if>
</@resourcePresentConditional>
</#macro>