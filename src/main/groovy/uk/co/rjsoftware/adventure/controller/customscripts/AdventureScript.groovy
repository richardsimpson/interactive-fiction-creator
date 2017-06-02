package uk.co.rjsoftware.adventure.controller.customscripts

import uk.co.rjsoftware.adventure.controller.AdventureController

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

    public void setVisible(String itemName) {
        adventureController.setVisible(itemName, true)
    }

    public void setInvisible(String itemName) {
        adventureController.setVisible(itemName, false)
    }

}