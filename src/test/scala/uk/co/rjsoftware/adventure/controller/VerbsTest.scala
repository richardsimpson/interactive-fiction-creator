package uk.co.rjsoftware.adventure.controller

import org.scalatest.FunSuite
import org.scalatest.Matchers._
import uk.co.rjsoftware.adventure.model._
import uk.co.rjsoftware.adventure.view.{CommandEvent, MainWindow, MainWindowView}

/**
  * Created by richardsimpson on 20/05/2017.
  * See http://www.scalatest.org/at_a_glance/FunSuite
  */
class VerbsTest extends FunSuite {

    private var adventure:Adventure = _

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

    private val lamp:Item = new Item("lamp", List("lamp"), "A bedside lamp. with a simple on/off switch.",
        switchable = true)
    private val tv:Item = new Item("tv", List("TV", "television"), "A 28\" TV",
        visible = true, scenery = true, gettable = false, droppable = false,
        switchable = true, switchOnMessage = "the TV flickers into life", switchOffMessage = "the TV is now off",
        extraMessageWhenSwitchedOn = "It is showing an old western.",
        extraMessageWhenSwitchedOff = "It is currently switched off.")
    private val newspaper:Item = new Item("paper", List("newspaper", "paper"), "The Daily Bugle.",
        gettable = true, droppable = false, switchable = false)
    private val remote:Item = new Item("remote", List("remote"), "The TV remote", visible = false, scenery = false)
    private val sandwich:Item = new Item("sandwich", List("sandwich"), "A crusty old sandwich", edible = true)
    private val donut:Item = new Item("donut", List("donut"), "A delicious looking donut", edible = true,
        eatMessage = "eating donut", onEatScript = "say('onEatScript')")

    private val watch = new CustomVerb("Watch", List("WATCH {noun}"))
    private val relax = new CustomVerb("Relax", List("RELAX"))

    tv.addVerb(watch, "say('You watch the TV for a while.')")
    livingRoom.addVerb(relax , "say('You sit on the floor, and veg out in fron of the TV.')")
    garden.addVerb(relax , "say('You sunbathe for a while.  Difficult without anything to sit on though.')")

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
        this.adventure = new Adventure("Welcome to the Adventure!")
        this.adventure.setWaitText("This is the wait text")

        this.adventure.addCustomVerb(watch)
        this.adventure.addCustomVerb(relax)

        livingRoom.addItem(lamp)
        livingRoom.addItem(tv)
        livingRoom.addItem(newspaper)
        livingRoom.addItem(remote)

        diningRoom.addItem(donut)
        diningRoom.addItem(sandwich)

        lamp.switchOff()
        tv.switchOff()

        this.adventure.addRoom(study)
        this.adventure.addRoom(livingRoom)
        this.adventure.addRoom(garden)
        this.adventure.addRoom(kitchen)
        this.adventure.addRoom(diningRoom)

        this.adventure.setStartRoom(livingRoom)

        this.mainWindow = new MainWindowForTesting()
        this.classUnderTest = new AdventureController(mainWindow)
        this.classUnderTest.loadAdventure(this.adventure)
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

