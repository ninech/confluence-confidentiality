package ch.nine.confluence.confidentiality.api

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

/**
 * Simple transport object, that holds information about confidentiality
 * for JavaScript part of the plugin.
 */
@XmlRootElement
class Confidentiality(private val confidentiality: String,
                      private val canUserEdit: Boolean) {
    private val possibleConfidentialities = arrayOf("public","internal","confidential")

    @XmlElement
    fun getConfidentiality() : String {
        return confidentiality
    }

    @XmlElement
    fun getPossibleConfidentialities() : Array<String> {
        return possibleConfidentialities
    }

    @XmlElement
    fun getCanUserEdit() : Boolean {
        return canUserEdit
    }
}