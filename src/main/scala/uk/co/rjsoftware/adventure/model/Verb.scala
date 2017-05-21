package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 20/05/2017.
  */
// TODO: Does this class change the value of synonyms in the calling code?
class Verb(private val synonyms:List[String]) {

    def getVerb : String = {
        this.synonyms.head
    }

    def getSynonyms : List[String] = {
        this.synonyms
    }

}
