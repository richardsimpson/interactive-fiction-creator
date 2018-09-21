package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
interface VerbContainer {

    Map<CustomVerb, Closure> getVerbs()

    void addVerb(CustomVerb verb, Closure closure)

}
