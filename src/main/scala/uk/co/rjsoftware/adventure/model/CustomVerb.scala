package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 20/05/2017.
  */
// TODO: Note that the subclass constructor params that are passed through DO NOT have val or var before them.  Update the other classes like this.
class CustomVerb(synonyms:List[String]) extends Verb(synonyms) {

    def this(name:String) {
        this(List(name))
    }

}
