package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
class Verb {

    private final String id                // id to refer to the verb within script
    private final String friendlyName      // name to display when asking the player to disambiguate
    private final List<String> synonyms    // synonyms for the verb

    Verb(String id, String friendlyName, List<String> synonyms) {
        this.id = id
        this.friendlyName = friendlyName
        this.synonyms = synonyms
    }

    String getId() {
        this.id
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
