package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 20/05/2017.
  */
// TODO: Does this class change the value of synonyms in the calling code?
class Verb(private val verbWords:List[String],
           private var synonyms:List[List[String]],
           private val prepositionRequired:Boolean,
           private val nounRequired:Boolean) {

    this.synonyms ::= this.verbWords

    def this(verbWords:List[String], prepositionRequired:Boolean, nounRequired:Boolean) {
        this(verbWords, Nil, prepositionRequired, nounRequired)
    }
    def getVerb : String = {
        this.verbWords.mkString(" ")
    }

    def getVerbWords : List[String] = {
        this.verbWords
    }

    def getSynonyms : List[List[String]] = {
        this.synonyms
    }

    def isPrepositionRequired : Boolean = {
        this.prepositionRequired
    }

    def isNounRequired : Boolean = {
        this.nounRequired
    }

}
