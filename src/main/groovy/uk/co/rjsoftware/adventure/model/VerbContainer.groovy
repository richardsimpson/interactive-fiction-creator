package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
interface VerbContainer {

    void addVerb(CustomVerb verb, Closure closure)

    boolean containsVerb(CustomVerb verb)

    Closure getVerbClosure(CustomVerb verb)

}
