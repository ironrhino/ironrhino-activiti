<#ftl output_format='HTML'>
<#assign templateName="/resources/view/process/form/"+processDefinitionKey/>
<#if formKey?has_content>
	<#assign templateName+="_"+formKey/>
</#if>
<#assign templateName+=".attachments.ftl"/>
<@resourcePresentConditional value=templateName>
<#include templateName>
</@resourcePresentConditional>
<@resourcePresentConditional value=templateName negated=true>
<#if attachments?? && !attachments.empty>
	<table class="table">
			<caption style="background-color: #bebec5;"><h5>${getText('attachment')}</h5></caption>
			<thead>
			<tr>
				<th style="width:200px;">${getText('file')}</th>
				<th style="width:100px;">${getText('owner')}</th>
				<th>${getText('description')}</th>
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
				<a href="${actionBaseUrl}/deleteAttachment/${attachment.id}" class="btn ajax deleteRow">${getText('delete')}</a>
				</#if>
				</td>
			</tr>
			</#list>
			</tbody>
	</table>	
</#if>
</@resourcePresentConditional>