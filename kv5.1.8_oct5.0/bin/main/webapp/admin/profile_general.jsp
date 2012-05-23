<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>General</legend>
  <table>
    <tr>
      <td>Name</td>
      <td><input class=":required :only_on_blur" name="prf_name" value="${prf.name}"/></td>
    </tr>
    <tr>
      <td>Active</td>
      <td>
        <c:choose>
          <c:when test="${prf.active}">
            <input name="prf_active" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_active" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>