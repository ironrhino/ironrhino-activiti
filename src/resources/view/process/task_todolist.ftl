<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>我的任务</title>
</head>
<body>
<#assign columns={
"historicProcessInstance.id":{"alias":"流程ID","width":"100px"},
"processDefinition.name":{"alias":"流程名"},
"historicProcessInstance.startUserId":{"alias","startUser","width":"80px","template":r'<span class="user" data-username="${value}">${statics["org.ironrhino.core.util.ApplicationContextUtils"].getBean("userManager").loadUserByUsername(value)!}</span>'},
"historicProcessInstance.startTime":{"alias":"发起时间","width":"130px"},
"task.name":{"alias":"任务名","width":"100px"},
"task.createTime":{"alias":"任务创建时间","width":"130px"},
"task.dueDate":{"width":"80px"},
"task.suspended":{"width":"60px"}}>
<#assign actionColumnButtons=r'
<#if !entity.task.suspended>
<#if !entity.task.assignee??>
<button type="button" class="btn" data-action="claim">签收</button>
<#else>
'+'
<button type="button" class="btn" data-view="form" data-windowoptions="{\'width\':\'80%\'}">办理</button>
'+r'
<#if !entity.task.delegationState??>
<button type="button" class="btn" data-view="delegate">${action.getText("delegate")}</button>
<button type="button" class="btn" data-action="unclaim">撤销</button>
</#if>
</#if>
</#if>
'>
<#assign bottomButtons='
<button type="button" class="btn reload">${action.getText("reload")}</button>
'>
<@richtable entityName="task" action="${actionBaseUrl}/todolist" columns=columns bottomButtons=bottomButtons actionColumnButtons=actionColumnButtons searchable=false celleditable=false/>
</body>
</html></#escape>