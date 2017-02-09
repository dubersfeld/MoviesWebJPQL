<spring:message code="title.welcome" var="pageTitle" />
<template:main htmlTitle="${pageTitle}" bodyTitle="${pageTitle}">

 <jsp:attribute name="navigationContent"><br/>
 	<a href="<c:url value="${backPage}" />"><spring:message code="backPage"/></a><br/>
	<a href="<c:url value="/backHome" />"><spring:message code="index.backHome"/></a><br/>
 
 <h2>An error happened</h2>
 
 
  </jsp:attribute>

</template:main>