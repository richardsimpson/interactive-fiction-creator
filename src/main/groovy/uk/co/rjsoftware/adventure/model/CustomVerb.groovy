package uk.co.rjsoftware.adventure.model

class CustomVerb extends Verb {

    CustomVerb(String friendlyName, List<String> synonyms) {
        super(friendlyName, synonyms)
    }

    CustomVerb(String friendlyName, String name) {
        this(friendlyName, [name])
    }

}
