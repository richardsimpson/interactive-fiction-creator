package uk.co.rjsoftware.adventure.view

/**
  * Created by richardsimpson on 20/05/2017.
  */
trait MainWindow {

    def say(outputText:String) : Unit

    def addListener(listener: CommandEvent => Unit) : Unit
}
