package uk.co.rjsoftware.adventure.model;

import groovy.transform.TypeChecked;

@TypeChecked
enum ContentVisibility {
    ALWAYS("Always"),
    AFTER_EXAMINE("After Item Examined"),
    NEVER("Never")

    private final String friendlyName

    ContentVisibility(String friendlyName) {
        this.friendlyName = friendlyName
    }

    String getFriendlyName() {
        this.friendlyName
    }
}