        assertMessagesAreCorrect(List(
            livingRoom.getDescription,
            "You can also see:",
            "lamp",
            "newspaper",
            ""
        ))
    }

    test("verb: LOOK (when there are no items in the room") {
        for (verbString <- this.verbs("LOOK").getSynonyms) {
            setup()
            testLook_WhenThereAreNoItemsInTheRoom(verbString)
        }
    }

    private def testLook_WhenThereAreNoItemsInTheRoom(command:String) {
        mainWindow.fireCommand(new CommandEvent("west"))

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        assertMessagesAreCorrect(List(
            study.getDescription,
            ""
        ))
    }

    private def assertMessagesAreCorrect(expectedMessages : List[String]): Unit = {
        val messages:Array[String] = this.mainWindow.getMessages
        assert(messages.length == expectedMessages.size)

        for (index <- expectedMessages.indices) {
            messages(index) should equal (expectedMessages(index))
        }
    }

    test("verb: EXITS") {
        this.mainWindow.clearMessages()
        mainWindow.fireCommand(new CommandEvent("exits"))

        assertMessagesAreCorrect(List(
            "From here you can go North, South, East, West, Up, Down,",
            ""
        ))
    }

    test("verb: EXAMINE {noun} (when item does not have an additional description") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            testExamineNoun(verbString.replaceAll("\\{noun\\}", "lamp"))
        }
    }

    private def testExamineNoun(command:String) {
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        assertMessagesAreCorrect(List(
            lamp.getDescription,
            ""
        ))
    }

    test("verb: EXAMINE {noun} (when item is switched on, and has an additional description") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            testExamineNoun_WhenSwitchedOnAndHasAdditionalDescription(verbString.replaceAll("\\{noun\\}", "tv"))
        }
    }

    private def testExamineNoun_WhenSwitchedOnAndHasAdditionalDescription(command:String) {
        mainWindow.fireCommand(new CommandEvent("turn on tv"))

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        assertMessagesAreCorrect(List(
            tv.getDescription + ".  " + tv.getExtraMessageWhenSwitchedOn,
            ""
        ))
    }

    test("verb: EXAMINE {noun} (when item is switched off, and has an additional description") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            testExamineNoun_WhenSwitchedOffAndHasAdditionalDescription(verbString.replaceAll("\\{noun\\}", "tv"))
        }
    }

    private def testExamineNoun_WhenSwitchedOffAndHasAdditionalDescription(command:String) {
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        assertMessagesAreCorrect(List(
            tv.getDescription + ".  " + tv.getExtraMessageWhenSwitchedOff,
            ""
        ))
    }

    test("verb: EXAMINE {noun} (when item is not visible") {
        for (verbString <- this.verbs("EXAMINE {noun}").getSynonyms) {
            setup()
            testExamineNoun_WhenItemIsNotVisible(verbString.replaceAll("\\{noun\\}", "remote"))
        }
    }

    private def testExamineNoun_WhenItemIsNotVisible(command:String) {
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        assertMessagesAreCorrect(List(
            "You cannot do that right now.",
            ""
        ))
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

        assertMessagesAreCorrect(List(
            "You are currently carrying:",
            "Nothing",
            ""
        ))
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

        assertMessagesAreCorrect(List(
            "You are currently carrying:",
            "lamp",
            ""
        ))
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

        assertMessagesAreCorrect(List(
            "You are currently carrying:",
            "lamp",
            "newspaper",
            ""
        ))
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

        assert(lamp.isSwitchedOn)

        assertMessagesAreCorrect(List(
            "You turn on the lamp",
            ""
        ))
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

        assert(tv.isSwitchedOn)

        assertMessagesAreCorrect(List(
            "the TV flickers into life",
            ""
        ))
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

        assert(newspaper.isSwitchedOff)

        assertMessagesAreCorrect(List(
            "You can't turn on the newspaper",
            ""
        ))
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

        assertMessagesAreCorrect(List(
            "You cannot do that right now.",
            ""
        ))
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

        assert(lamp.isSwitchedOff)

        assertMessagesAreCorrect(List(
            "You turn off the lamp",
            ""
        ))
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

        assert(tv.isSwitchedOff)

        assertMessagesAreCorrect(List(
            "the TV is now off",
            ""
        ))
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

        assert(newspaper.isSwitchedOff)

        assertMessagesAreCorrect(List(
            "You can't turn off the newspaper",
            ""
        ))
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

        assertMessagesAreCorrect(List(
            "You cannot do that right now.",
            ""
        ))
    }

    test("verb: WAIT (with default wait text)") {
        for (verbString <- this.verbs("WAIT").getSynonyms) {
            setup()
            this.adventure.setWaitText(null)
            testWait(verbString, "time passes...")
        }
    }

    test("verb: WAIT (with custom wait text)") {
        for (verbString <- this.verbs("WAIT").getSynonyms) {
            setup()
            testWait(verbString, "This is the wait text")
        }
    }

    private def testWait(command:String, waitText:String) {
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        assertMessagesAreCorrect(List(
            waitText,
            ""
        ))
    }

    test("verb: EAT {noun}") {
        for (verbString <- this.verbs("EAT {noun}").getSynonyms) {
            setup()
            testEatNoun(verbString.replaceAll("\\{noun\\}", "sandwich"))
        }
    }

    private def testEatNoun(command:String): Unit = {
        assert(diningRoom.contains(sandwich))


        mainWindow.fireCommand(new CommandEvent("east"))
        mainWindow.clearMessages()
        mainWindow.fireCommand(new CommandEvent(command))

        assert(!diningRoom.contains(sandwich))

        assertMessagesAreCorrect(List(
            "You eat the sandwich",
            ""
        ))
    }

    test("verb: EAT {noun} (with custom message and script") {
        for (verbString <- this.verbs("EAT {noun}").getSynonyms) {
            setup()
            testEatNounWithCustomMessageAndScript(verbString.replaceAll("\\{noun\\}", "donut"))
        }
    }

    private def testEatNounWithCustomMessageAndScript(command:String): Unit = {
        assert(diningRoom.contains(donut))


        mainWindow.fireCommand(new CommandEvent("east"))
        mainWindow.clearMessages()
        mainWindow.fireCommand(new CommandEvent(command))

        assert(!diningRoom.contains(donut))

        assertMessagesAreCorrect(List(
            "eating donut",
            "onEatScript",
            ""
        ))
    }

    test("verb: EAT {noun} (when item not edible)") {
        for (verbString <- this.verbs("EAT {noun}").getSynonyms) {
            setup()
            testEatNounWhenItemNotEdible(verbString.replaceAll("\\{noun\\}", "tv"))
        }
    }

    private def testEatNounWhenItemNotEdible(command:String): Unit = {
        assert(diningRoom.contains(donut))

        mainWindow.clearMessages()
        mainWindow.fireCommand(new CommandEvent(command))

        assertMessagesAreCorrect(List(
            "You cannot eat the TV",
            ""
        ))
    }

    test("custom verb with noun: WATCH {noun}") {
        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("watch tv"))

        assertMessagesAreCorrect(List(
            "You watch the TV for a while.",
            ""
        ))
    }

    test("custom verb without noun: RELAX (in the living room") {
        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("relax"))

        assertMessagesAreCorrect(List(
            "You sit on the floor, and veg out in fron of the TV.",
            ""
        ))
    }

    test("custom verb without noun: RELAX (in the garden") {
        mainWindow.fireCommand(new CommandEvent("south"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("relax"))

        assertMessagesAreCorrect(List(
            "You sunbathe for a while.  Difficult without anything to sit on though.",
            ""
        ))
    }

    test("additional words between verb and noun: GET THE {noun}") {
        mainWindow.fireCommand(new CommandEvent("get the lamp"))
        assert(!livingRoom.contains(lamp))
        assert(player.contains(lamp))
    }

    test("Noun can be referenced by it's synonyms") {
        mainWindow.clearMessages()
        mainWindow.fireCommand(new CommandEvent("examine newspaper"))

        assertMessagesAreCorrect(List(
            newspaper.getDescription,
            ""
        ))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("examine paper"))

        assertMessagesAreCorrect(List(
            newspaper.getDescription,
            ""
        ))
    }

    // TODO Add a test for the script functions isSwitchedOn and isSwitchedOff
    // TODO: Add test where two object have the same custom verb defined
}
