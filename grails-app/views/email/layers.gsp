<%@ page contentType="text/html"%>
<html>
  <head><title>Email alert from ${grailsApplication.config.skin.orgNameLong}</title></head>
  <body>
    <h3>Email alert from ${grailsApplication.config.skin.orgNameLong}</h3>
    <h2>${title}</h2>
    <p><g:message code="${message}" default="${message}"/></p>
    <p><a href="${moreInfo}">View details of the added/changed layers</a></p>
    <style type="text/css">
    	body { font-family:Arial; }
    	table { border-collapse: collapse; border: 1px solid #CCC; padding:2px; }
		td, th { border: 1px solid #CCC; padding:4px; }
        img { max-width:150px; max-height:150px; }
    </style>
    <g:if test="${records}">
    <h3>Spatial layers update</h3>
    <table style="border-collapse: collapse; border: 1px solid #CCC; padding:2px;">
    	<thead>
			<th>Name</th>
			<th>Description</th>
            <th>&nbsp;</th>
    	</thead>
    <g:each in="${records}" var="layer">
      <tbody>
      <tr>
        <td>
          <strong>${layer.name}</strong><br/>
          ${layer.description}
        </td>
        <td>
           <a href="${grailsApplication.config.spatial.baseURL}/layers/more/${layer.name}">View details of ${layer.name}</a>
        </td>
      </tr>
      </tbody>
    </g:each>
    </table>
    </g:if>

    <g:render template="/email/unsubscribe"/>

  </body>
</html>