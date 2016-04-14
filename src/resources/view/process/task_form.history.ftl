<#assign templateName="/resources/view/process/form/"+processDefinitionKey/>
<#if formKey?has_content>
	<#assign templateName+="_"+formKey/>
</#if>
<#assign templateName+=".history.ftl"/>
<@resourcePresentConditional value=templateName>
<#include templateName>
</@resourcePresentConditional>
<@resourcePresentConditional value=templateName negated=true>
<#if !historicProcessInstance??>
<#if processDefinition?? && processDefinition.description?has_content>
<div class="alert alert-block">
${processDefinition.description}
</div>
</#if>
<#else>
<div class="accordion" id="accordion">
	<div class="accordion-group">
		<div class="accordion-heading">
			<h4 style="text-align:center;"><a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion" href="#history">${action.getText('historyTrack')}</a></h4>
		</div>
		<div id="history" class="accordion-body collapse<#if !historicTaskInstances?? || historicTaskInstances?size lt 4> in</#if>">
			<div class="accordion-inner">
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
						<td><#if historicProcessInstance.startUserId?has_content><span class="user" data-username="${historicProcessInstance.startUserId}">${(beans['userDetailsService'].loadUserByUsername(historicProcessInstance.startUserId,true))!}</span></#if></td>
						<td>${historicProcessInstance.startTime?datetime}</td>
						<td></td>
						<td></td>
					</tr>
					<#if historicTaskInstances?? && !historicTaskInstances.empty>
					<#list historicTaskInstances as task>
					<tr>
						<td>${action.getText(task.name)}</td>
						<td>
						<#if task.assignee?has_content><span class="user" data-username="${task.assignee}">${(beans['userDetailsService'].loadUserByUsername(task.assignee,true))!}</span></#if>
						<#if task.owner?? && task.assignee?? && task.owner!=task.assignee> (<span class="user" data-username="${task.owner!}">${(beans['userDetailsService'].loadUserByUsername(task.owner,true))!}</span>)</#if>
						</td>
						<td>${task.startTime?datetime}</td>
						<td>${(task.claimTime?datetime)!}</td>
						<td>${task.endTime?datetime}</td>
					</tr>
					</#list>
					</#if>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
</#if>
</@resourcePresentConditional>