package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 20/05/2017.
  */
class CustomVerb(private val synonyms:List[String], private val script:String) extends Verb(synonyms) {

    def getScript : String = {
        this.script
    }
}
