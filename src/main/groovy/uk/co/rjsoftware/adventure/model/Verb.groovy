package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
class Verb {

    private final UUID id
    private final String name              // name to use in the scripts
    private final String displayName       // name to display when asking the player to disambiguate
    private final List<String> synonyms    // synonyms for the verb

    Verb(String name, String displayName, List<String> synonyms) {
        this.id = UUID.randomUUID()
        this.name = name
        this.displayName = displayName
        this.synonyms = synonyms
    }

    UUID getId() {
        this.id
    }

    String getName() {
        this.name
    }

    String getDisplayName() {
        this.displayName
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
