<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Tab folder</legend>
  <table>
    <tr>
      <td>Properties visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.tab.folder.propertiesVisible}">
            <input name="prf_tab_folder_properties_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_folder_properties_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Security visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.tab.folder.securityVisible}">
            <input name="prf_tab_folder_security_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_folder_security_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Notes visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.tab.folder.notesVisible}">
            <input name="prf_tab_folder_notes_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_folder_notes_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>