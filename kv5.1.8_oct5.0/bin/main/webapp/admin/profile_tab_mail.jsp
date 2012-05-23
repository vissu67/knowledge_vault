<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Tab mail</legend>
  <table>
    <tr>
      <td>Properties visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.tab.mail.propertiesVisible}">
            <input name="prf_tab_mail_properties_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_mail_properties_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Security visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.tab.mail.securityVisible}">
            <input name="prf_tab_mail_security_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_mail_security_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>