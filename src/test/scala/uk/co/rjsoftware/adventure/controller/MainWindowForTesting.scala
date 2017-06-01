package uk.co.rjsoftware.adventure.controller

import uk.co.rjsoftware.adventure.view.{CommandEvent, LoadEvent, MainWindow}

/**
  * Created by richardsimpson on 20/05/2017.
  */
class MainWindowForTesting extends MainWindow {

    private var commandListeners: List[CommandEvent => Unit] = Nil
    private var loadListeners: List[LoadEvent => Unit] = Nil
    private var messages : List[String] = Nil

    override def say(outputText: String): Unit = {
        this.messages :+= outputText
    }

    override def addCommandListener(listener: (CommandEvent) => Unit): Unit = {
        this.commandListeners ::= listener
    }

    def fireCommand(event: CommandEvent): Unit = {
        for (listener <- this.commandListeners) {
            listener(event)
        }
    }

    override def addLoadListener(listener: (LoadEvent) => Unit): Unit = {
        this.loadListeners ::= listener
    }

    def fireLoadCommand(event: LoadEvent): Unit = {
        for (listener <- this.loadListeners) {
            listener(event)
        }
    }

    def getLastMessage : String = {
        this.messages.head
    }

    def getMessages : List[String] = {
        this.messages
    }

    def clearMessages() = {
        this.messages = Nil
    }

    override def loadAdventure(title: String, introduction: String): Unit = {

    }
}
