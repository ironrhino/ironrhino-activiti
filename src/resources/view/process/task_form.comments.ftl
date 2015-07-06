<#assign templateName="/resources/view/process/form/"+processDefinitionKey/>
<#if formKey?has_content>
	<#assign templateName+="_"+formKey/>
</#if>
<#assign templateName+=".comments.ftl"/>
<@resourcePresentConditional value=templateName>
<#include templateName>
</@resourcePresentConditional>
<@resourcePresentConditional value=templateName negated=true>
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
</@resourcePresentConditional>