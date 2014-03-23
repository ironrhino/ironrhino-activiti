<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>查看流程历史</title>
</head>
<body>
<div class="diagram processInstance" data-pid="${historicProcessInstance.id}"></div>
</body>
</html></#escape>