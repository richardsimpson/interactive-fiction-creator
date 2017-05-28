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

    private val study:Room = new Room("study", "This is your study.")
    private val livingRoom:Room = new Room("livingRoom", "This is the living room.")
    private val garden:Room = new Room("garden", "This is the garden.")
    private val kitchen:Room = new Room("kitchen", "This is the kitchen.")
    private val diningRoom:Room = new Room("diningRoom", "This is the dining room.")
    private val landing:Room = new Room("landing", "This is the landing.")
    private val cellar:Room = new Room("cellar", "This is the cellar.")

    private val lamp:Item = new Item(List("lamp"), "A bedside lamp. with a simple on/off switch.",
        switchable = true)
    private val tv:Item = new Item(List("TV", "television"), "A 28\" TV",
        visible = true, scenery = true, gettable = false, droppable = false,
        switchable = true, "the TV flickers into life", "the TV is now off",
        extraMessageWhenSwitchedOn = "It is showing an old western.",
        extraMessageWhenSwitchedOff = "It is currently switched off.")
    private val newspaper:Item = new Item(List("newspaper", "paper"), "The Daily Bugle.",
        gettable = true, droppable = false, switchable = false)
    private val remote:Item = new Item(List("remote"), "The TV remote", visible = false, scenery = false)

    tv.addVerb(new CustomVerb(List("WATCH {noun}")),
        "say('You watch the TV for a while.');"
    )

    //           kitchen    landing
    //                |      |
    //                N      U
    //                |      |
    //study ----W---- livingRoom ----E---- diningRoom
    //                |      |
    //                S      D
    //                |      |
    //            garden    cellar

    study.addExit(Direction.EAST, livingRoom)
    livingRoom.addExit(Direction.NORTH, kitchen)
    livingRoom.addExit(Direction.SOUTH, garden)
    livingRoom.addExit(Direction.EAST, diningRoom)
    livingRoom.addExit(Direction.WEST, study)
    livingRoom.addExit(Direction.UP, landing)
    livingRoom.addExit(Direction.DOWN, cellar)
    kitchen.addExit(Direction.SOUTH, livingRoom)
    diningRoom.addExit(Direction.WEST, livingRoom)
    garden.addExit(Direction.NORTH, livingRoom)

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

        livingRoom.addItem(lamp)
        livingRoom.addItem(tv)
        livingRoom.addItem(newspaper)
        livingRoom.addItem(remote)

        lamp.switchOff()
        tv.switchOff()

        adventure.addRoom(study)
        adventure.addRoom(livingRoom)
        adventure.addRoom(garden)
        adventure.addRoom(kitchen)
        adventure.addRoom(diningRoom)

        adventure.setStartRoom(livingRoom)

        mainWindow = new MainWindowForTesting()
        this.classUnderTest = new AdventureController(adventure, mainWindow)
        this.player = this.classUnderTest.getPlayer
    }

    test("verb: NORTH") {
        for (verbString <- this.verbs("NORTH").getSynonyms) {
            setup()
            testNorth(verbString)
        }
    }

    private def testNorth(command:String) {
        mainWindow.fireCommand(new CommandEvent(command))
        assert(this.classUnderTest.getCurrentRoom == kitchen)
    }

    test("verb: SOUTH") {
        for (verbString <- this.verbs("SOUTH").getSynonyms) {
            setup()
            testSouth(verbString)
        }
    }

    private def testSouth(command:String) {
        mainWindow.fireCommand(new CommandEvent(command))
        assert(this.classUnderTest.getCurrentRoom == garden)
    }

    test("verb: EAST") {
        for (verbString <- this.verbs("EAST").getSynonyms) {
            setup()
            testEast(verbString)
        }
    }

    private def testEast(command:String) {
        mainWindow.fireCommand(new CommandEvent(command))
        assert(this.classUnderTest.getCurrentRoom == diningRoom)
    }

    test("verb: WEST") {
        for (verbString <- this.verbs("WEST").getSynonyms) {
            setup()
            testWest(verbString)
        }
    }

    private def testWest(command:String) {
        mainWindow.fireCommand(new CommandEvent(command))
        assert(this.classUnderTest.getCurrentRoom == study)
    }

    test("verb: UP") {
        for (verbString <- this.verbs("UP").getSynonyms) {
            setup()
            testUp(verbString)
        }
    }

    private def testUp(command:String) {
        mainWindow.fireCommand(new CommandEvent(command))
        assert(this.classUnderTest.getCurrentRoom == landing)
    }

    test("verb: DOWN") {
        for (verbString <- this.verbs("DOWN").getSynonyms) {
            setup()
            testDown(verbString)
        }
    }

    private def testDown(command:String) {
        mainWindow.fireCommand(new CommandEvent(command))
        assert(this.classUnderTest.getCurrentRoom == cellar)
    }

    test("verb: LOOK") {
        for (verbString <- this.verbs("LOOK").getSynonyms) {
            setup()
            testLook(verbString)
        }
    }

    private def testLook(command:String) {
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 4)
        messages(3) should equal (livingRoom.getDescription)
        messages(2) should equal ("You can also see:")
        messages(1) should equal ("lamp")
        messages(0) should equal ("newspaper")
    }

    test("verb: EXITS") {
        mainWindow.fireCommand(new CommandEvent("exits"))
        this.mainWindow.getLastMessage should equal ("From here you can go North, South, East, West, Up, Down, ")

    }

    test("verb: EXAMINE {noun} (when item does not have an additional description") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            testExamineNoun(verbString.replaceAll("\\{noun\\}", "lamp"))
        }
    }

    private def testExamineNoun(command:String) {
        mainWindow.fireCommand(new CommandEvent(command))
        this.mainWindow.getLastMessage should equal (lamp.getDescription)
    }

    test("verb: EXAMINE {noun} (when item is switched on, and has an additional description") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            testExamineNoun_WhenSwitchedOnAndHasAdditionalDescription(verbString.replaceAll("\\{noun\\}", "tv"))
        }
    }

    private def testExamineNoun_WhenSwitchedOnAndHasAdditionalDescription(command:String) {
        mainWindow.fireCommand(new CommandEvent("turn on tv"))
        mainWindow.fireCommand(new CommandEvent(command))
        this.mainWindow.getLastMessage should equal (tv.getDescription + ".  " + tv.getExtraMessageWhenSwitchedOn)
    }

    test("verb: EXAMINE {noun} (when item is switched off, and has an additional description") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            testExamineNoun_WhenSwitchedOffAndHasAdditionalDescription(verbString.replaceAll("\\{noun\\}", "tv"))
        }
    }

    private def testExamineNoun_WhenSwitchedOffAndHasAdditionalDescription(command:String) {
        mainWindow.fireCommand(new CommandEvent(command))
        this.mainWindow.getLastMessage should equal (tv.getDescription + ".  " + tv.getExtraMessageWhenSwitchedOff)
    }

    test("verb: EXAMINE {noun} (when item is not visible") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            testExamineNoun_WhenItemIsNotVisible(verbString.replaceAll("\\{noun\\}", "remote"))
        }
    }

    private def testExamineNoun_WhenItemIsNotVisible(command:String) {
        mainWindow.fireCommand(new CommandEvent(command))
        this.mainWindow.getLastMessage should equal ("Cannot find the remote")
    }

    test("verb: GET {noun}") {
        for (verbString <- this.verbs("GET {noun}").getSynonyms) {
            setup()
            testGetNoun(verbString.replaceAll("\\{noun\\}", "lamp"))
        }
    }

    private def testGetNoun(command:String) {
        mainWindow.fireCommand(new CommandEvent(command))
        assert(!livingRoom.contains(lamp))
        assert(player.contains(lamp))
    }

    test("verb: GET {noun} (when item cannot be picked up)") {
        for (verbString <- this.verbs("GET {noun}").getSynonyms) {
            setup()
            testGetNoun_WhenItemIsNotGettable(verbString.replaceAll("\\{noun\\}", "tv"))
        }
    }

    private def testGetNoun_WhenItemIsNotGettable(command:String) {
        mainWindow.fireCommand(new CommandEvent(command))
        assert(livingRoom.contains(tv))
        assert(!player.contains(tv))
    }

    test("verb: GET {noun} (when item is not visible)") {
        for (verbString <- this.verbs("GET {noun}").getSynonyms) {
            setup()
            testGetNoun_WhenItemIsNotVisible(verbString.replaceAll("\\{noun\\}", "remote"))
        }
    }

    private def testGetNoun_WhenItemIsNotVisible(command:String) {
        mainWindow.fireCommand(new CommandEvent(command))
        assert(livingRoom.contains(remote))
        assert(!player.contains(remote))
    }

    test("verb: DROP {noun}") {
        mainWindow.fireCommand(new CommandEvent("get lamp"))
        mainWindow.fireCommand(new CommandEvent("east"))
        mainWindow.fireCommand(new CommandEvent("drop lamp"))

        assert(!livingRoom.contains(lamp))
        assert(diningRoom.contains(lamp))
        assert(!player.contains(lamp))
    }

    test("verb: DROP {noun} (when item cannot be dropped)") {
        mainWindow.fireCommand(new CommandEvent("get newspaper"))

        assert(!livingRoom.contains(newspaper))
        assert(player.contains(newspaper))

        mainWindow.fireCommand(new CommandEvent("drop newspaper"))

        assert(!livingRoom.contains(newspaper))
        assert(player.contains(newspaper))
    }

    test("verb: INVENTORY (when empty)") {
        for (verbString <- this.verbs("INVENTORY").getSynonyms) {
            setup()
            testInventory_WhenEmpty(verbString)
        }
    }

    private def testInventory_WhenEmpty(command:String) : Unit = {
        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 2)
        messages(1) should equal ("You are currently carrying:")
        messages(0) should equal ("Nothing")
    }

    test("verb: INVENTORY (when not empty)") {
        for (verbString <- this.verbs("INVENTORY").getSynonyms) {
            setup()
            testInventory_WhenNotEmpty(verbString)
        }
    }

    private def testInventory_WhenNotEmpty(command:String) {
        mainWindow.fireCommand(new CommandEvent("get lamp"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 2)
        messages(1) should equal ("You are currently carrying:")
        messages(0) should equal ("lamp")
    }

    test("verb: INVENTORY (when contains multiple items)") {
        for (verbString <- this.verbs("INVENTORY").getSynonyms) {
            setup()
            testInventory_WhenPlayerHasMultipleItems(verbString)
        }
    }

    private def testInventory_WhenPlayerHasMultipleItems(command:String) {
        mainWindow.fireCommand(new CommandEvent("get lamp"))
        mainWindow.fireCommand(new CommandEvent("get newspaper"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 3)
        messages(2) should equal ("You are currently carrying:")
        messages(1) should equal ("newspaper")
        messages(0) should equal ("lamp")
    }

    test("verb: TURN ON {noun}") {
        for (verbString <- this.verbs("TURN ON {noun}").getSynonyms) {
            setup()
            testTurnOnNoun(verbString.replaceAll("\\{noun\\}", "lamp"))
        }
    }

    private def testTurnOnNoun(command:String): Unit = {
        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        assert(lamp.isOn)

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("You turn on the lamp")
    }

    test("verb: TURN ON {noun} (when the item has a custom message defined for switching on") {
        for (verbString <- this.verbs("TURN ON {noun}").getSynonyms) {
            setup()
            testTurnOnNoun_WhenItemHasCustomMessageDefined(verbString.replaceAll("\\{noun\\}", "tv"))
        }
    }

    private def testTurnOnNoun_WhenItemHasCustomMessageDefined(command:String): Unit = {
        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        assert(tv.isOn)

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("the TV flickers into life")
    }

    test("verb: TURN ON {noun} (when the item is not switchable)") {
        for (verbString <- this.verbs("TURN ON {noun}").getSynonyms) {
            setup()
            testTurnOnNoun_WhenItemIsNotSwitchable(verbString.replaceAll("\\{noun\\}", "newspaper"))
        }
    }

    private def testTurnOnNoun_WhenItemIsNotSwitchable(command:String) : Unit = {
        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        assert(newspaper.isOff)

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("You can't turn on the newspaper")
    }

    test("verb: TURN ON {noun} (when the item is not visible)") {
        for (verbString <- this.verbs("TURN ON {noun}").getSynonyms) {
            setup()
            testTurnOnNoun_WhenItemIsNotVisible(verbString.replaceAll("\\{noun\\}", "remote"))
        }
    }

    private def testTurnOnNoun_WhenItemIsNotVisible(command:String) : Unit = {
        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("Cannot find the remote")
    }

    test("verb: TURN OFF {noun}") {
        for (verbString <- this.verbs("TURN OFF {noun}").getSynonyms) {
            setup()
            testTurnOffNoun(verbString.replaceAll("\\{noun\\}", "lamp"))
        }
    }

    private def testTurnOffNoun(command:String): Unit = {
        mainWindow.fireCommand(new CommandEvent("turn on lamp"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        assert(lamp.isOff)

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("You turn off the lamp")
    }

    test("verb: TURN OFF {noun} (when the item has a custom message defined for switching off") {
        for (verbString <- this.verbs("TURN OFF {noun}").getSynonyms) {
            setup()
            testTurnOffNoun_WhenItemHasCustomMessageDefined(verbString.replaceAll("\\{noun\\}", "tv"))
        }
    }

    private def testTurnOffNoun_WhenItemHasCustomMessageDefined(command:String) : Unit = {
        mainWindow.fireCommand(new CommandEvent("turn on tv"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        assert(tv.isOff)

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("the TV is now off")
    }

    test("verb: TURN OFF {noun} (when the item is not switchable)") {
        for (verbString <- this.verbs("TURN OFF {noun}").getSynonyms) {
            setup()
            testTurnOffNoun_WhenItemIsNotSwitchable(verbString.replaceAll("\\{noun\\}", "newspaper"))
        }
    }

    private def testTurnOffNoun_WhenItemIsNotSwitchable(command:String) : Unit = {
        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        assert(newspaper.isOff)

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("You can't turn off the newspaper")
    }

    test("verb: TURN OFF {noun} (when the item is not visible)") {
        for (verbString <- this.verbs("TURN OFF {noun}").getSynonyms) {
            setup()
            testTurnOffNoun_WhenItemIsNotVisible(verbString.replaceAll("\\{noun\\}", "remote"))
        }
    }

    private def testTurnOffNoun_WhenItemIsNotVisible(command:String) : Unit = {
        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("Cannot find the remote")
    }

    test("custom verb: WATCH {noun}") {
        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("watch tv"))

        val messages:List[String] = this.mainWindow.getMessages

        assert(messages.size == 1)
        messages(0) should equal ("You watch the TV for a while.")
    }

    test("additional words between verb and noun: GET THE {noun}") {
        mainWindow.fireCommand(new CommandEvent("get the lamp"))
        assert(!livingRoom.contains(lamp))
        assert(player.contains(lamp))
    }

    test("Noun can be referenced by it's synonyms") {
        mainWindow.fireCommand(new CommandEvent("examine newspaper"))
        this.mainWindow.getLastMessage should equal (newspaper.getDescription)

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("examine paper"))
        this.mainWindow.getLastMessage should equal (newspaper.getDescription)
    }

    // TODO Add a test for the script functions isSwitchedOn and isSwitchedOff
    // TODO: Add test where two object have the same custom verb defined
}
