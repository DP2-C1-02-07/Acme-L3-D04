<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://www.the-acme-framework.org/"%>

<acme:form>
	<acme:input-moment code="authenticated.offer.form.label.instantiationMoment" path="instantiationMoment"/>	
	<acme:input-textarea code="authenticated.offer.form.label.heading" path="heading"/>
	<acme:input-textarea code="authenticated.offer.form.label.summary" path="summary"/>
	<acme:input-moment code="authenticated.offer.form.label.availabilityStart" path="availabilityStart"/>
	<acme:input-moment code="authenticated.offer.form.label.availabilityEnd" path="availabilityEnd"/>
	<acme:input-money code="authenticated.offer.form.label.price" path="price"/>
	<acme:input-url code="authenticated.offer.form.label.link" path="link"/>
	<acme:input-money code="authenticated.offer.form.label.targetPrice" path="targetPrice" readonly="true"/>
</acme:form>