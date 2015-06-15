<#macro processFormElement name>
<#assign fe=formElements[name]/>
<#assign id=fe.id!/>
<#if !id?has_content>
<#assign id='form_'+name/>
</#if>
<#assign hidden=fe.disabled&&!fe.value?has_content/>
<#if fe.type=='listpick'>
	<div<#if hidden> style="display:none;"</#if> class="control-group <#if fe.readonly||fe.disabled>_</#if>listpick" data-options="{'url':'<@url value=fe.dynamicAttributes['pickUrl']/>'}">
		<@s.hidden id=id name=name value=fe.value! disabled=fe.disabled class="listpick-id "+fe.cssClass/>
		<label class="control-label">${action.getText(fe.label)}</label>
		<div class="controls<#if fe.readonly||fe.disabled> text</#if>">
		<span class="listpick-name"><#if taskVariables?? && taskVariables[name]??><#if taskVariables[name].fullname??>${taskVariables[name].fullname!}<#else>${taskVariables[name]!}</#if></#if></span>
		</div>
	</div>
<#else>
<div<#if hidden> style="display:none;"</#if> class="control-group">
	<label class="control-label" for="${id}">${action.getText(fe.label)}</label>
	<div class="controls">
	<#if fe.type=='textarea'>
	<textarea id="${id}" name="${name}"<#if fe.readonly> readonly</#if><#if fe.disabled> disabled</#if> <#if fe.cssClass?has_content> class="${fe.cssClass}"</#if><#list fe.dynamicAttributes.entrySet() as en> ${en.key}="${en.value}"</#list>>${fe.value!}</textarea>
	<#elseif fe.type=='select'>
	<#if fe.readonly><input id="${id}" type="hidden" name="${name}"<#if fe.value?has_content> value="${fe.value}"</#if><#if fe.cssClass?has_content> class="${fe.cssClass}"</#if><#list fe.dynamicAttributes.entrySet() as en> ${en.key}="${en.value}"</#list>/></#if>
	<select id="${id}" name="${name}"<#if fe.readonly||fe.disabled> disabled</#if> <#if fe.cssClass?has_content> class="${fe.cssClass}"</#if><#list fe.dynamicAttributes.entrySet() as en> ${en.key}="${en.value}"</#list>>
	<option></option>
	<#list fe.values.entrySet() as en>
	<option value="${en.key}"<#if fe.value??&&fe.value==en.key> selected</#if>>${en.value}</option>
	</#list>
	</select>
	<#elseif fe.type=='enum'>
	<#if fe.readonly><input id="${id}" type="hidden" name="${name}"<#if fe.value?has_content> value="${fe.value}"</#if><#if fe.cssClass?has_content> class="${fe.cssClass}"</#if><#list fe.dynamicAttributes.entrySet() as en> ${en.key}="${en.value}"</#list>/></#if>
	<select id="${id}" name="${name}"<#if fe.readonly||fe.disabled> disabled</#if> <#if fe.cssClass?has_content> class="${fe.cssClass}"</#if>>
	<option></option>
	<#list statics[fe.dynamicAttributes['enumType']].values() as en>
	<option value="${en.name()}"<#if fe.value??&&fe.value==en.name()> selected</#if>>${en}</option>
	</#list>
	</select>
	<#elseif fe.type=='radio'>
	<#list fe.values.entrySet() as en>
	<label for="${id}_${en.key}" class="radio inline"><input id="${id}_${en.key}" type="radio" name="${name}" value="${en.key}"<#if fe.readonly> readonly</#if><#if fe.disabled> disabled</#if><#if fe.value??&&fe.value==en.key> checked</#if> class="custom <#if fe.cssClass?has_content> ${fe.cssClass}</#if>"> ${action.getText(en.value)}</label>
	</#list>
	<#else>
	<input id="${id}" type="${fe.inputType}" name="${name}"<#if fe.value?has_content> value="${fe.value}"</#if><#if fe.readonly> readonly</#if><#if fe.disabled> disabled</#if> <#if fe.cssClass?has_content> class="${fe.cssClass}"</#if><#list fe.dynamicAttributes.entrySet() as en> ${en.key}="${en.value}"</#list>/>
	</#if>
	</div>
</div>
</#if>
</#macro>