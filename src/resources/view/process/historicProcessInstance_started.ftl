<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>发起的流程</title>
</head>
<body>
<#assign columns={"value.name":{"width":"150px"},"key.startTime":{"width":"130px"},"key.endTime":{"width":"130px"},"key.deleteReason":{}}>

<#assign bottomButtons='
<button type="button" class="btn reload">${action.getText("reload")}</button>
'>
<#assign actionColumnButtons='
<button type="button" class="btn" data-view="view" data-windowoptions="{\'width\':\'80%\',\'height\':650}">${action.getText("view")}</button>
'>

<@richtable entityName="historicProcessInstance" action="${actionBaseUrl}/started" columns=columns actionColumnButtons=actionColumnButtons bottomButtons=bottomButtons searchable=false celleditable=false/>
</body>
</html></#escape>