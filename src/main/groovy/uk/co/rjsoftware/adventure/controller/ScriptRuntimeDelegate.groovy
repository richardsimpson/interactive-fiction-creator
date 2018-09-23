package uk.co.rjsoftware.adventure.controller

import groovy.transform.TypeChecked

@TypeChecked
class ScriptRuntimeDelegate {

    private final AdventureController adventureController

    ScriptRuntimeDelegate(AdventureController adventureController) {
        this.adventureController = adventureController
    }

    void say(String message) {
        adventureController.say(message)
    }

    boolean isSwitchedOn(String itemName) {
        adventureController.isSwitchedOn(itemName);
    }

    boolean isSwitchedOff(String itemName) {
        !adventureController.isSwitchedOn(itemName);
    }

    boolean isOpen(String itemName) {
        adventureController.isOpen(itemName)
    }

    boolean isClosed(String itemName) {
        !adventureController.isOpen(itemName)
    }

    void executeAfterTurns(int turns, Closure scriptToExecute) {
        adventureController.executeAfterTurns(turns, scriptToExecute)
    }

    void setVisible(String itemName) {
        adventureController.setVisible(itemName, true)
    }

    void setInvisible(String itemName) {
        adventureController.setVisible(itemName, false)
    }

    boolean playerInRoom(String roomName) {
        adventureController.playerInRoom(roomName)
    }

    boolean playerNotInRoom(String roomName) {
        !adventureController.playerInRoom(roomName)
    }

    boolean movePlayerTo(String roomName) {
        adventureController.movePlayerTo(roomName)
    }
}
