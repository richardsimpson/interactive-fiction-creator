package uk.co.rjsoftware.adventure.view

import groovy.transform.TypeChecked

@TypeChecked
class LoadEvent {

    private File file

    LoadEvent(File file) {
        this.file = file
    }

    File getFile() {
        this.file
    }
}

