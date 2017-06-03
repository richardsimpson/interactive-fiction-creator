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

    private val chocolates:Item = new Item(List("a box of expensive chocolates", "chocolates"),
        "The chocolates were small and brown.", visible = true)

    private val explodedChocolates:Item = new Item(List("a wrecked box of chocolates", "chocolates"),
        "The exploded chocolates did not look very appetising.", visible = false)

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

        livingRoom.addItem(chocolates)
        livingRoom.addItem(explodedChocolates)

        chocolates.setVisible(true)
        explodedChocolates.setVisible(false)

        adventure.addRoom(livingRoom)
        adventure.setStartRoom(livingRoom)

        this.mainWindow = new MainWindowForTesting()
        this.classUnderTest = new AdventureController(mainWindow)
        this.classUnderTest.loadAdventure(adventure)
        this.player = this.classUnderTest.getPlayer
    }

    private def assertMessagesAreCorrect(expectedMessages : List[String]): Unit = {
        val messages:List[String] = this.mainWindow.getMessages
        assert(messages.size == expectedMessages.size)

        for (index <- expectedMessages.indices) {
            messages(index) should equal (expectedMessages(index))
        }
    }

    test("verb: EXAM chocolates (when non-exploded chocolates are visible)") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chocolates")))

            assertMessagesAreCorrect(List(
                chocolates.getDescription,
                ""
            ))
        }
    }

    test("verb: EXAM chocolates (when exploded chocolates are visible)") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            chocolates.setVisible(false)
            explodedChocolates.setVisible(true)

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chocolates")))

            assertMessagesAreCorrect(List(
                explodedChocolates.getDescription,
                ""
            ))
        }
    }

    test("verb: GET chocolates (when non-exploded chocolates are visible)") {
        for (verbString <- this.verbs("GET {noun}").getSynonyms) {
            setup()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chocolates")))
            assert(!livingRoom.contains(chocolates))
            assert(player.contains(chocolates))
        }
    }

    test("verb: GET chocolates (when exploded chocolates are visible)") {
        for (verbString <- this.verbs("GET {noun}").getSynonyms) {
            setup()
            chocolates.setVisible(false)
            explodedChocolates.setVisible(true)

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun\\}", "chocolates")))
            assert(!livingRoom.contains(explodedChocolates))
            assert(player.contains(explodedChocolates))
        }
    }

}
