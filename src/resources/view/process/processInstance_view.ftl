<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>查看流程实例</title>
</head>
<body>
<div class="diagram processInstance" data-pid="${processInstance.id}"></div>
</body>
</html></#escape>