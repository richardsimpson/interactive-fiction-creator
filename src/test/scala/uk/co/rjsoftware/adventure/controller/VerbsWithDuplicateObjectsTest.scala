package uk.co.rjsoftware.adventure.controller

import org.scalatest.FunSuite
import org.scalatest.Matchers._
import uk.co.rjsoftware.adventure.model._
import uk.co.rjsoftware.adventure.view.CommandEvent

/**
  * Created by richardsimpson on 20/05/2017.
  * See http://www.scalatest.org/at_a_glance/FunSuite
  */
class VerbsWithDuplicateObjectsTest extends FunSuite {

    private var classUnderTest : AdventureController = _
    private var player : Player = _
    private var mainWindow : MainWindowForTesting = _

    private val livingRoom:Room = new Room("livingRoom", "This is the living room.")

    private val redBox:Item = new Item("redbox", List("red box", "box"),
        "This is the red box.", visible = true)

    private val blueBox:Item = new Item("bluebox", List("blue box", "box"),
        "This is the blue box.", visible = false)

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

        livingRoom.addItem(redBox)
        livingRoom.addItem(blueBox)

        redBox.setVisible(true)
        blueBox.setVisible(false)

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

    test("verb: EXAM box (when red box is visible)") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "box")))

            assertMessagesAreCorrect(List(
                redBox.getDescription,
                ""
            ))
        }
    }

    test("verb: EXAM box (when blue box is visible)") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            redBox.setVisible(false)
            blueBox.setVisible(true)

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "box")))

            assertMessagesAreCorrect(List(
                blueBox.getDescription,
                ""
            ))
        }
    }

    test("verb: EXAM box (when both boxes are visible and select the red box)") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            redBox.setVisible(true)
            blueBox.setVisible(true)

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "box")))

            assertMessagesAreCorrect(List(
                "Examine what?",
                "1) red box",
                "2) blue box",
                ""
            ))

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent("1"))

            assertMessagesAreCorrect(List(
                "This is the red box.",
                ""
            ))
        }
    }

    test("verb: EXAM box (when both boxes are visible and select the blue box)") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            redBox.setVisible(true)
            blueBox.setVisible(true)

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "box")))

            assertMessagesAreCorrect(List(
                "Examine what?",
                "1) red box",
                "2) blue box",
                ""
            ))

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent("2"))

            assertMessagesAreCorrect(List(
                "This is the blue box.",
                ""
            ))
        }
    }

    test("verb: EXAM box (when both boxes are visible and select an option that doesn't exist)") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            redBox.setVisible(true)
            blueBox.setVisible(true)

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "box")))

            assertMessagesAreCorrect(List(
                "Examine what?",
                "1) red box",
                "2) blue box",
                ""
            ))

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent("3"))

            assertMessagesAreCorrect(List(
                "I'm sorry, I don't understand",
                ""
            ))
        }
    }

    test("verb: EXAM box (when both boxes are visible and select an invalid option)") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            redBox.setVisible(true)
            blueBox.setVisible(true)

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "box")))

            assertMessagesAreCorrect(List(
                "Examine what?",
                "1) red box",
                "2) blue box",
                ""
            ))

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent("A"))

            assertMessagesAreCorrect(List(
                "I'm sorry, I don't understand",
                ""
            ))
        }
    }

    test("verb: GET box (when red box is visible)") {
        for (verbString <- this.verbs("GET {noun}").getSynonyms) {
            setup()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "box")))
            assert(!livingRoom.contains(redBox))
            assert(player.contains(redBox))
        }
    }

    test("verb: GET box (when blue box is visible)") {
        for (verbString <- this.verbs("GET {noun}").getSynonyms) {
            setup()
            redBox.setVisible(false)
            blueBox.setVisible(true)

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "box")))
            assert(!livingRoom.contains(blueBox))
            assert(player.contains(blueBox))
        }
    }

}
