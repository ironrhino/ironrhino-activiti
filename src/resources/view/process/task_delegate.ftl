<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>${action.getText('delegate')}</title>
</head>
<body>
<form action="${actionBaseUrl}/delegate" method="post" class="ajax form-horizontal disposable">
	<@s.hidden name="id"/>
	<div class="control-group listpick" data-options="{'url':'<@url value="/user/pick?enabled=true"/>','name':'#assignee-control','nameindex':2,'id':'#assignee','idindex':1}">
		<@s.hidden id="assignee" name="assignee" cssClass="required"/>
		<label class="control-label" for="assignee-control">${action.getText('assignee')}</label>
		<div class="controls">
		<span id="assignee-control"></span>
		</div>
	</div>
	<@s.submit value="%{getText('submit')}" cssClass="btn-primary"/>
</form>
</body>
</html></#escape>