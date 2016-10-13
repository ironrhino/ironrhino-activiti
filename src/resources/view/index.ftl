<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>${getText('index')}</title>
</head>
<body>
<div class="portal savable">

	<ul class="portal-column">
		<li id="todoList" class="portlet">
			<div class="portlet-header">待办任务</div>
			<div class="portlet-content">
				<div class="ajaxpanel" data-url="<@url value="/todoList"/>" data-interval="60000"></div>
			</div>
		</li>
	</ul>

	
</div>
</body>
</html></#escape>