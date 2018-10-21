package uk.co.rjsoftware.adventure.view

import groovy.transform.TypeChecked

@TypeChecked
interface IPlayerAppView {

    void say(String outputText)

    void sayWithoutLineBreak(String outputText)

    void addCommandListener(CommandListener listener)

    void addLoadListener(LoadListener listener)

    void loadAdventure(String title, String introduction)
}

@TypeChecked
interface LoadListener {
    void callback(LoadEvent event)
}

@TypeChecked
interface CommandListener {
    void callback(CommandEvent event)
}
