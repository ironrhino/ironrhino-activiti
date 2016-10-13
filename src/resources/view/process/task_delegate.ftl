<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>${getText('delegate')}</title>
</head>
<body>
<form action="${actionBaseUrl}/delegate" method="post" class="ajax form-horizontal">
	<@s.hidden name="id"/>
	<div class="control-group listpick" data-options="{'url':'<@url value="/user/pick?columns=username,name&enabled=true"/>','idindex':1,'nameindex':2}">
		<@s.hidden id="assignee" name="assignee" class="required listpick-id"/>
		<label class="control-label" for="assignee-control">${getText('assignee')}</label>
		<div class="controls">
		<span class="listpick-name"></span>
		</div>
	</div>
	<@s.submit value=getText('submit') class="btn-primary"/>
</form>
</body>
</html></#escape>