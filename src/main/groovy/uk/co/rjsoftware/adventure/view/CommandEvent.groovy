package uk.co.rjsoftware.adventure.view

import groovy.transform.TypeChecked

@TypeChecked
class CommandEvent {

    private String command

    CommandEvent(String command) {
        this.command = command
    }

    String getCommand() {
        this.command
    }

}
