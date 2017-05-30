package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 20/05/2017.
  */
class CustomVerb(synonyms:List[String]) extends Verb(synonyms) {

    def this(name:String) {
        this(List(name))
    }

}
