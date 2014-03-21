<ul class="nav">
  <li><a href="<@url value="/"/>">${action.getText("index")}</a></li>
  <@authorize ifAnyGranted="ROLE_ADMINISTRATOR">
  <li><a href="<@url value="/user"/>">${action.getText("user")}</a></li>
  <li><a href="<@url value="/process/processDefinition"/>">流程部署</a></li>
  <li><a href="<@url value="/process/processInstance"/>">流程实例</a></li>
  </@authorize>
  <li class="dropdown">
 	<a href="#" class="dropdown-toggle" data-toggle="dropdown">
      	运行中的流程
    </a>
    <ul class="dropdown-menu">
        <li><a href="<@url value="/process/processInstance/started"/>">发起的流程</a></li>
  		<li><a href="<@url value="/process/processInstance/involved"/>">经办的流程</a></li>
    </ul>
  </li>
  <li class="dropdown">
 	<a href="#" class="dropdown-toggle" data-toggle="dropdown">
      	办结的流程
    </a>
    <ul class="dropdown-menu">
        <li><a href="<@url value="/process/historicProcessInstance/started"/>">发起的流程</a></li>
  		<li><a href="<@url value="/process/historicProcessInstance/involved"/>">经办的流程</a></li>
    </ul>
  </li>
  <li><a href="<@url value="/process/task"/>">我的任务</a></li>
  <@authorize ifAnyGranted="user">
  <li><a href="<@url value="/leave"/>">我的请假</a></li>
  </@authorize>
</ul>