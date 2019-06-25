package ch.nine.confluence.confidentiality.service

import ch.nine.confluence.confidentiality.ConfidentialityRepository
import ch.nine.confluence.confidentiality.api.Confidentiality
import ch.nine.confluence.confidentiality.auditlog.AuditLogger
import com.atlassian.confluence.pages.Page
import com.atlassian.confluence.security.Permission
import com.atlassian.confluence.security.PermissionManager
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal
import com.atlassian.confluence.user.ConfluenceUser
import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.log4j.LogManager
import java.util.Optional.ofNullable

/**
 * Class responsible for managing confidentiality response per page.
 * Checks user permissions, and sends confidentiality changed events.
 */
class ConfidentialityService constructor(private val repository: ConfidentialityRepository,
                                         private val logger: AuditLogger,
                                         private val permissionManager: PermissionManager) {
    companion object {
        private val log = LogManager.getLogger(this::class.java.name.substringBefore("\$Companion"))
    }

    fun getConfidentiality(page: Page?): Confidentiality {
        val confidentiality = repository.getConfidentiality(page)
        return Confidentiality(confidentiality, canUserEdit(page))
    }

    fun saveConfidentiality(page: Page, newConfidentiality: String): Confidentiality {
        val oldConfidentiality = repository.getConfidentiality(page)
        val confidentiality = repository.save(page, newConfidentiality)
        auditLog(oldConfidentiality, newConfidentiality, page)
        return Confidentiality(confidentiality, canUserEdit(page))
    }

    private fun auditLog(old: String, new: String, page: Page) {
        val user = ofNullable(AuthenticatedUserThreadLocal.get()).orElseThrow { RuntimeException("User cannot be unauthenticated!") }
        val change = ImmutablePair.of(old, new)
        log.info("User: ${user.name}, ${user.fullName} is changing confidentiality for page id: ${page.id}, change: $change")
        logger.confidentialityChanged(page, change, user, permissionManager.isSystemAdministrator(user))
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