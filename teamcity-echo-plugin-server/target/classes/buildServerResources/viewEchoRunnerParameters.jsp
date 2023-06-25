<%@ page import="jetbrains.teamcity.EchoRunnerConstants" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<c:set var="messageId" value="<%=EchoRunnerConstants.MESSAGE_KEY%>"/>
c:set var="keystore_pass" value="<%=EchoRunnerConstants.KEYSTORE_PASS%>"/>
<c:set var="keystore_alias" value="<%=EchoRunnerConstants.KEYSTORE_ALIAS%>"/>
<c:set var="key_pass" value="<%=EchoRunnerConstants.KEY_PASS%>"/>

<div class="parameter">
    Key Pass: <props:displayValue name="${key_pass}" emptyValue=""/>
</div>