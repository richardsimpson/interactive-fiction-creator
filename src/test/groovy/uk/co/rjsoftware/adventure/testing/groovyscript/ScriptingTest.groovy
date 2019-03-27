package uk.co.rjsoftware.adventure.testing.groovyscript

import groovy.transform.TypeChecked
import org.junit.Before
import org.junit.Test
import uk.co.rjsoftware.adventure.controller.AdventureController
import uk.co.rjsoftware.adventure.controller.IPlayerAppViewForTesting
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.Direction
import uk.co.rjsoftware.adventure.model.Exit
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.CommandEvent

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertEquals

@TypeChecked
class ScriptingTest {

    private AdventureController controller
    private IPlayerAppViewForTesting mainWindow

    private Room study = new Room("study", "This is your study.")
    private Room livingRoom = new Room("livingRoom", "This is the living room.")

    private Item tv = new Item("tv", "TV", ["television"], "A 28\" TV")
    private Item chest = new Item("chest", "chest", "This is the chest.")
    private Item dummy = new Item("dummy", "dummy", "This is the dummy item.")
    private Item dummy2 = new Item("dummy2", "dummy2", "This is the second dummy item.")
    private Item boobyTrappedBox = new Item("box", "booby trapped box", ["box"], "This box will explode when opened.")
    private Item player = new Item("player")

    @Before
    void before() {
        tv.setVisible(true)
        tv.setSwitchable(true)

        chest.setContainer(true)
        dummy.setContainer(true)
        dummy.setOpenable(true)
        dummy.setCloseable(true)
        dummy2.setContainer(true)
        dummy2.setOpenable(true)
        dummy2.setCloseable(true)
        boobyTrappedBox.setOpenable(true)
        boobyTrappedBox.setOnOpenScript("""
            say("the box explodes. game over.")
            endGame()
        """)

        //
        // study -------- livingRoom
        //

        study.addExit(new Exit(Direction.EAST, livingRoom))
        livingRoom.addExit(new Exit(Direction.WEST, study))

        final Adventure adventure = new Adventure("intro")

        livingRoom.addItem(tv)
        livingRoom.addItem(chest)
        livingRoom.addItem(dummy)
        livingRoom.addItem(player)
        livingRoom.addItem(boobyTrappedBox)

        study.addItem(dummy2)

        adventure.addRoom(study)
        adventure.addRoom(livingRoom)

        dummy.setOpen(false)
        dummy2.setOpen(false)

        this.mainWindow = new IPlayerAppViewForTesting()
        this.controller = new AdventureController(mainWindow)
        this.controller.loadAdventure(adventure)

        // get room and item references back from the AdventureController, as that now copies the adventure
        // during the loading process
        this.study = this.controller.getRoom(this.study.name)
        this.livingRoom = this.controller.getRoom(this.livingRoom.name)
        this.tv = this.controller.getItemByName(this.tv.getName())
        this.chest = this.controller.getItemByName(this.chest.getName())
        this.dummy = this.controller.getItemByName(this.dummy.getName())
        this.dummy2 = this.controller.getItemByName(this.dummy2.getName())
        this.boobyTrappedBox = this.controller.getItemByName(this.boobyTrappedBox.getName())
    }

