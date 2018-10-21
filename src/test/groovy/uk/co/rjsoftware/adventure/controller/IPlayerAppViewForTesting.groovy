package uk.co.rjsoftware.adventure.controller

import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.view.CommandEvent
import uk.co.rjsoftware.adventure.view.CommandListener
import uk.co.rjsoftware.adventure.view.LoadEvent
import uk.co.rjsoftware.adventure.view.LoadListener
import uk.co.rjsoftware.adventure.view.IPlayerAppView

@TypeChecked
class IPlayerAppViewForTesting implements IPlayerAppView {

    private List<CommandListener> commandListeners = new ArrayList()
    private List<LoadListener> loadListeners = new ArrayList()
    private String messages = ""

    void say(String outputText) {
        this.messages += outputText + System.lineSeparator()
    }

    void sayWithoutLineBreak(String outputText) {
        this.messages += outputText
    }

    void addCommandListener(CommandListener listener) {
        this.commandListeners.add(listener)
    }

    void fireCommand(CommandEvent event) {
        for (CommandListener listener : this.commandListeners) {
            listener.callback(event)
        }
    }

    void addLoadListener(LoadListener listener) {
        this.loadListeners.add(listener)
    }

    void fireLoadCommand(LoadEvent event) {
        for (LoadListener listener : this.loadListeners) {
            listener.callback(event)
        }
    }

    String getLastMessage() {
        this.messages.split(System.lineSeparator()).reverse()[0]
    }

    String[] getMessages() {
        this.messages.split(System.lineSeparator(), -1).dropRight(1)
    }

    void clearMessages() {
        this.messages = ""
    }

    void loadAdventure(String title, String introduction) {

    }
}
