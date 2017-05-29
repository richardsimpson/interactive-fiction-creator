package uk.co.rjsoftware.adventure.controller

public abstract class AdventureScript extends Script {

    private AdventureController adventureController = null

    public void setAdventureController(AdventureController adventureController) {
        this.adventureController = adventureController
    }

    public void say(String message) {
        adventureController.say(message)
    }

    public boolean isSwitchedOn(String itemName) {
        adventureController.isSwitchedOn(itemName);
    }

    public boolean isSwitchedOff(String itemName) {
        !adventureController.isSwitchedOn(itemName);
    }

    public void executeAfterTurns(int turns, Closure scriptToExecute) {
        adventureController.executeAfterTurns(turns, scriptToExecute)
    }

}