<ul class="nav">
  <li><a href="<@url value="/"/>">${action.getText("index")}</a></li>
  <@authorize ifAnyGranted="ROLE_ADMINISTRATOR">
  <li><a href="<@url value="/user"/>">${action.getText("user")}</a></li>
</@authorize>
</ul>