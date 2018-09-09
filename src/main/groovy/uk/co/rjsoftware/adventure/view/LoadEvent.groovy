package uk.co.rjsoftware.adventure.view

class LoadEvent {

    private File file

    LoadEvent(File file) {
        this.file = file
    }

    File getFile() {
        this.file
    }
}

