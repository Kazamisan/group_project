<!DOCTYPE html>
<html>
    <head>
        <title>Item Addition</title>
    </head>
    <body>
        <c:url var="logoutUrl" value="/logout"/>
        <form action="${logoutUrl}" method="post">
            <input type="submit" value="Log out" />
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>

        <h2>Create an item:</h2>
        <form:form method="POST" enctype="multipart/form-data" modelAttribute="ticketForm">
            <form:label path="subject">Item Name:</form:label><br/>
            <form:input type="text" path="subject" /><br/><br/>
            <form:label path="body">Description:</form:label><br/>
            <form:textarea path="body" rows="5" cols="30" /><br/><br/>
            <form:label path="price">Initial Price:</form:label><br/>
            <form:input type="text" path="price"/><br/><br/>
            <form:label path="countdown">Time Set (In second)</form:label><br/>
            <form:input type="text" path="countdown"/><br/><br/>
            <b>Attachments</b><br/>
            <input type="file" name="attachments" multiple="multiple"/><br/><br/>
            <input type="submit" value="Submit"/>
        </form:form>
    </body>
</html>
