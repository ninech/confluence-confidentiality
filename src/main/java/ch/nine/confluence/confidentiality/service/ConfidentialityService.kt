package ch.nine.confluence.confidentiality.service

import ch.nine.confluence.confidentiality.api.Confidentiality
import com.atlassian.confluence.core.ContentPropertyManager
import com.atlassian.confluence.pages.Page
import com.atlassian.confluence.security.Permission
import com.atlassian.confluence.security.PermissionManager
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal
import com.atlassian.confluence.user.ConfluenceUser
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
import org.apache.log4j.LogManager

/**
 * Service responsible for getting appropriate confidentiality response per page.
 */
@Scanned
class ConfidentialityService constructor(@ComponentImport val contentPropertyManager: ContentPropertyManager,
                                         @ComponentImport val permissionManager: PermissionManager) {
    companion object {
        private val log = LogManager.getLogger(this::class.java.name.substringBefore("\$Companion"))
    }
    private val managerConfidentialityKey = "ch.nine.confluence-confidentiality.value"

    fun getConfidentiality(page: Page?): Confidentiality {
        val confidentiality = contentPropertyManager.getStringProperty(page, managerConfidentialityKey)
                ?: "confidential"
        return Confidentiality(confidentiality, canUserEdit(page))
    }

    fun saveConfidentiality(page: Page, newConfidentiality: String): Confidentiality {
        contentPropertyManager.setStringProperty(page, managerConfidentialityKey, newConfidentiality)
        val confidentiality = contentPropertyManager.getStringProperty(page, managerConfidentialityKey) ?: "confidential"
        val user = AuthenticatedUserThreadLocal.get()
        log.info("User: $user is setting new confidentiality: $confidentiality for page: ${page.id}")
        return Confidentiality(confidentiality, canUserEdit(page))
    }

    fun canUserEdit(page: Page?): Boolean {
        return canUserDo(Permission.EDIT, page)
    }

    fun canUserView(page: Page?): Boolean {
        return canUserDo(Permission.VIEW, page)
    }

    private fun canUserDo(permission: Permission?, page: Page?): Boolean {
        return canUserDo(AuthenticatedUserThreadLocal.get(), permission, page)
    }

    private fun canUserDo(user: ConfluenceUser, permission: Permission?, page: Page?): Boolean {
        return permissionManager.hasPermission(user, permission, page)
    }
}