<link href="<@url value="/assets/styles/app${modernBrowser?string('-min','-ie')}.css"/>" media="all" rel="stylesheet" type="text/css" />
<script src="<@url value="/assets/scripts/app${modernBrowser?string('-min','')}.js"/>" type="text/javascript"<#if modernBrowser&&!head?contains('</script>')> defer</#if>></script>
