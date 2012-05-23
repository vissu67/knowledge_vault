<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Tab document</legend>
  <table>
    <tr>
      <td>Properties visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.tab.document.propertiesVisible}">
            <input name="prf_tab_document_properties_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_document_properties_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Security visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.tab.document.securityVisible}">
            <input name="prf_tab_document_security_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_document_security_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Notes visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.tab.document.notesVisible}">
            <input name="prf_tab_document_notes_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_document_notes_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Versions visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.tab.document.versionsVisible}">
            <input name="prf_tab_document_versions_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_document_versions_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Preview visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.tab.document.previewVisible}">
            <input name="prf_tab_document_preview_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_document_preview_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Property groups visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.tab.document.propertyGroupsVisible}">
            <input name="prf_tab_document_property_groups_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_tab_document_property_groups_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>