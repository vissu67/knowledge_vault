<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Dashboard</legend>
  <table>
    <tr>
      <td>User visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.dashboard.userVisible}">
            <input name="prf_dashboard_user_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_dashboard_user_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Mail visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.dashboard.mailVisible}">
            <input name="prf_dashboard_mail_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_dashboard_mail_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>News visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.dashboard.newsVisible}">
            <input name="prf_dashboard_news_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_dashboard_news_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>General visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.dashboard.generalVisible}">
            <input name="prf_dashboard_general_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_dashboard_general_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Workflow visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.dashboard.workflowVisible}">
            <input name="prf_dashboard_workflow_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_dashboard_workflow_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Keywords visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.dashboard.keywordsVisible}">
            <input name="prf_dashboard_keywords_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_dashboard_keywords_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>