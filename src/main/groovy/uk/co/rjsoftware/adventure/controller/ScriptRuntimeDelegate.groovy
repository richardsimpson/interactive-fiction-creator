package uk.co.rjsoftware.adventure.controller

import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.model.Room

@TypeChecked
class ScriptRuntimeDelegate {

    private final AdventureController adventureController

    ScriptRuntimeDelegate(AdventureController adventureController) {
        this.adventureController = adventureController
    }

    void say(String message) {
        adventureController.say(message)
    }

    void sayWithoutLineBreak(String outputText) {
        adventureController.sayWithoutLineBreak(outputText)
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

    void movePlayerTo(String roomName) {
        adventureController.movePlayerTo(roomName)
    }

    void moveItemTo(String itemName, String itemContainerName) {
        adventureController.moveItemTo(itemName, itemContainerName)
    }

    Item getItem(String itemName) {
        adventureController.getItem(itemName)
    }

    Room getRoom(String roomName) {
        adventureController.getRoom(roomName)
    }

    Room getCurrentRoom() {
        adventureController.getCurrentRoom()
    }

    Item getPlayer() {
        adventureController.getPlayer()
    }

    void increaseScore(int amount) {
        adventureController.increaseScore(amount)
    }

    void decreaseScore(int amount) {
        adventureController.decreaseScore(amount)
    }

    int getScore() {
        adventureController.getScore()
    }

    int getTurnCounter() {
        adventureController.getTurnCounter()
    }

    void endGame() {
        adventureController.endGame()
    }
}
