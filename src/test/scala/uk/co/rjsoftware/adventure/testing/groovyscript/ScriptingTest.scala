package uk.co.rjsoftware.adventure.testing.groovyscript

import org.scalatest.FunSuite
import org.scalatest.Matchers._
import uk.co.rjsoftware.adventure.controller.customscripts.ScriptExecutor
import uk.co.rjsoftware.adventure.controller.{AdventureController, MainWindowForTesting}
import uk.co.rjsoftware.adventure.model.{Adventure, Direction, Item, Room}
import uk.co.rjsoftware.adventure.view.CommandEvent

/**
  * Created by richardsimpson on 29/05/2017.
  */
class ScriptingTest extends FunSuite {

    private var controller : AdventureController = _
    private var mainWindow : MainWindowForTesting = _

    private val study:Room = new Room("study", "This is your study.")
    private val livingRoom:Room = new Room("livingRoom", "This is the living room.")

    private val tv:Item = new Item(List("TV", "television"), "A 28\" TV", visible = true, switchable = true)
    private val chest:Item = new Item(List("chest"), "This is the chest.", container = true)
    private val dummy:Item = new Item(List("dummy"), "This is the dummy item.", container = true)
    private val dummy2:Item = new Item(List("dummy2"), "This is the second dummy item.", container = true)

    //
    // study -------- livingRoom
    //

    study.addExit(Direction.EAST, livingRoom)
    livingRoom.addExit(Direction.WEST, study)

    override def withFixture(test: NoArgTest) = {
        // Shared setup (run at beginning of each test)
        setup()
        try
            test()
        finally {
            // Shared cleanup (run at end of each test)
        }
    }

    private def setup(): Unit = {
        val adventure:Adventure = new Adventure("intro")

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
    }

    private def assertMessagesAreCorrect(expectedMessages : List[String]): Unit = {
        val messages:Array[String] = this.mainWindow.getMessages
        assert(messages.length == expectedMessages.size)

        for (index <- expectedMessages.indices) {
            messages(index) should equal (expectedMessages(index))
        }
    }

    test("function: say") {
        dummy.setOnOpenScript("say('hello')")
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect(List(
            "You open the dummy",
            "hello",
            ""
        ))
    }

    test("function: isSwitchedOn") {
        dummy.setOnOpenScript(
            """
              |if (isSwitchedOn('tv')) {
              |    say('tv is switched on')
              |}
              |else {
              |    say('tv is switched off')
              |}
            """.stripMargin)

        tv.switchOn()
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect(List(
            "You open the dummy",
            "tv is switched on",
            ""
        ))

        tv.switchOff()
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect(List(
            "You open the dummy",
            "tv is switched off",
            ""
        ))
    }

    test("function: isSwitchedOff") {
        dummy.setOnOpenScript(
            """
              |if (isSwitchedOff('tv')) {
              |    say('tv is switched off')
              |}
              |else {
              |    say('tv is switched on')
              |}
            """.stripMargin)

        tv.switchOn()
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect(List(
            "You open the dummy",
            "tv is switched on",
            ""
        ))

        tv.switchOff()
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect(List(
            "You open the dummy",
            "tv is switched off",
            ""
        ))
    }

    test("function: isOpen") {
        dummy.setOnOpenScript(
            """
              |if (isOpen('chest')) {
              |    say('chest is open')
              |}
              |else {
              |    say('chest is closed')
              |}
            """.stripMargin)

        chest.setOpen(true)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect(List(
            "You open the dummy",
            "chest is open",
            ""
        ))

        chest.setOpen(false)
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect(List(
            "You open the dummy",
            "chest is closed",
            ""
        ))
    }

    test("function: isClosed") {
        dummy.setOnOpenScript(
            """
              |if (isClosed('chest')) {
              |    say('chest is closed')
              |}
              |else {
              |    say('chest is open')
              |}
            """.stripMargin)

        chest.setOpen(true)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect(List(
            "You open the dummy",
            "chest is open",
            ""
        ))

        chest.setOpen(false)
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect(List(
            "You open the dummy",
            "chest is closed",
            ""
        ))
    }

    test("function: executeAfterTurns") {
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

        assertMessagesAreCorrect(List(
            "time passes...", "",
            "time passes...", "",
            "time passes...", "",
            "time passes...", ""
        ))

        mainWindow.fireCommand(new CommandEvent("wait"))

        assertMessagesAreCorrect(List(
            "time passes...", "",
            "time passes...", "",
            "time passes...", "",
            "time passes...", "",
            "time passes...", "",
            "hello from closure"
        ))
    }

    test("function: setVisible") {
        dummy.setOnOpenScript("setVisible('tv')")

        tv.setVisible(false)
        assert(!tv.isVisible)

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assert(tv.isVisible)
    }

    test("function: setInvisible") {
        dummy.setOnOpenScript("setInvisible('tv')")

        tv.setVisible(true)
        assert(tv.isVisible)

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assert(!tv.isVisible)
    }

    test("function: playerInRoom") {
        dummy.setOnOpenScript(
            """
              |if (playerInRoom('livingRoom')) {
              |    say('player is in the living room')
              |}
              |else {
              |    say('player is not in the living room')
              |}
            """.stripMargin)

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect(List(
            "You open the dummy",
            "player is in the living room",
            ""
        ))

        mainWindow.fireCommand(new CommandEvent("west"))
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect(List(
            "You open the dummy",
            "player is not in the living room",
            ""
        ))
    }

    test("function: playerNotInRoom") {
        dummy.setOnOpenScript(
            """
              |if (playerNotInRoom('livingRoom')) {
              |    say('player is not in the living room')
              |}
              |else {
              |    say('player is in the living room')
              |}
            """.stripMargin)

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect(List(
            "You open the dummy",
            "player is in the living room",
            ""
        ))

        mainWindow.fireCommand(new CommandEvent("west"))
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect(List(
            "You open the dummy",
            "player is not in the living room",
            ""
        ))
    }

    test("function: move") {
        dummy.setOnOpenScript("move('study')")

        assert(this.controller.getCurrentRoom == livingRoom)

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assert(this.controller.getCurrentRoom == study)

        assertMessagesAreCorrect(List(
            "You open the dummy",
            "This is your study.",
            "You can also see",
            "dummy2",
            ""
        ))

    }

}
