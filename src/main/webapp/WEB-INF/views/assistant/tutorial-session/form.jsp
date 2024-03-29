<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://www.the-acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="assistant.tutorial.session.form.label.title" path="title"/>
	<acme:input-textarea code="assistant.tutorial.session.form.label.abstractSession" path="abstractSession"/>
	<acme:input-moment code="assistant.tutorial.session.form.label.startDate" path="startDate"/>
	<acme:input-moment code="assistant.tutorial.session.form.label.finishDate" path="finishDate"/>
	<acme:input-url code="assistant.tutorial.session.form.label.info" path="info"/>
	
	
	<acme:input-select code="assistant.tutorial.session.form.label.type" path="sessionType" choices="${sessionType}"/>
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">			
			<acme:submit code="assistant.tutorial.session.form.button.update" action="/assistant/tutorial-session/update"/>
			<acme:submit code="assistant.tutorial.session.form.button.delete" action="/assistant/tutorial-session/delete"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="assistant.tutorial.session.form.button.create" action="/assistant/tutorial-session/create?masterId=${masterId}"/>
		</jstl:when>		
	</jstl:choose>
</acme:form>