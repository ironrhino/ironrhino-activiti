<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>请假</title>
</head>
<body>
<@s.form action="${actionBaseUrl}/start" method="post" cssClass="form-horizontal ajax">
	<@s.textfield label="%{getText('startTime')}" name="leave.startTime" cssClass="required date"/>
	<@s.textfield label="%{getText('endTime')}" name="leave.endTime" cssClass="required date"/>
	<@s.textarea label="%{getText('reason')}" name="leave.reason" cssClass="required"/>
	<@s.submit value="%{getText('submit')}" />
</@s.form>
</body>
</html></#escape>


