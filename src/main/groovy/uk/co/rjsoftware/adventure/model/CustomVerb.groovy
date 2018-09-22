package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
class CustomVerb extends Verb {

    CustomVerb(String id, String friendlyName, List<String> synonyms) {
        super(id, friendlyName, synonyms)
    }

    CustomVerb(String id, String friendlyName, String command) {
        this(id, friendlyName, [command])
    }

}
