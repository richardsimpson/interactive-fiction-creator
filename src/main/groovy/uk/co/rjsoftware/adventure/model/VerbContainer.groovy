package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
interface VerbContainer {

    void addVerb(CustomVerb verb, CustomVerbInstance verbInstance)

    boolean containsVerb(CustomVerb verb)

    String getVerbScript(CustomVerb verb)

}
