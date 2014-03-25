<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>我的任务</title>
</head>
<body>
<#assign columns={"processDefinition.name":{"alias":"流程名","width":"100px"},
"historicProcessInstance.startUserId":{"alias","startUser","width":"80px","template":r'<span class="user" data-username="${value}">${statics["org.ironrhino.core.util.ApplicationContextUtils"].getBean("userManager").loadUserByUsername(value)!}</span>'},
"historicProcessInstance.startTime":{"alias":"发起时间","width":"130px"},
"task.name":{"alias":"任务名","width":"100px"},
"task.createTime":{"alias":"任务创建时间","width":"130px"},
"task.dueDate":{"width":"80px"},
"task.description":{"alias":"任务描述"},
"task.suspended":{"width":"60px"}}>
<#assign actionColumnButtons=r'
<#if !entity.task.suspended>
<#if !entity.task.assignee??>
<button type="button" class="btn" data-action="claim">签收</button>
<#else>
<button type="button" class="btn" data-view="form">办理</button>
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
<@richtable entityName="task" columns=columns bottomButtons=bottomButtons actionColumnButtons=actionColumnButtons searchable=false celleditable=false/>
</body>
</html></#escape>