<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fieldset>
  <legend>Misc</legend>
  <table>
    <tr>
      <td>Quota limit</td>
      <td><input class=":integer :only_on_blur" name="prf_misc_user_quota" value="${prf.misc.userQuota}"/></td>
    </tr>
    <tr>
      <td>Advanced filters</td>
      <td>
        <c:choose>
          <c:when test="${prf.misc.advancedFilters}">
            <input name="prf_misc_advanced_filter" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_misc_advanced_filter" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Web skin</td>
      <td>
        <select name="prf_misc_web_skin">
          <c:choose>
            <c:when test="${prf.misc.webSkin == 'default'}">
              <option value="default" selected="selected">default</option>
            </c:when>
            <c:otherwise><option value="default">default</option></c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${prf.misc.webSkin == 'test'}">
              <option value="test" selected="selected">test</option>
            </c:when>
            <c:otherwise><option value="test">test</option></c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${prf.misc.webSkin == 'mediumfont'}">
              <option value="mediumfont" selected="selected">mediumfont</option>
            </c:when>
            <c:otherwise><option value="mediumfont">mediumfont</option></c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${prf.misc.webSkin == 'bigfont'}">
              <option value="bigfont" selected="selected">bigfont</option>
            </c:when>
            <c:otherwise><option value="bigfont">bigfont</option></c:otherwise>
          </c:choose>
        </select>
      </td>
    </tr>
    <tr>
      <td>Print preview</td>
      <td>
        <c:choose>
          <c:when test="${prf.misc.printPreview}">
            <input name="prf_misc_print_preview" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_misc_print_preview" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Keywords enabled</td>
      <td>
        <c:choose>
          <c:when test="${prf.misc.keywordsEnabled}">
            <input name="prf_misc_keywords_enabled" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_misc_keywords_enabled" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Extensions</td>
      <td>
        <select multiple="multiple" name="prf_misc_extensions" size="4">
          <c:forEach var="ext" items="${exts}">
            <c:choose>
              <c:when test="${fn:contains(prf.misc.extensions, ext.uuid)}">
                <option value="${ext.uuid}" selected="selected">${ext.name}</option>
              </c:when>
              <c:otherwise>
                <option value="${ext.uuid}">${ext.name}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
    <tr>
      <td>Reports</td>
      <td>
        <select multiple="multiple" name="prf_misc_reports" size="4">
          <c:forEach var="rep" items="${reps}">
            <c:choose>
              <c:when test="${fn:contains(prf.misc.reports, rep.id)}">
                <option value="${rep.id}" selected="selected">${rep.name}</option>
              </c:when>
              <c:otherwise>
                <option value="${rep.id}">${rep.name}</option>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </select>
      </td>
    </tr>
  </table>
</fieldset>