package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 04/06/2017.
  */
trait VerbContainer {

    def getVerbs : Map[CustomVerb, String]

    def addVerb(verb:CustomVerb, script:String) : Unit

}
