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
"assignee":{"alias":"当前处理人","width":"100px","template":r'<#if entity.historicActivityInstance??&&entity.historicActivityInstance.assignee?has_content><span class="user" data-username="${entity.historicActivityInstance.assignee}">${statics["org.ironrhino.core.util.ApplicationContextUtils"].getBean("userDetailsService").loadUserByUsername(entity.historicActivityInstance.assignee,true)!}</span></#if>'}
}>

<#assign bottomButtons='
<button type="button" class="btn confirm" data-action="delete" data-shown="selected" data-filterselector=":not([data-deletable=\'false\'])">${action.getText("delete")}</button>'
+r'<@btn class="reload"/>'>
<#assign actionColumnButtons=r'<@btn view="view" '+'windowoptions="{\'width\':\'80%\',\'height\':650}"/>'>
<@authorize ifAnyGranted="ROLE_ADMINISTRATOR">
<#assign actionColumnButtons=actionColumnButtons+r'
<#if entity.processInstance.suspended>
<@btn action="activate" confirm=true/>
<#else>
<@btn action="suspend" confirm=true/>
</#if>
'>
</@authorize>

<@richtable entityName="processInstance" columns=columns rowDynamicAttributes=r'{"data-deletable":"${entity.processInstance.suspended?string}"}' actionColumnButtons=actionColumnButtons bottomButtons=bottomButtons searchable=false celleditable=false/>
</body>
</html></#escape>