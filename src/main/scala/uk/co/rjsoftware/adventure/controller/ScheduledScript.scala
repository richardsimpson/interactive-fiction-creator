package uk.co.rjsoftware.adventure.controller

import groovy.lang.Closure

/**
  * Created by richardsimpson on 29/05/2017.
  */
class ScheduledScript(private var turnCount:Int, private val script:Closure[Unit]) {

    def getTurnCount : Int = {
        this.turnCount
    }

    def decrementTurnCount : Unit = {
        this.turnCount -= 1
    }

    def getScript() : Closure[Unit] = {
        this.script
    }
}
