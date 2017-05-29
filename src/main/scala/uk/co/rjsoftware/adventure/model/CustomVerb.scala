package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 20/05/2017.
  */
// TODO: Check if this should be declared private or override.  Do we end up with a synonyms attribute in the custom verb as well?
class CustomVerb(private val synonyms:List[String]) extends Verb(synonyms) {

}
