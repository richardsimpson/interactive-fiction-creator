package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 20/05/2017.
  */
class CustomVerb(private val verb:String,
                 private var synonyms:List[String]) extends Verb(verb, synonyms, prepositionRequired=false, nounRequired=true) {

    def this(verb:String) {
        this(verb, Nil)
    }
}
