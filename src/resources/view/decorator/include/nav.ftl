<ul class="nav">
  <li><a href="<@url value="/"/>">${action.getText("index")}</a></li>
  <@authorize ifAnyGranted="ROLE_ADMINISTRATOR">
  <li><a href="<@url value="/user"/>">${action.getText("user")}</a></li>
  <li><a href="<@url value="/process/processDefinition"/>">流程定义</a></li>
  <li><a href="<@url value="/process/processInstance"/>">流程实例</a></li>
</@authorize>
</ul>