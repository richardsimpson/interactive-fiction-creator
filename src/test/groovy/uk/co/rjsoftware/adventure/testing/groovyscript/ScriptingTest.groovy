package uk.co.rjsoftware.adventure.testing.groovyscript

import org.junit.Before
import org.junit.Test
import uk.co.rjsoftware.adventure.controller.AdventureController
import uk.co.rjsoftware.adventure.controller.MainWindowForTesting
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.Direction
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.CommandEvent

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertEquals

class ScriptingTest {

    private AdventureController controller
    private MainWindowForTesting mainWindow

    private Room study
    private Room livingRoom

    private Item tv
    private Item chest
    private Item dummy
    private Item dummy2

    @Before
    void before() {
        study = new Room("study", "This is your study.")
        livingRoom = new Room("livingRoom", "This is the living room.")

        tv = new Item("tv", ["TV", "television"], "A 28\" TV")
        chest = new Item("chest", ["chest"], "This is the chest.")
        dummy = new Item("dummy", ["dummy"], "This is the dummy item.")
        dummy2 = new Item("dummy2", ["dummy2"], "This is the second dummy item.")

        tv.setVisible(true)
        tv.setSwitchable(true)

        chest.setContainer(true)
        dummy.setContainer(true)
        dummy2.setContainer(true)

        //
        // study -------- livingRoom
        //

        study.addExit(Direction.EAST, livingRoom)
        livingRoom.addExit(Direction.WEST, study)

        final Adventure adventure = new Adventure("intro")

        livingRoom.addItem(tv)
        livingRoom.addItem(chest)
        livingRoom.addItem(dummy)

        study.addItem(dummy2)

        adventure.addRoom(study)
        adventure.addRoom(livingRoom)

        adventure.setStartRoom(livingRoom)

        dummy.setOpen(false)
        dummy2.setOpen(false)

        this.mainWindow = new MainWindowForTesting()
        this.controller = new AdventureController(mainWindow)
        this.controller.loadAdventure(adventure)

        // read back the data from the controller, as it would have made a copy
        study = this.controller.getAdventure().findRoom("study")
        livingRoom = this.controller.getAdventure().findRoom("livingRoom")

        tv = livingRoom.getItem("tv")
        chest = livingRoom.getItem("chest")
        dummy = livingRoom.getItem("dummy")
        dummy2 = study.getItem("dummy2")
    }

    private void assertMessagesAreCorrect(List<String> expectedMessages) {
        final String[] messages = this.mainWindow.getMessages()
        assertEquals(expectedMessages.size(), messages.length)

        for (int index = 0; index < expectedMessages.size(); index++) {
            assertEquals(expectedMessages.get(index), messages[index])
        }
    }

    @Test
    void testSay() {
        dummy.setOnOpenScript("say('hello')")
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "hello",
                ""
        ])
    }

    @Test
    void testIsSwitchedOn() {
        dummy.setOnOpenScript(
                """
              |if (isSwitchedOn('tv')) {
              |    say('tv is switched on')
              |}
              |else {
              |    say('tv is switched off')
              |}
            """.stripMargin())

        tv.switchOn()
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "tv is switched on",
                ""
        ])

        tv.switchOff()
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "tv is switched off",
                ""
        ])
    }

    @Test
    void testIsSwitchedOff() {
        dummy.setOnOpenScript(
                """
              |if (isSwitchedOff('tv')) {
              |    say('tv is switched off')
              |}
              |else {
              |    say('tv is switched on')
              |}
            """.stripMargin())

        tv.switchOn()
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "tv is switched on",
                ""
        ])

        tv.switchOff()
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "tv is switched off",
                ""
        ])
    }

    @Test
    void testIsOpen() {
        dummy.setOnOpenScript(
                """
              |if (isOpen('chest')) {
              |    say('chest is open')
              |}
              |else {
              |    say('chest is closed')
              |}
            """.stripMargin())

        chest.setOpen(true)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "chest is open",
                ""
        ])

        chest.setOpen(false)
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "chest is closed",
                ""
        ])
    }

    @Test
    void testIsClosed() {
        dummy.setOnOpenScript(
                """
              |if (isClosed('chest')) {
              |    say('chest is closed')
              |}
              |else {
              |    say('chest is open')
              |}
            """.stripMargin())

        chest.setOpen(true)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "chest is open",
                ""
        ])

        chest.setOpen(false)
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "chest is closed",
                ""
        ])
    }

    @Test
    void testExecuteAfterTurns() {
        dummy.setOnOpenScript(
                "executeAfterTurns(5) {" +
                        "    say('hello from closure')" +
                        "}")

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("wait"))
        mainWindow.fireCommand(new CommandEvent("wait"))
        mainWindow.fireCommand(new CommandEvent("wait"))
        mainWindow.fireCommand(new CommandEvent("wait"))

        assertMessagesAreCorrect([
                "time passes...", "",
                "time passes...", "",
                "time passes...", "",
                "time passes...", ""
        ])

        mainWindow.fireCommand(new CommandEvent("wait"))

        assertMessagesAreCorrect([
                "time passes...", "",
                "time passes...", "",
                "time passes...", "",
                "time passes...", "",
                "time passes...", "hello from closure",
                ""
        ])
    }

    @Test
    void testSetVisible() {
        dummy.setOnOpenScript("setVisible('tv')")

        tv.setVisible(false)
        assertFalse(tv.isVisible())

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertTrue(tv.isVisible())
    }

    @Test
    void testSetInvisible() {
        dummy.setOnOpenScript("setInvisible('tv')")

        tv.setVisible(true)
        assertTrue(tv.isVisible())

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertFalse(tv.isVisible())
    }

    @Test
    void testPlayerInRoom() {
        dummy.setOnOpenScript(
                """
              |if (playerInRoom('livingRoom')) {
              |    say('player is in the living room')
              |}
              |else {
              |    say('player is not in the living room')
              |}
            """.stripMargin())
        dummy2.setOnOpenScript(dummy.getOnOpenScript())

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "player is in the living room",
                ""
        ])

        mainWindow.fireCommand(new CommandEvent("west"))
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy2"))

        assertMessagesAreCorrect([
                "You open the dummy2",
                "player is not in the living room",
                ""
        ])
    }

    @Test
    void testPlayerNotInRoom() {
        dummy.setOnOpenScript(
                """
              |if (playerNotInRoom('livingRoom')) {
              |    say('player is not in the living room')
              |}
              |else {
              |    say('player is in the living room')
              |}
            """.stripMargin())
        dummy2.setOnOpenScript(dummy.getOnOpenScript())

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "player is in the living room",
                ""
        ])

        mainWindow.fireCommand(new CommandEvent("west"))
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy2"))

        assertMessagesAreCorrect([
                "You open the dummy2",
                "player is not in the living room",
                ""
        ])
    }

    @Test
    void testMove() {
        dummy.setOnOpenScript("moveTo('study')")

        assertEquals(livingRoom, this.controller.getCurrentRoom())

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertEquals(study, this.controller.getCurrentRoom())

        assertMessagesAreCorrect([
                "You open the dummy",
                "This is your study.",
                "You can also see:",
                "dummy2",
                ""
        ])
    }

}
