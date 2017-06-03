package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 20/05/2017.
  */
class CustomVerb(friendlyName:String, synonyms:List[String]) extends Verb(friendlyName, synonyms) {

    def this(friendlyName:String, name:String) {
        this(friendlyName, List(name))
    }

}
