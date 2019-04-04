package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
class CustomVerb extends Verb {

    CustomVerb(String name, String displayName, List<String> synonyms) {
        super(name, displayName, synonyms)
    }

    CustomVerb(String name, String displayName, String command) {
        this(name, displayName, [command])
    }

}
