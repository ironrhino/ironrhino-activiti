<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>${action.getText('delegate')}</title>
</head>
<body>
<form action="${actionBaseUrl}/delegate" method="post" class="ajax form-horizontal disposable">
	<@s.hidden name="id"/>
	<@s.textfield label="%{getText('assignee')}" name="assignee" cssClass="required"/>
	<@s.submit value="%{getText('submit')}" cssClass="btn-primary"/>
</form>
</body>
</html></#escape>