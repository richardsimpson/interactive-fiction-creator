package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
class Verb {

    private final UUID id
    private String name              // name to use in the scripts
    private String displayName       // name to display when asking the player to disambiguate
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

    void setName(String name) {
        this.name = name
    }

    String getDisplayName() {
        this.displayName
    }

    void setDisplayName(String displayName) {
        this.displayName = displayName
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

    void setSynonyms(List<String> synonyms) {
        this.synonyms.clear()
        this.synonyms.addAll(synonyms)
    }

}
