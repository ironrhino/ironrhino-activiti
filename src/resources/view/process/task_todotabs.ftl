<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>我的任务</title>
</head>
<body>

<#assign dataurl=actionBaseUrl+"/todolist"/>
<#if request.queryString??>
<#assign dataurl+='?'+request.queryString>
</#if>
<ul class="nav nav-tabs">
	<li class="active"><a href="#todolist_all" data-toggle="tab">${getText('all')}</a></li>
	<li><a href="#todolist_assigned" data-toggle="tab">${getText('assigned')}</a></li>
	<li><a href="#todolist_candidate" data-toggle="tab">待签收</a></li>
</ul>
<div class="tab-content">
	<div id="todolist_all" class="tab-pane ajaxpanel active" data-url="${dataurl}">
	</div>
	<div id="todolist_assigned" class="tab-pane ajaxpanel manual" data-url="${dataurl+dataurl?contains('?')?then('&','?')}tab=type&type=assigned">
	</div>
	<div id="todolist_candidate" class="tab-pane ajaxpanel manual" data-url="${dataurl+dataurl?contains('?')?then('&','?')}tab=type&type=candidate">
	</div>
</div>
</body>
</html></#escape>