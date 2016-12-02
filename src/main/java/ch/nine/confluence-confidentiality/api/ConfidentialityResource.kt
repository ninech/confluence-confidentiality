package ch.nine.`confluence-confidentiality`.api

import com.atlassian.confluence.core.ContentPropertyManager
import com.atlassian.confluence.pages.PageManager
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created by cmaeder on 01.12.16.
 *
 */
@Path ("confluence-confidentiality")
@Produces (MediaType.APPLICATION_JSON)
@Scanned
class ConfidentialityResource constructor(@ComponentImport val pageManager: PageManager,
                              @ComponentImport val contentPropertyManager: ContentPropertyManager) {
    @GET
    fun getConfidentiality(@QueryParam("pageId") pageId: Long): Response {
        try {
            val page = pageManager.getPage(pageId)
            val confidentiality = contentPropertyManager.getStringProperty(page, "ch.nine.confluence-confidentiality.value")

            val response = Confidentiality(confidentiality ?: "confidential")

            return Response.ok(response).build()
        } catch (e: Exception) {
            return Response.serverError().build()
        }
    }

    @POST
    fun setConfidentiality(@QueryParam("pageId") pageId: Long): Response {
        return Response.serverError().build()
    }
}