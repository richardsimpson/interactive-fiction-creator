package uk.co.rjsoftware.adventure.controller

import org.scalatest.FunSuite
import org.scalatest.Matchers._
import uk.co.rjsoftware.adventure.model._
import uk.co.rjsoftware.adventure.view.{CommandEvent, MainWindow, MainWindowView}

/**
  * Created by richardsimpson on 20/05/2017.
  * See http://www.scalatest.org/at_a_glance/FunSuite
  */
class AdventureControllerTest extends FunSuite {

    private var classUnderTest : AdventureController = _
    private var player : Player = _
    private var mainWindow : MainWindowForTesting = _

    private val bedroom:Room = new Room("bedroom", "This is your bedroom.")
    private val landing:Room = new Room("landing", "This is the landing.")
    private val bedroom2:Room = new Room("bedroom2", "This is the spare bedroom.")
    private val bathroom:Room = new Room("bathroom", "This is the en-suite bathroom.")
    private val veranda:Room = new Room("veranda", "This is the veranda.")

    private val lamp:Item = new Item("lamp", "A bedside lamp. with a simple on/off switch",
        switchable = true)
    private val tv:Item = new Item("TV", "A 28\" TV.",
        switchable = true, "the TV flickers into life", "the TV is now off")
    private val newspaper:Item = new Item("newspaper", "The Daily Bugle", switchable = false)

    override def withFixture(test: NoArgTest) = {
        // Shared setup (run at beginning of each test)
        val adventure:Adventure = new Adventure("Welcome to the Adventure!")

        bedroom.addItem(lamp)
        bedroom.addItem(tv)
        bedroom.addItem(newspaper)

        // TODO: Currently, CustomVerb's require a Noun.  Allow custom verbs without nouns.
        tv.addVerb(new CustomVerb(List("WATCH {noun}")),
            "say('You watch the TV for a while.');"
        )


        //          bathroom
        //             |
        //bedroom - landing - veranda
        //             |
        //          bedroom2
        bedroom.addExit(Direction.EAST, landing)
        landing.addExit(Direction.NORTH, bathroom)
        landing.addExit(Direction.SOUTH, bedroom2)
        landing.addExit(Direction.EAST, veranda)
        landing.addExit(Direction.WEST, bedroom)
        bathroom.addExit(Direction.SOUTH, landing)
        veranda.addExit(Direction.WEST, landing)
        bedroom2.addExit(Direction.NORTH, landing)

        adventure.addRoom(bedroom)
        adventure.addRoom(landing)
        adventure.addRoom(bedroom2)
        adventure.addRoom(bathroom)
        adventure.addRoom(veranda)

        adventure.setStartRoom(landing)

        mainWindow = new MainWindowForTesting()
        this.classUnderTest = new AdventureController(adventure, mainWindow)
        this.player = this.classUnderTest.getPlayer

        try
            test()
        finally {
            // Shared cleanup (run at end of each test)
        }
    }

    test("verb: NORTH") {
        mainWindow.fireCommand(new CommandEvent("north"))
        assert(this.classUnderTest.getCurrentRoom == bathroom)
    }

    test("verb: SOUTH") {
        mainWindow.fireCommand(new CommandEvent("south"))
        assert(this.classUnderTest.getCurrentRoom == bedroom2)
    }

    test("verb: EAST") {
        mainWindow.fireCommand(new CommandEvent("east"))
        assert(this.classUnderTest.getCurrentRoom == veranda)
    }

    test("verb: WEST") {
        mainWindow.fireCommand(new CommandEvent("west"))
        assert(this.classUnderTest.getCurrentRoom == bedroom)
    }

    test("verb: N") {
        mainWindow.fireCommand(new CommandEvent("n"))
        assert(this.classUnderTest.getCurrentRoom == bathroom)
    }

    test("verb: S") {
        mainWindow.fireCommand(new CommandEvent("s"))
        assert(this.classUnderTest.getCurrentRoom == bedroom2)
    }

    test("verb: E") {
        mainWindow.fireCommand(new CommandEvent("e"))
        assert(this.classUnderTest.getCurrentRoom == veranda)
    }

    test("verb: W") {
        mainWindow.fireCommand(new CommandEvent("w"))
        assert(this.classUnderTest.getCurrentRoom == bedroom)
    }

    test("verb: LOOK") {
        mainWindow.fireCommand(new CommandEvent("look"))
        this.mainWindow.getLastMessage should equal (landing.getDescription)
    }

    test("verb: EXITS") {
        mainWindow.fireCommand(new CommandEvent("exits"))
        this.mainWindow.getLastMessage should equal ("From here you can go North, South, East, West, ")

    }

    test("verb: EXAMINE {noun}") {
        mainWindow.fireCommand(new CommandEvent("west"))
        mainWindow.fireCommand(new CommandEvent("examine lamp"))
        this.mainWindow.getLastMessage should equal (lamp.getDescription)
    }

