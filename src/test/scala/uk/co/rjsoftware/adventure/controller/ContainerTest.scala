package uk.co.rjsoftware.adventure.controller

import org.scalatest.FunSuite
import org.scalatest.Matchers._
import uk.co.rjsoftware.adventure.model._
import uk.co.rjsoftware.adventure.view.CommandEvent

/**
  * Created by richardsimpson on 20/05/2017.
  * See http://www.scalatest.org/at_a_glance/FunSuite
  */
class ContainerTest extends FunSuite {

    private var classUnderTest : AdventureController = _
    private var player : Player = _
    private var mainWindow : MainWindowForTesting = _

    private val livingRoom:Room = new Room("livingRoom", "This is the living room.")

    private val chest:Item = new Item("chest", List("chest"), "This is the chest.",
        container = true, openMessage = "The chest is now open", closeMessage = "The chest is now closed",
        onOpenScript = "say('onOpenScript')", onCloseScript = "say('onCloseScript')"
    )

    private val goldCoin :Item = new Item("coin", List("gold coin", "coin"), "This coin is gold.")
    private val notepad :Item = new Item("notepad", List("notepad"), "A notebook with strange writing on it.")

    chest.addItem(goldCoin)
    chest.addItem(notepad)

    val verbs:Map[String, Verb] = StandardVerbs.getVerbs.map(
        verb => (verb.getVerb, verb)
    ).toMap

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
        val adventure:Adventure = new Adventure("Welcome to the Adventure!")

        livingRoom.addItem(chest)

        chest.setOpen(false)
        chest.setContentVisibility(ContentVisibility.AFTER_EXAMINE)
        chest.setItemPreviouslyExamined(false)
        chest.setOpenable(true)
        chest.setCloseable(true)

        adventure.addRoom(livingRoom)
        adventure.setStartRoom(livingRoom)

