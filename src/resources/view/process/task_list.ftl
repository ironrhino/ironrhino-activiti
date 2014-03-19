<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>待办列表</title>
</head>
<body>
<#assign columns={"owner":{"width":"100px"},"name":{"width":"100px"},"description":{},"createDate":{},"dueDate":{},"suspended":{}}>
<#assign actionColumnButtons=r'
<#if !entity.assignee??>
<button type="button" class="btn" data-action="claim">签收</button>
<#else>
<button type="button" class="btn" data-action="unclaim">撤销</button>
<button type="button" class="btn" data-view="form">办理</button>
</#if>
'>
<#assign bottomButtons='
<button type="button" class="btn reload">${action.getText("reload")}</button>
'>
<@richtable entityName="task" columns=columns bottomButtons=bottomButtons actionColumnButtons=actionColumnButtons searchable=false celleditable=false/>
</body>
</html></#escape>