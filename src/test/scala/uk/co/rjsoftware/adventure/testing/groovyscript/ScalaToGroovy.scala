package uk.co.rjsoftware.adventure.testing.groovyscript

import org.scalatest.FunSuite
import org.scalatest.Matchers._
import uk.co.rjsoftware.adventure.controller.customscripts.ScriptExecutor
import uk.co.rjsoftware.adventure.controller.{AdventureController, MainWindowForTesting}
import uk.co.rjsoftware.adventure.model.{Adventure, Room}

/**
  * Created by richardsimpson on 29/05/2017.
  */
class ScalaToGroovy extends FunSuite {

    private var controller : AdventureController = _
    private var mainWindow : MainWindowForTesting = _

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
        val room:Room = new Room("roomName1", "room description")

        adventure.addRoom(room)
        adventure.setStartRoom(room)

        mainWindow = new MainWindowForTesting()
        this.controller = new AdventureController(mainWindow)
        this.controller.loadAdventure(adventure)
    }

    test("call the GroovyShell: say") {
        val executor:ScriptExecutor = new ScriptExecutor(controller)

        executor.executeScript("say('hello')")
    }

    // TODO: Put this test back when implement the WAIT command
    ignore("call the GroovyShell: executeAfterTurns") {
        mainWindow.clearMessages()

        val executor:ScriptExecutor = new ScriptExecutor(controller)

        executor.executeScript(
            "executeAfterTurns(5) {" +
                "say('hello from closure')" +
            "}")

        assertMessagesAreCorrect(List(
            "hello from closure"
        ))
    }

    private def assertMessagesAreCorrect(expectedMessages : List[String]): Unit = {
        val messages:Array[String] = this.mainWindow.getMessages
        assert(messages.length == expectedMessages.size)

        for (index <- expectedMessages.indices) {
            messages(index) should equal (expectedMessages(index))
        }
    }

}