    test("verb: EXAM {noun}") {
        mainWindow.fireCommand(new CommandEvent("west"))
        mainWindow.fireCommand(new CommandEvent("exam lamp"))
        this.mainWindow.getLastMessage should equal (lamp.getDescription)
    }

    test("verb: X {noun}") {
        mainWindow.fireCommand(new CommandEvent("west"))
        mainWindow.fireCommand(new CommandEvent("x lamp"))
        this.mainWindow.getLastMessage should equal (lamp.getDescription)
    }

    test("verb: GET {noun}") {
        mainWindow.fireCommand(new CommandEvent("west"))
        mainWindow.fireCommand(new CommandEvent("get lamp"))
        assert(!bedroom.contains(lamp))
        assert(player.contains(lamp))
    }

    test("verb: TAKE {noun}") {
        mainWindow.fireCommand(new CommandEvent("west"))
        mainWindow.fireCommand(new CommandEvent("take lamp"))
        assert(!bedroom.contains(lamp))
        assert(player.contains(lamp))
    }

    test("verb: DROP {noun}") {
        mainWindow.fireCommand(new CommandEvent("west"))
        mainWindow.fireCommand(new CommandEvent("get lamp"))
        mainWindow.fireCommand(new CommandEvent("east"))
        mainWindow.fireCommand(new CommandEvent("drop lamp"))

        assert(!bedroom.contains(lamp))
        assert(landing.contains(lamp))
        assert(!player.contains(lamp))
    }

    test("verb: INVENTORY (when empty)") {
        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("inventory"))

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 2)
        messages(1) should equal ("You are currently carrying:")
        messages(0) should equal ("Nothing")
    }

    test("verb: INVENTORY (when not empty)") {
        mainWindow.fireCommand(new CommandEvent("west"))
        mainWindow.fireCommand(new CommandEvent("get lamp"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("inventory"))

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 2)
        messages(1) should equal ("You are currently carrying:")
        messages(0) should equal ("lamp")
    }

    test("verb: INVENTORY (when contains multiple items)") {
        mainWindow.fireCommand(new CommandEvent("west"))
        mainWindow.fireCommand(new CommandEvent("get lamp"))
        mainWindow.fireCommand(new CommandEvent("get tv"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("inventory"))

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 3)
        messages(2) should equal ("You are currently carrying:")
        messages(1) should equal ("TV")
        messages(0) should equal ("lamp")
    }

    test("verb: INV (when empty)") {
        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("inv"))

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 2)
        messages(1) should equal ("You are currently carrying:")
        messages(0) should equal ("Nothing")
    }

    test("verb: I (when empty)") {
        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("i"))

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 2)
        messages(1) should equal ("You are currently carrying:")
        messages(0) should equal ("Nothing")
    }

    test("verb: TURN ON {noun}") {
        mainWindow.fireCommand(new CommandEvent("west"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("turn on lamp"))

        assert(this.bedroom.getItem(lamp.getName).get.isOn)

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("You turn on the lamp")
    }

    test("verb: TURN ON {noun} (when the item has a custom message defined for switching on") {
        mainWindow.fireCommand(new CommandEvent("west"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("turn on tv"))

        assert(this.bedroom.getItem(lamp.getName).get.isOn)

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("the TV flickers into life")
    }

    test("verb: TURN ON {noun} (when the item is not switchable)") {
        mainWindow.fireCommand(new CommandEvent("west"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("turn on newspaper"))

        assert(this.bedroom.getItem(newspaper.getName).get.isOff)

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("You can't turn on the newspaper")
    }

    test("verb: TURN OFF {noun}") {
        mainWindow.fireCommand(new CommandEvent("west"))
        mainWindow.fireCommand(new CommandEvent("turn on lamp"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("turn off lamp"))

        assert(!this.bedroom.getItem(lamp.getName).get.isOn)

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("You turn off the lamp")
    }

    test("verb: TURN OFF {noun} (when the item has a custom message defined for switching off") {
        mainWindow.fireCommand(new CommandEvent("west"))
        mainWindow.fireCommand(new CommandEvent("turn on tv"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("turn off tv"))

        assert(!this.bedroom.getItem(lamp.getName).get.isOn)

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("the TV is now off")
    }

    test("verb: TURN OFF {noun} (when the item is not switchable)") {
        mainWindow.fireCommand(new CommandEvent("west"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("turn off newspaper"))

        assert(this.bedroom.getItem(newspaper.getName).get.isOff)

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("You can't turn off the newspaper")
    }

    test("custom verb: WATCH {noun}") {
        mainWindow.fireCommand(new CommandEvent("west"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("watch tv"))

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("You watch the TV for a while.")
    }

    test("additional words between verb and noun: GET THE {noun}") {
        mainWindow.fireCommand(new CommandEvent("west"))
        mainWindow.fireCommand(new CommandEvent("get the lamp"))
        assert(!bedroom.contains(lamp))
        assert(player.contains(lamp))
    }


    // TODO Add a test for the script functions isSwitchedOn and isSwitchedOff
    // TODO: Add test where two object have the same verb defined
}
