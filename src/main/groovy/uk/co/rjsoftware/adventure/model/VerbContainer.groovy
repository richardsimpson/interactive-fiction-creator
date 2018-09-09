package uk.co.rjsoftware.adventure.model

interface VerbContainer {

    Map<CustomVerb, String> getVerbs()

    void addVerb(CustomVerb verb, String script)

}
