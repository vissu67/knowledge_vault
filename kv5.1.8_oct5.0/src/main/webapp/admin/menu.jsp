<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="com.openkm.extension.dao.ExtensionDAO"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="isMultipleInstancesAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
<table width="100%" border="0">
  <tr>
  <td align="center" width="100%">
  <a target="frame" href="home.jsp"><img src="img/toolbar/home.png" title="Home"></a>
  &nbsp;
  <c:if test="${isMultipleInstancesAdmin}">
    <a target="frame" href="Config"><img src="img/toolbar/config.png" title="Configuration"></a>
    &nbsp;
  </c:if>
  <a target="frame" href="MimeType"><img src="img/toolbar/mime.png" title="Mime types"></a>
  &nbsp;
  <a target="frame" href="stats.jsp"><img src="img/toolbar/stats.png" title="Statistics"></a>
  &nbsp;
  <c:if test="${isMultipleInstancesAdmin}">
    <a target="frame" href="scripting.jsp"><img src="img/toolbar/scripting.png" title="Scripting"></a>
    &nbsp;
    <a target="frame" href="RepositorySearch"><img src="img/toolbar/search.png" title="Repository Search"></a>
    &nbsp;
    <a target="frame" href="RepositoryView"><img src="img/toolbar/repository.png" title="Repository view"></a>
    &nbsp;
    <a target="frame" href="PropertyGroups"><img src="img/toolbar/properties.png" title="Property groups"></a>
    &nbsp;
  </c:if>
  <a target="frame" href="LoggedUsers"><img src="img/toolbar/logged.png" title="Logged users"></a>
  &nbsp;
  <a target="frame" href="Auth"><img src="img/toolbar/users.png" title="Users"></a>
  &nbsp;
  <a target="frame" href="Profile"><img src="img/toolbar/profile.png" title="Profiles"></a>
  &nbsp;
  <%--
  <a target="frame" href="DocumentFilter"><img src="img/toolbar/filter.png" title="Document filter"></a>
  &nbsp;
  --%>
  <c:if test="${isMultipleInstancesAdmin}">
    <a target="frame" href="DatabaseQuery"><img src="img/toolbar/database.png" title="Database query"></a>
    &nbsp;
  </c:if>
  <a target="frame" href="Report"><img src="img/toolbar/report.png" title="Reports"></a>
  &nbsp;
  <a target="frame" href="ActivityLog"><img src="img/toolbar/activity.png" title="Activity log"></a>
  &nbsp;
  <a target="frame" href="Workflow"><img src="img/toolbar/workflow.png" title="Workflow"></a>
  &nbsp;
  <c:if test="${isMultipleInstancesAdmin}">
    <a target="frame" href="CronTab"><img src="img/toolbar/crontab.png" title="Crontab"></a>
    &nbsp;
    <a target="frame" href="generate_thesaurus.jsp"><img src="img/toolbar/thesaurus.png" title="Generate thesaurus"></a>
    &nbsp;
    <a target="frame" href="Language"><img src="img/toolbar/language.png" title="Language"></a>
    &nbsp;
    <a target="frame" href="repository_import.jsp"><img src="img/toolbar/import.png" title="Repository import"></a>
    &nbsp;
    <a target="frame" href="repository_export.jsp"><img src="img/toolbar/export.png" title="Repository export"></a>
  </c:if>
  <script type="text/javascript">
    // Identify if being loaded inside an iframe
    if (self == top) {
      document.write('&nbsp;\n');
      document.write('<a href="logout.jsp"><img src="img/toolbar/exit.png" title="Exit"></a>\n');
    }
  </script>
  </td>
  <c:if test="${isMultipleInstancesAdmin}">
    <td><a target="frame" href="experimental.jsp">&nbsp;</a></td>
  </c:if>
  </tr>
</table>