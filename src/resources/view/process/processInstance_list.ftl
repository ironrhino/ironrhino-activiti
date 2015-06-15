<!DOCTYPE html>
<#escape x as x?html><html>
<head>
<title>流程实例</title>
</head>
<body>
<#assign columns={"processInstance.id":{"alias":"流程实例ID","width":"100px"},
"processInstance.processDefinitionId":{"alias":"流程定义ID","width":"150px","template":r"<#if id?has_content>${value}<#else><a href='${actionBaseUrl}/list/${value}' class='ajax view'>${value}</a></#if>"},
"processDefinition.name":{"alias":"流程名"},
"processInstance.businessKey":{"alias":"流程业务键值","width":"100px"},
"historicProcessInstance.startUserId":{"alias","startUser","width":"80px","template":r'<#if value?has_content><span class="user" data-username="${value}">${statics["org.ironrhino.core.util.ApplicationContextUtils"].getBean("userDetailsService").loadUserByUsername(value,true)!}</span></#if>'},
"historicProcessInstance.startTime":{"alias":"发起时间","width":"130px"},
"activityName":{"alias":"当前活动","width":"80px","template",r'${(entity.historicActivityInstance.activityName)!}'},
"assignee":{"alias":"当前处理人","width":"100px","template":r'<#if entity.historicActivityInstance??&&entity.historicActivityInstance.assignee?has_content><span class="user" data-username="${entity.historicActivityInstance.assignee}">${statics["org.ironrhino.core.util.ApplicationContextUtils"].getBean("userDetailsService").loadUserByUsername(entity.historicActivityInstance.assignee,true)!}</span></#if>'},
"processInstance.suspended":{"alias":"挂起","width":"50px"}
}>

<#assign bottomButtons='
<button type="button" class="btn confirm" data-action="delete" data-shown="selected" data-filterselector="[data-suspended=\'true\']:not([data-deletable=\'false\'])">${action.getText("delete")}</button>
'+r'<@authorize ifAnyGranted="ROLE_ADMINISTRATOR">'+'
<button type="button" class="btn confirm" data-action="activate" data-shown="selected" data-filterselector="[data-suspended=\'true\']">${action.getText("activate")}</button>
<button type="button" class="btn confirm" data-action="suspend" data-shown="selected" data-filterselector=":not([data-suspended=\'true\'])">${action.getText("suspend")}</button>
'+r'</@authorize>'
+r'<@btn class="reload"/>'>
<#assign actionColumnButtons=r'<@btn view="view" '+'windowoptions="{\'width\':\'80%\',\'height\':650}"/>'>

<@richtable entityName="processInstance" columns=columns rowDynamicAttributes=r'{"data-deletable":"${entity.processInstance.suspended?string}"}' actionColumnButtons=actionColumnButtons bottomButtons=bottomButtons searchable=false celleditable=false rowDynamicAttributes="<#if entity.processInstance.suspended>{'data-suspended':'true'}</#if>"/>
</body>
</html></#escape>