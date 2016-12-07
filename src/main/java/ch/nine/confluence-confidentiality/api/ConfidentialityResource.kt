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
            val userCanViewPage = canUserView(page)

            if (!userCanViewPage) {
                return forbidden()
            }

            val confidentiality = contentPropertyManager.getStringProperty(page, "ch.nine.confluence-confidentiality.value")

            val response = Confidentiality(confidentiality ?: "confidential", canUserEdit(page))

            return ok(response)
        } catch (e: Exception) {
            return serverError()
        }
    }

    @POST
    fun setConfidentiality(@QueryParam("pageId") pageId: Long,
                           @FormParam("confidentiality") newConfidentiality: String): Response {
        try {
            val page = pageManager.getPage(pageId)
            val userCanViewPage = canUserView(page)

            if (!userCanViewPage) {
                return forbidden()
            }

            val userCanEditPage = canUserEdit(page)

            if (userCanEditPage) {
                contentPropertyManager.setStringProperty(page, "ch.nine.confluence-confidentiality.value", newConfidentiality)

                val confidentiality = contentPropertyManager.getStringProperty(page, "ch.nine.confluence-confidentiality.value")

                val response = Confidentiality(confidentiality ?: "confidential", userCanEditPage)

                return ok(response)
            } else {
                return forbidden()
            }
        } catch (e: Exception) {
            return serverError()
        }
    }

    private fun canUserEdit(page: Page?): Boolean {
        return canUserDo(Permission.EDIT, page)
    }

    private fun canUserView(page: Page?): Boolean {
        return canUserDo(Permission.VIEW, page)
    }

    private fun canUserDo(permission: Permission?, page: Page?): Boolean {
        val confluenceUser = AuthenticatedUserThreadLocal.get()
        val userCanEdit = permissionManager.hasPermission(confluenceUser, permission, page)
        return userCanEdit
    }

    private fun ok(response: Confidentiality) = Response.ok(response).build()

    private fun serverError() = Response.serverError().build()

    private fun forbidden() = Response.status(Response.Status.FORBIDDEN).build()
}
