package uk.co.rjsoftware.adventure.model

class CustomVerb extends Verb {

    CustomVerb(String friendlyName, List<String> synonyms) {
        super(friendlyName, synonyms)
    }

    CustomVerb(String friendlyName, String name) {
        this(friendlyName, [name])
    }

    CustomVerb createCopy() {
        final newSynonyms = new ArrayList<>()
        newSynonyms.addAll(getSynonyms())

        new CustomVerb(getFriendlyName(), newSynonyms)
    }

    @Override
    boolean equals(Object obj) {
        if (! obj instanceof CustomVerb) {
            return false
        }

        return friendlyName.equals(((CustomVerb)obj).friendlyName)
    }

    @Override
    int hashCode() {
        return friendlyName.hashCode()
    }
}
