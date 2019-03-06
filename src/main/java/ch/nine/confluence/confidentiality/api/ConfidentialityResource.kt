package ch.nine.confluence.confidentiality.api

import ch.nine.confluence.confidentiality.service.ConfidentialityService
import com.atlassian.confluence.pages.Page
import com.atlassian.confluence.pages.PageManager
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
import org.apache.log4j.LogManager
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Controller that's responsible for validating permissions
 * and calls backend services that perform business logic.
 */
@Scanned
@Produces(MediaType.APPLICATION_JSON)
@Path("confluence-confidentiality")
class ConfidentialityResource constructor(@ComponentImport val pageManager: PageManager,
                                          @ComponentImport val service: ConfidentialityService) {
    companion object {
        private val log = LogManager.getLogger(this::class.java.name.substringBefore("\$Companion"))
    }

    @GET
    fun getConfidentiality(@QueryParam("pageId") pageId: Long): Response {
        return try {
            val page = pageManager.getPage(pageId)
            when {
                (page == null) -> notFound()
                (!canUserView(page)) -> forbidden()
                else -> Response.ok(service.getConfidentiality(page)).build()
            }
        } catch (e: Exception) {
            log.error("Exception occurred while trying to get page with id: $pageId", e)
            return serverError()
        }
    }

    @POST
    fun setConfidentiality(@QueryParam("pageId") pageId: Long,
                           @FormParam("confidentiality") newConfidentiality: String): Response {
        return try {
            val page = pageManager.getPage(pageId)
            when {
                page == null -> notFound()
                !(canUserView(page)) -> forbidden()
                !(canUserEdit(page)) -> forbidden()
                else -> Response.ok(service.saveConfidentiality(page, newConfidentiality)).build()
            }
        } catch (e: Exception) {
            log.error("Exception occurred while trying to save confidentiality: $newConfidentiality, for page id: $pageId", e)
            return serverError()
        }
    }

    private fun canUserEdit(page: Page?): Boolean {
        return service.canUserEdit(page)
    }

    private fun canUserView(page: Page?): Boolean {
        return service.canUserView(page)
    }

    private fun serverError() = Response.serverError().build()

    private fun forbidden() = Response.status(Response.Status.FORBIDDEN).build()

    private fun notFound() = Response.status(Response.Status.NOT_FOUND).build()
}
