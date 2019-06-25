package ch.nine.confluence.confidentiality.auditlog

import com.atlassian.confluence.api.model.audit.AffectedObject
import com.atlassian.confluence.api.model.audit.AuditRecord
import com.atlassian.confluence.api.model.people.User
import com.atlassian.confluence.api.service.audit.AuditService
import com.atlassian.confluence.pages.Page
import com.atlassian.confluence.user.ConfluenceUser
import com.opensymphony.webwork.ServletActionContext
import org.apache.commons.lang3.tuple.ImmutablePair
import org.apache.log4j.LogManager
import org.joda.time.DateTime
import javax.servlet.http.HttpServletRequest

/**
 * Class responsible for persisting changes of Confidentiality. Creates audit log event, to be stored by confluence.
 */
class AuditLogger constructor(private val storage: AuditService) {
    companion object {
        private val log = LogManager.getLogger(this::class.java.name.substringBefore("\$Companion"))
    }

    fun confidentialityChanged(page: Page, change: ImmutablePair<String, String>, byUser: ConfluenceUser, isSysAdm: Boolean) {
        val request: HttpServletRequest? = ServletActionContext.getRequest()

        val logDescription = "Confidentiality for page: ${page.id}, space: ${page.spaceKey}, title: '${page.title}', changed from: '${change.left}' to: '${change.right}' , by: ${byUser.name}, ${byUser.fullName}, system admin? $isSysAdm"
        log.info(logDescription)

        val auditRecord = AuditRecord.Builder()
                .affectedObject(AffectedObject
                        .builder()
                        .name("Page id: ${page.id}, space: ${page.spaceKey}")
                        .objectType(page.displayTitle)
                        .build())
                .category("change")
                .createdDate(DateTime.now())
                .description(logDescription)
                .summary("Page id: ${page.id} confidentiality change: $change}")
                .author(User(null, byUser.name, byUser.fullName, byUser.key))
                .isSysAdmin(isSysAdm)
                .remoteAddress(request?.getHeader("X-Forwarded-For") ?: request?.remoteAddr ?: "remote unknown")
                .build()
        storage.storeRecord(auditRecord)
    }

}
