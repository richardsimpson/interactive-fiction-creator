package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 20/05/2017.
  */
class Verb(private val friendlyName:String, private var synonyms:List[String]) {

    def getFriendlyName : String = {
        this.friendlyName
    }

    def getVerb : String = {
        this.synonyms.head
    }

    def addSynonym(synonym : String) : Unit = {
        this.synonyms = this.synonyms :+ synonym
    }

    def getSynonyms : List[String] = {
        this.synonyms
    }

}
