<!DOCTYPE html>
<html>
    <head>
        <title>Item List</title>
    </head>
    <body>
        <c:url var="logoutUrl" value="/logout"/>
        <form action="${logoutUrl}" method="post">
            <input type="submit" value="Log out" />
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
        <h1>Auction</h1>
        <h2>List of the Items:</h2>
        <a href="<c:url value="/ticket/create" />">Create a new item</a><br /><br />
        <c:choose>
            <c:when test="${fn:length(ticketDatabase) == 0}">
                <i>There are no items in the system.</i>
            </c:when>
            <c:otherwise>
                <c:forEach items="${ticketDatabase}" var="entry">
                    Item ${entry.key}:
                    <a href="<c:url value="/ticket/itemview/${entry.key}" />">
                        <c:out value="${entry.value.subject}" /></a>
                    (customer: <c:out value="${entry.value.customerName}" />)
                    <security:authorize access="hasRole('ADMIN') or principal.username=='${entry.value.customerName}'">
                        [<a href="<c:url value="/ticket/editpage/${entry.key}" />">Edit</a>]
                    </security:authorize>
                    <security:authorize access="hasRole('ADMIN')">
                        [<a href="<c:url value="/ticket/delete/${entry.key}" />">Delete</a>]
                    </security:authorize>
                    <br />
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </body>
</html>
