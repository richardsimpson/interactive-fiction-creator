package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
interface VerbContainer {

    void addVerb(CustomVerb verb, String script)

    boolean containsVerb(CustomVerb verb)

    String getVerbScript(CustomVerb verb)

}
