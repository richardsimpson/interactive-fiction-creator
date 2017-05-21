package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 20/05/2017.
  */
// TODO: Does this class change the value of synonyms in the calling code?
class Verb(private val verb:String,
           private var synonyms:List[String],
           private val prepositionRequired:Boolean,
           private val nounRequired:Boolean) {

    this.synonyms ::= this.verb

    def this(verb:String, prepositionRequired:Boolean, nounRequired:Boolean) {
        this(verb, Nil, prepositionRequired, nounRequired)
    }
    def getVerb : String = {
        this.verb
    }

    def getSynonyms : List[String] = {
        this.synonyms
    }

    def isPrepositionRequired : Boolean = {
        this.prepositionRequired
    }

    def isNounRequired : Boolean = {
        this.nounRequired
    }

}
