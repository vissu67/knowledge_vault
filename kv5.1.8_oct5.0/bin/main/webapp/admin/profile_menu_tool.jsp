<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Menu tool</legend>
  <table>
    <tr>
      <td>Languages visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.menu.tool.languagesVisible}">
            <input name="prf_menu_tool_languages_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_tool_languages_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Skin visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.menu.tool.skinVisible}">
            <input name="prf_menu_tool_skin_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_tool_skin_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Debug visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.menu.tool.debugVisible}">
            <input name="prf_menu_tool_debug_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_tool_debug_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Administration visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.menu.tool.administrationVisible}">
            <input name="prf_menu_tool_administration_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_tool_administration_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Preferences visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.menu.tool.preferencesVisible}">
            <input name="prf_menu_tool_preferences_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_tool_preferences_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>