        this.mainWindow = new MainWindowForTesting()
        this.classUnderTest = new AdventureController(mainWindow)
        this.classUnderTest.loadAdventure(adventure)
        this.player = this.classUnderTest.getPlayer
    }

    private def assertMessagesAreCorrect(expectedMessages : List[String]): Unit = {
        val messages:Array[String] = this.mainWindow.getMessages
        assert(messages.length == expectedMessages.size)

        for (index <- expectedMessages.indices) {
            messages(index) should equal (expectedMessages(index))
        }
    }

    test("OPEN chest") {
        for (verbString <- this.verbs("OPEN {noun}").getSynonyms) {
            setup()

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chest")))

            assertMessagesAreCorrect(List(
                "The chest is now open",
                "onOpenScript",
                ""
            ))

            assert(this.chest.isOpen)
        }
    }

    test("CLOSE chest") {
        for (verbString <- this.verbs("CLOSE {noun}").getSynonyms) {
            setup()

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chest")))

            assertMessagesAreCorrect(List(
                "The chest is now closed",
                "onCloseScript",
                ""
            ))

            assert(!this.chest.isOpen)
        }
    }

    test("OPEN chest when it is already open") {
        for (verbString <- this.verbs("OPEN {noun}").getSynonyms) {
            setup()

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chest")))

            assertMessagesAreCorrect(List(
                "chest is already open",
                ""
            ))
        }
    }

    test("CLOSE chest when it is already closed") {
        for (verbString <- this.verbs("CLOSE {noun}").getSynonyms) {
            setup()

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chest")))

            assertMessagesAreCorrect(List(
                "chest is already closed",
                ""
            ))
        }
    }

    test("OPEN chest when chest cannot be opened") {
        for (verbString <- this.verbs("OPEN {noun}").getSynonyms) {
            setup()
            this.chest.setOpenable(false)

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chest")))

            assertMessagesAreCorrect(List(
                "You cannot open the chest",
                ""
            ))

            assert(!this.chest.isOpen)
        }
    }

    test("CLOSE chest when chest cannot be closed") {
        for (verbString <- this.verbs("CLOSE {noun}").getSynonyms) {
            setup()
            this.chest.setCloseable(false)

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chest")))

            assertMessagesAreCorrect(List(
                "You cannot close the chest",
                ""
            ))

            assert(this.chest.isOpen)
        }
    }

    test("LOOK when container closed (contents initially hidden)") {
        for (verbString <- this.verbs("LOOK").getSynonyms) {
            setup()

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect(List(
                "This is the living room.",
                "You can also see:",
                chest.getName,
                ""
            ))
        }
    }

    test("LOOK when container open (contents initially hidden)") {
        for (verbString <- this.verbs("LOOK").getSynonyms) {
            setup()

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect(List(
                "This is the living room.",
                "You can also see:",
                chest.getName,
                ""
            ))
        }
    }

    test("LOOK when container closed (contents always visible)") {
        for (verbString <- this.verbs("LOOK").getSynonyms) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.ALWAYS)

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect(List(
                "This is the living room.",
                "You can also see:",
                chest.getName,
                ""
            ))
        }
    }

    test("LOOK when container open (contents always visible)") {
        for (verbString <- this.verbs("LOOK").getSynonyms) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.ALWAYS)

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect(List(
                "This is the living room.",
                "You can also see:",
                chest.getName + ", containing:",
                "    " + goldCoin.getName,
                "    " + notepad.getName,
                ""
            ))
        }
    }

    test("LOOK when container closed (contents never visible)") {
        for (verbString <- this.verbs("LOOK").getSynonyms) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.NEVER)

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect(List(
                "This is the living room.",
                "You can also see:",
                chest.getName,
                ""
            ))
        }
    }

    test("LOOK when container open (contents never visible)") {
        for (verbString <- this.verbs("LOOK").getSynonyms) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.NEVER)

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect(List(
                "This is the living room.",
                "You can also see:",
                chest.getName,
                ""
            ))
        }
    }

    test("EXAM when container closed (contents initially hidden)") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chest")))

            assertMessagesAreCorrect(List(
                "This is the chest.",
                ""
            ))
        }
    }

    test("EXAM when container open (contents initially hidden)") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chest")))

            assertMessagesAreCorrect(List(
                "This is the chest.  It contains:",
                goldCoin.getName,
                notepad.getName,
                ""
            ))
        }
    }

    test("EXAM when container closed (contents always visible)") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.ALWAYS)

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chest")))

            assertMessagesAreCorrect(List(
                "This is the chest.",
                ""
            ))
        }
    }

    test("EXAM when container open (contents always visible)") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.ALWAYS)

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chest")))

            assertMessagesAreCorrect(List(
                "This is the chest.  It contains:",
                goldCoin.getName,
                notepad.getName,
                ""
            ))
        }
    }

    test("EXAM when container closed (contents never visible)") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.NEVER)

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chest")))

            assertMessagesAreCorrect(List(
                "This is the chest.",
                ""
            ))
        }
    }

    test("EXAM when container open (contents never visible)") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.NEVER)

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chest")))

            assertMessagesAreCorrect(List(
                "This is the chest.",
                ""
            ))
        }
    }

    test("EXAM when container closed, which was previously examined when open (contents initially hidden)") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()

            mainWindow.fireCommand(new CommandEvent("open chest"))
            mainWindow.fireCommand(new CommandEvent("examine chest"))
            mainWindow.fireCommand(new CommandEvent("close chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chest")))

            assertMessagesAreCorrect(List(
                "This is the chest.",
                ""
            ))
        }
    }

    test("LOOK after examining an open container (contents initially hidden)") {
        for (verbString <- this.verbs("LOOK").getSynonyms) {
            setup()

            mainWindow.fireCommand(new CommandEvent("open chest"))
            mainWindow.fireCommand(new CommandEvent("examine chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect(List(
                "This is the living room.",
                "You can also see:",
                chest.getName + ", containing:",
                "    " + goldCoin.getName,
                "    " + notepad.getName,
                ""
            ))
        }
    }

    test("LOOK after examining a closed container (contents initially hidden)") {
        for (verbString <- this.verbs("LOOK").getSynonyms) {
            setup()

            mainWindow.fireCommand(new CommandEvent("examine chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect(List(
                "This is the living room.",
                "You can also see:",
                chest.getName,
                ""
            ))
        }
    }

    test("LOOK when the contained was closed after being examined (contents initially hidden)") {
        for (verbString <- this.verbs("LOOK").getSynonyms) {
            setup()

            mainWindow.fireCommand(new CommandEvent("open chest"))
            mainWindow.fireCommand(new CommandEvent("examine chest"))
            mainWindow.fireCommand(new CommandEvent("close chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect(List(
                "This is the living room.",
                "You can also see:",
                chest.getName,
                ""
            ))
        }
    }

}
