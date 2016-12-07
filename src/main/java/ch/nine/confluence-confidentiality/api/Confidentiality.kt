package ch.nine.`confluence-confidentiality`.api

import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

/**
 * Created by cmaeder on 01.12.16.
 *
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