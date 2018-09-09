package uk.co.rjsoftware.adventure.controller

class ScheduledScript {

    private int turnToExecuteOn
    private Closure<Void> script

    ScheduledScript(int turnToExecuteOn, Closure<Void> script) {
        this.turnToExecuteOn = turnToExecuteOn
        this.script = script
    }

    int getTurnToExecuteOn() {
        this.turnToExecuteOn
    }

    Closure<Void> getScript() {
        this.script
    }
}
