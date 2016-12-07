package ch.nine.`confluence-confidentiality`.api

import com.atlassian.confluence.core.ContentPropertyManager
import com.atlassian.confluence.pages.Page
import com.atlassian.confluence.pages.PageManager
import com.atlassian.confluence.security.Permission
import com.atlassian.confluence.security.PermissionManager
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal
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
                              @ComponentImport val contentPropertyManager: ContentPropertyManager,
                                          @ComponentImport val permissionManager: PermissionManager) {
    @GET
    fun getConfidentiality(@QueryParam("pageId") pageId: Long): Response {
        try {
            val page = pageManager.getPage(pageId)
            val confidentiality = contentPropertyManager.getStringProperty(page, "ch.nine.confluence-confidentiality.value")

            val response = Confidentiality(confidentiality ?: "confidential", canUserEdit(page))

            return Response.ok(response).build()
        } catch (e: Exception) {
            return Response.serverError().build()
        }
    }

    private fun canUserEdit(page: Page?): Boolean {
        val confluenceUser = AuthenticatedUserThreadLocal.get()
        val userCanEdit = permissionManager.hasPermission(confluenceUser, Permission.EDIT, page)
        return userCanEdit
    }

    @POST
    fun setConfidentiality(@QueryParam("pageId") pageId: Long,
                           @FormParam("confidentiality") newConfidentiality: String): Response {
        try {
            val page = pageManager.getPage(pageId)
            val userCanEdit = canUserEdit(page)

            if (userCanEdit) {
                contentPropertyManager.setStringProperty(page, "ch.nine.confluence-confidentiality.value", newConfidentiality)

                val confidentiality = contentPropertyManager.getStringProperty(page, "ch.nine.confluence-confidentiality.value")

                val response = Confidentiality(confidentiality ?: "confidential", userCanEdit)

                return Response.ok(response).build()
            } else {
                return Response.status(Response.Status.FORBIDDEN).build()
            }
        } catch (e: Exception) {
            return Response.serverError().build()
        }
    }
}
