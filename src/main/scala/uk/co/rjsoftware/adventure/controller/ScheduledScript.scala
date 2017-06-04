package uk.co.rjsoftware.adventure.controller

import groovy.lang.Closure

/**
  * Created by richardsimpson on 29/05/2017.
  */
class ScheduledScript(private var turnToExecuteOn:Int, private val script:Closure[Unit]) {

    def getTurnToExecuteOn : Int = {
        this.turnToExecuteOn
    }

    def getScript() : Closure[Unit] = {
        this.script
    }
}
