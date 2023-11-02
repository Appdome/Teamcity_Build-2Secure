<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--@elvariable id="text" type="java.lang.String"--%>

<b>This is the report</b>
<p>
    <c:out value="${text}" default="no content to display"/><br/>
</p>

<embed src="</Users/eli/TeamCity/buildAgent/work/692458e1ddfbe443/artifacts/certificate.pdf>" type="application/pdf" width="100%" height="600px" />