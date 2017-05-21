package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 20/05/2017.
  */
class CustomVerb(private val verbWords:List[String],
                 private var synonyms:List[List[String]]) extends Verb(verbWords, synonyms, prepositionRequired=false, nounRequired=true) {

    def this(verbWords:List[String]) {
        this(verbWords, Nil)
    }
}
