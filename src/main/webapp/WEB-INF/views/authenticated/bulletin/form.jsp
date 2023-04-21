<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://www.the-acme-framework.org/"%>

<acme:form>
	<acme:input-textarea code="authenticated.bulletin.form.label.title" path="title"/>
	<acme:input-moment code="authenticated.bulletin.form.label.instantiationMoment" path="instantiationMoment"/>
		<acme:input-textarea code="authenticated.bulletin.form.label.flag" path="flag"/>
	
	<acme:input-textarea code="authenticated.bulletin.form.label.message" path="message"/>
	<acme:input-url code="authenticated.bulletin.form.label.link" path="link"/>
	
</acme:form>