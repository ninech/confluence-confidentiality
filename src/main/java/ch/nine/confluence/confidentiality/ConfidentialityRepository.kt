package ch.nine.confluence.confidentiality

import com.atlassian.confluence.core.ContentPropertyManager
import com.atlassian.confluence.pages.Page
/**
 * Repository responsible for storing and receiving confidentiality value, per page.
 */
class ConfidentialityRepository constructor(private val contentPropertyManager: ContentPropertyManager) {
    private val managerConfidentialityKey = "ch.nine.confluence-confidentiality.value"
    private val defaultConfidentiality = "confidential"
    fun getConfidentiality(page: Page?): String {
        return contentPropertyManager.getStringProperty(page, managerConfidentialityKey) ?: defaultConfidentiality
    }

    fun save(page: Page, newConfidentiality: String): String {
        contentPropertyManager.setStringProperty(page, managerConfidentialityKey, newConfidentiality)
        return contentPropertyManager.getStringProperty(page, managerConfidentialityKey) ?: defaultConfidentiality
    }

}