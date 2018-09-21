package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
class Verb {

    private final String friendlyName
    private final List<String> synonyms

    Verb(String friendlyName, List<String> synonyms) {
        this.friendlyName = friendlyName
        this.synonyms = synonyms
    }

    String getFriendlyName() {
        this.friendlyName
    }

    String getVerb() {
        this.synonyms.get(0)
    }

    void addSynonym(String synonym) {
        this.synonyms.add(synonym)
    }

    List<String> getSynonyms() {
        this.synonyms
    }

}
