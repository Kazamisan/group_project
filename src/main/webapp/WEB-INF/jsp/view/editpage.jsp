<!DOCTYPE html>
<html>
    <head>
        <title>Edit page</title>
    </head>
    <body>
        <c:url var="logoutUrl" value="/logout"/>
        <form action="${logoutUrl}" method="post">
            <input type="submit" value="Log out" />
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>

        <h2>Ticket #${ticketId}</h2>
        <form:form method="POST" enctype="multipart/form-data" modelAttribute="ticketForm">
            <form:label path="subject">Item name:</form:label><br/>
            <form:input type="text" path="subject" /><br/><br/>
            <form:label path="body">Description:</form:label><br/>
            <form:textarea path="body" rows="5" cols="30" /><br/><br/>
            <form:label path="price">Initial Price:</form:label><br/>
            <form:input type="text" path="price"/><br/><br/>
            <form:label path="countdown"> Countdown</form:label><br/><br/>
            <form:input type="text" path="countdown"/><br/><br/>
            <c:if test="${ticket.numberOfAttachments > 0}">
                <b>Attachments:</b><br/>
                <ul>
                    <c:forEach items="${ticket.attachments}" var="attachment">
                        <li>
                            <c:out value="${attachment.name}" />
                            [<a href="<c:url value="/ticket/${ticketId}/delete/${attachment.name}" />">Delete</a>]
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
            <b>Add attachments</b><br />
            <input type="file" name="attachments" multiple="multiple"/><br/><br/>
            <input type="submit" value="Save"/>
        </form:form>
    </body>
</html>
