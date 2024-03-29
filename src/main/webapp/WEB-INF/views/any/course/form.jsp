<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://www.the-acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="any.course.form.label.code" path="code"/>
	<acme:input-textbox code="any.course.form.label.title" path="title"/>
	<acme:input-textbox code="any.course.form.label.anAbstract" path="anAbstract"/>
	<acme:input-money code="any.course.form.label.retailPrice" path="retailPrice"/>
	<acme:input-url code="any.course.form.label.furtherInformation" path="furtherInformation"/>
	<acme:input-textbox code="any.course.form.label.courseType" path="courseType"/>
	<acme:input-money code="any.course.form.label.targetRetailPrice" path="targetRetailPrice" readonly="true"/>
	
	<jstl:if test="${authorised == true}">
		<acme:button code="authenticated.practica.form.button" action="/authenticated/practicum/list?courseId=${id}"/>
	</jstl:if>
</acme:form>