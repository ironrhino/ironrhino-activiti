<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>我的任务</title>
</head>
<body>
<#assign columns={"value.name":{"alias":"流程名","width":"100px"},"key.name":{"alias":"任务名","width":"100px"},"key.description":{"alias":"任务描述"},"key.createTime":{"width":"130px"},"key.dueDate":{"width":"130px"},"key.suspended":{"width":"70px"}}>
<#assign actionColumnButtons=r'
<#if !entity.key.suspended>
<#if !entity.key.assignee??>
<button type="button" class="btn" data-action="claim">签收</button>
<#else>
<button type="button" class="btn" data-action="unclaim">撤销</button>
<button type="button" class="btn" data-view="form">办理</button>
</#if>
</#if>
'>
<#assign bottomButtons='
<button type="button" class="btn reload">${action.getText("reload")}</button>
'>
<@richtable entityName="task" columns=columns bottomButtons=bottomButtons actionColumnButtons=actionColumnButtons searchable=false celleditable=false/>
</body>
</html></#escape>