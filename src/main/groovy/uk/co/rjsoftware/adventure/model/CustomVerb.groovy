package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
class CustomVerb extends Verb {

    CustomVerb(String friendlyName, List<String> synonyms) {
        super(friendlyName, synonyms)
    }

    CustomVerb(String friendlyName, String name) {
        this(friendlyName, [name])
    }

}
