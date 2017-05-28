package uk.co.rjsoftware.adventure.controller

import uk.co.rjsoftware.adventure.view.{CommandEvent, MainWindow}

/**
  * Created by richardsimpson on 20/05/2017.
  */
class MainWindowForTesting extends MainWindow {

    private var listeners: List[CommandEvent => Unit] = Nil
    private var messages : List[String] = Nil

    override def say(outputText: String): Unit = {
        this.messages :+= outputText
    }

    override def addListener(listener: (CommandEvent) => Unit): Unit = {
        this.listeners ::= listener
    }

    def fireCommand(event: CommandEvent): Unit = {
        for (listener <- this.listeners) {
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

}