    private void assertMessagesAreCorrect(List<String> expectedMessages) {
        final String[] messages = this.mainWindow.getMessages()
        assertEquals(expectedMessages.join(System.lineSeparator()), messages.join(System.lineSeparator()))
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
    void testSayRemovesAnyMarginFromTheText() {
        dummy.setOnOpenScript('''say("""
            Hello
            This is a test""")
        ''')
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "Hello",
                "This is a test",
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
    void testMovePlayerTo() {
        dummy.setOnOpenScript("movePlayerTo('study')")

        assertEquals(livingRoom, this.controller.getCurrentRoom())

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertEquals(study, this.controller.getCurrentRoom())

        assertMessagesAreCorrect([
                "You open the dummy",
                "This is your study.",
                "From here you can go East",
                "You can also see:",
                "dummy2",
                ""
        ])
    }

    @Test
    void testMoveItemTo_WhenTargetIsARoom() {
        dummy.setOnOpenScript("moveItemTo('tv', 'study')")

        assertEquals(livingRoom, this.controller.getCurrentRoom())

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertFalse(this.livingRoom.contains(tv))
        assertTrue(this.study.contains(tv))
        assertEquals(this.study, this.tv.getParent())
    }

    @Test
    void testMoveItemTo_WhenTargetIsAnItem() {
        dummy.setOnOpenScript("moveItemTo('tv', 'dummy2')")

        assertEquals(livingRoom, this.controller.getCurrentRoom())

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertFalse(this.livingRoom.contains(tv))
        assertTrue(this.dummy2.contains(tv))
        assertEquals(this.dummy2, this.tv.getParent())
    }

    @Test
    void testGetItem() {
        dummy.setOnOpenScript("say(getItem('tv').getDisplayName())")

        assertEquals(livingRoom, this.controller.getCurrentRoom())

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "TV",
                ""
        ])
    }

    @Test
    void testGetCurrentRoom() {
        dummy.setOnOpenScript("say(getCurrentRoom().getName())")
        dummy2.setOnOpenScript("say(getCurrentRoom().getName())")

        assertEquals(livingRoom, this.controller.getCurrentRoom())

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))
        assertMessagesAreCorrect([
                "You open the dummy",
                "livingRoom",
                ""
        ])

        mainWindow.fireCommand(new CommandEvent("west"))
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy2"))
        assertMessagesAreCorrect([
                "You open the dummy2",
                "study",
                ""
        ])

    }

    @Test
    void testGetPlayer() {
        dummy.setOnOpenScript("say(getPlayer().getName())")

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))
        assertMessagesAreCorrect([
                "You open the dummy",
                "player",
                ""
        ])
    }

    @Test
    void testIncreaseScore() {
        dummy.setOnOpenScript("increaseScore(1)")
        dummy2.setOnOpenScript("say('Score: ' + getScore())")

        mainWindow.fireCommand(new CommandEvent("open dummy"))
        mainWindow.fireCommand(new CommandEvent("west"))

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy2"))
        assertMessagesAreCorrect([
                "You open the dummy2",
                "Score: 1",
                ""
        ])
    }

    @Test
    void testDecreaseScore() {
        dummy.setOnOpenScript("decreaseScore(2)")
        dummy2.setOnOpenScript("say('Score: ' + getScore())")

        mainWindow.fireCommand(new CommandEvent("open dummy"))
        mainWindow.fireCommand(new CommandEvent("west"))

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy2"))
        assertMessagesAreCorrect([
                "You open the dummy2",
                "Score: -2",
                ""
        ])
    }

    @Test
    void testGetTurnCounter() {
        dummy.setOnOpenScript("say('TurnCounter: ' + getTurnCounter())")

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))
        assertMessagesAreCorrect([
                "You open the dummy",
                "TurnCounter: 1",
                ""
        ])

        mainWindow.fireCommand(new CommandEvent("west"))
        mainWindow.fireCommand(new CommandEvent("east"))
        mainWindow.fireCommand(new CommandEvent("close dummy"))

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))
        assertMessagesAreCorrect([
                "You open the dummy",
                "TurnCounter: 5",
                ""
        ])

    }

    @Test
    void testEndGame() {
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open box"))

        assertMessagesAreCorrect([
                "You open the booby trapped box",
                "the box explodes. game over.",
                "",
                "Press <Enter> to start a new game.",
                ""
        ])

        this.mainWindow.clearMessages()
        mainWindow.fireCommand(new CommandEvent(""))

        assertEquals(0, this.controller.getTurnCounter())
        assertFalse(this.controller.getItemByName(this.boobyTrappedBox.getName()).isOpen())
    }


}
