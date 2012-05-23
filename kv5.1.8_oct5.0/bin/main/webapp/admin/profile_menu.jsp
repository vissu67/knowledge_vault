<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Menu</legend>
  <table>
    <tr>
      <td>File visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.menu.fileVisible}">
            <input name="prf_menu_file_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_file_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Edit visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.menu.editVisible}">
            <input name="prf_menu_edit_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_edit_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Tools visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.menu.toolsVisible}">
            <input name="prf_menu_tools_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_tools_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Bookmark visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.menu.bookmarksVisible}">
            <input name="prf_menu_bookmarks_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_bookmarks_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Help visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.menu.helpVisible}">
            <input name="prf_menu_help_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_help_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>