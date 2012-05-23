<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Stacks</legend>
  <table>
    <tr>
      <td>Taxonomy visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.stack.taxonomyVisible}">
            <input name="prf_stack_taxonomy_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_stack_taxonomy_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Categories visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.stack.categoriesVisible}">
            <input name="prf_stack_categories_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_stack_categories_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Thesaurus visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.stack.thesaurusVisible}">
            <input name="prf_stack_thesaurus_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_stack_thesaurus_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Templates visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.stack.templatesVisible}">
            <input name="prf_stack_templates_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_stack_templates_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Personal visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.stack.personalVisible}">
            <input name="prf_stack_personal_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_stack_personal_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Mail visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.stack.mailVisible}">
            <input name="prf_stack_mail_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_stack_mail_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Trash visible</td>
      <td>
        <c:choose>
          <c:when test="${prf.stack.trashVisible}">
            <input name="prf_stack_trash_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_stack_trash_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>