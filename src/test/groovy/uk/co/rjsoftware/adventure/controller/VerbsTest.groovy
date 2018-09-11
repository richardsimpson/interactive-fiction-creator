package uk.co.rjsoftware.adventure.controller

import org.junit.Before
import org.junit.Test
import uk.co.rjsoftware.adventure.model.*
import uk.co.rjsoftware.adventure.view.CommandEvent

import static org.junit.Assert.*

class VerbsTest {

    private AdventureController classUnderTest
    private Player player
    private MainWindowForTesting mainWindow

    private Room study
    private Room livingRoom
    private Room garden
    private Room kitchen
    private Room diningRoom
    private Room landing
    private Room cellar

    private Item lamp
    private Item tv
    private Item newspaper
    private Item remote
    private Item sandwich
    private Item donut

    private CustomVerb watch = new CustomVerb("Watch", ["WATCH {noun}"])
    private CustomVerb relax = new CustomVerb("Relax", ["RELAX"])

    private final Map<String, Verb> verbs = new HashMap()

    @Before
    void before() {
        study = new Room("study", "This is your study.")
        livingRoom = new Room("livingRoom", "This is the living room.")
        garden = new Room("garden", "This is the garden.")
        kitchen = new Room("kitchen", "This is the kitchen.")
        diningRoom = new Room("diningRoom", "This is the dining room.")
        landing = new Room("landing", "This is the landing.")
        cellar = new Room("cellar", "This is the cellar.")

        lamp = new Item("lamp", "lamp", "A bedside lamp. with a simple on/off switch.")
        tv = new Item("tv", ["TV", "television"], "A 28\" TV")
        newspaper = new Item("paper", ["newspaper", "paper"], "The Daily Bugle.")
        remote = new Item("remote", "remote", "The TV remote")
        sandwich = new Item("sandwich", "sandwich", "A crusty old sandwich")
        donut = new Item("donut", "donut", "A delicious looking donut")

        lamp.setSwitchable(true)
        tv.setScenery(true)
        tv.setGettable(false)
        tv.setDroppable(false)
        tv.setSwitchable(true)
        tv.setSwitchOnMessage("the TV flickers into life")
        tv.setSwitchOffMessage("the TV is now off")
        tv.setExtraMessageWhenSwitchedOn("It is showing an old western.")
        tv.setExtraMessageWhenSwitchedOff("It is currently switched off.")
        newspaper.setDroppable(false)
        remote.setVisible(false)
        sandwich.setEdible(true)
        donut.setEdible(true)
        donut.setEatMessage("eating donut")
        donut.setOnEatScript("say('onEatScript')")

        tv.addVerb(watch, "say('You watch the TV for a while.')")
        livingRoom.addVerb(relax , "say('You sit on the floor, and veg out in front of the TV.')")
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

        for (Verb verb : StandardVerbs.getVerbs()) {
            verbs.put(verb.getVerb(), verb)
        }

        setup()
    }

    private void setup(String waitText = null) {
        final Adventure adventure = new Adventure("Welcome to the Adventure!")
        adventure.setWaitText(waitText)

        adventure.addCustomVerb(watch)
        adventure.addCustomVerb(relax)

        livingRoom.addItem(lamp)
        livingRoom.addItem(tv)
        livingRoom.addItem(newspaper)
        livingRoom.addItem(remote)

        diningRoom.addItem(donut)
        diningRoom.addItem(sandwich)

        lamp.switchOff()
        tv.switchOff()

        adventure.addRoom(study)
        adventure.addRoom(livingRoom)
        adventure.addRoom(garden)
        adventure.addRoom(kitchen)
        adventure.addRoom(diningRoom)
        adventure.addRoom(landing)
        adventure.addRoom(cellar)

        adventure.setStartRoom(livingRoom)

        this.mainWindow = new MainWindowForTesting()
        this.classUnderTest = new AdventureController(mainWindow)
        this.classUnderTest.loadAdventure(adventure)

        // read back the data from the controller, as it would have made a copy
        this.player = this.classUnderTest.getPlayer()
        study = this.classUnderTest.getAdventure().findRoom("study")
        livingRoom = this.classUnderTest.getAdventure().findRoom("livingRoom")
        garden = this.classUnderTest.getAdventure().findRoom("garden")
        kitchen = this.classUnderTest.getAdventure().findRoom("kitchen")
        diningRoom = this.classUnderTest.getAdventure().findRoom("diningRoom")
        landing = this.classUnderTest.getAdventure().findRoom("landing")
        cellar = this.classUnderTest.getAdventure().findRoom("cellar")

        lamp = livingRoom.getItem("lamp")
        tv = livingRoom.getItem("tv")
        newspaper = livingRoom.getItem("paper")
        remote = livingRoom.getItem("remote")
        sandwich = diningRoom.getItem("sandwich")
        donut = diningRoom.getItem("donut")
    }

    private void assertMessagesAreCorrect(List<String> expectedMessages) {
        final String[] messages = this.mainWindow.getMessages()
        assertEquals(expectedMessages.size(), messages.length)

        for (int index = 0; index < expectedMessages.size(); index++) {
            assertEquals(expectedMessages.get(index), messages[index])
        }
    }

    @Test
    void testNorth() {
        for (String verbString : this.verbs.get("NORTH").getSynonyms()) {
            setup()
            mainWindow.fireCommand(new CommandEvent(verbString))
            assertEquals(kitchen, this.classUnderTest.getCurrentRoom())
        }
    }

    @Test
    void testSouth() {
        for (String verbString : this.verbs.get("SOUTH").getSynonyms()) {
            setup()
            mainWindow.fireCommand(new CommandEvent(verbString))
            assertEquals(garden, this.classUnderTest.getCurrentRoom())
        }
    }

    @Test
    void testEast() {
        for (String verbString : this.verbs.get("EAST").getSynonyms()) {
            setup()
            mainWindow.fireCommand(new CommandEvent(verbString))
            assertEquals(diningRoom, this.classUnderTest.getCurrentRoom())
        }
    }

    @Test
    void testWest() {
        for (String verbString : this.verbs.get("WEST").getSynonyms()) {
            setup()
            mainWindow.fireCommand(new CommandEvent(verbString))
            assertEquals(study, this.classUnderTest.getCurrentRoom())
        }
    }

    @Test
    void testUp() {
        for (String verbString : this.verbs.get("UP").getSynonyms()) {
            setup()
            mainWindow.fireCommand(new CommandEvent(verbString))
            assertEquals(landing, this.classUnderTest.getCurrentRoom())
        }
    }

    @Test
    void testDown() {
        for (String verbString : this.verbs.get("DOWN").getSynonyms()) {
            setup()
            mainWindow.fireCommand(new CommandEvent(verbString))
            assertEquals(cellar, this.classUnderTest.getCurrentRoom())
        }
    }

    @Test
    void testLook() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    livingRoom.getDescription(),
                    "You can also see:",
                    "lamp",
                    "newspaper",
                    ""
            ])
        }
    }

    @Test
    void testLook_WhenThereAreNoItemsInTheRoom() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent("west"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    study.getDescription(),
                    ""
            ])
        }
    }

    @Test
    void testExists() {
        this.mainWindow.clearMessages()
        mainWindow.fireCommand(new CommandEvent("exits"))

        assertMessagesAreCorrect([
                "From here you can go North, South, East, West, Up, Down,",
                ""
        ])
    }

    @Test
    void testExamineNoun_WhenItemDoesNotHaveAnAdditionalDescription() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "lamp")))

            assertMessagesAreCorrect([
                    lamp.getDescription(),
                    ""
            ])

        }
    }

    @Test
    void testExamineNoun_WhenSwitchedOnAndHasAdditionalDescription() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent("turn on tv"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "tv")))

            assertMessagesAreCorrect([
                    tv.getDescription() + ".  " + tv.getExtraMessageWhenSwitchedOn(),
                    ""
            ])
        }
    }

    @Test
    void testExamineNoun_WhenSwitchedOffAndHasAdditionalDescription() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "tv")))

            assertMessagesAreCorrect([
                    tv.getDescription() + ".  " + tv.getExtraMessageWhenSwitchedOff(),
                    ""
            ])
        }
    }

    @Test
    void testExamineNoun_WhenItemIsNotVisible() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "remote")))

            assertMessagesAreCorrect([
                    "You cannot do that right now.",
                    ""
            ])
        }
    }

    @Test
    void testGetNoun() {
        for (String verbString : this.verbs.get("GET {noun}").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "lamp")))
            assertFalse(livingRoom.contains(lamp))
            assertTrue(player.contains(lamp))
        }
    }

    @Test
    void testGetNoun_WhenItemIsNotGettable() {
        for (String verbString : this.verbs.get("GET {noun}").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "tv")))
            assertTrue(livingRoom.contains(tv))
            assertFalse(player.contains(tv))
        }
    }

    @Test
    void testGetNoun_WhenItemIsNotVisible() {
        for (String verbString : this.verbs.get("GET {noun}").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "remote")))
            assertTrue(livingRoom.contains(remote))
            assertFalse(player.contains(remote))
        }
    }

    @Test
    void testDropNoun() {
        mainWindow.fireCommand(new CommandEvent("get lamp"))
        mainWindow.fireCommand(new CommandEvent("east"))
        mainWindow.fireCommand(new CommandEvent("drop lamp"))

        assertFalse(livingRoom.contains(lamp))
        assertTrue(diningRoom.contains(lamp))
        assertFalse(player.contains(lamp))
    }

    @Test
    void testDropNoun_WhenItemCannotBeDropped() {
        mainWindow.fireCommand(new CommandEvent("get newspaper"))

        assertFalse(livingRoom.contains(newspaper))
        assertTrue(player.contains(newspaper))

        mainWindow.fireCommand(new CommandEvent("drop newspaper"))

        assertFalse(livingRoom.contains(newspaper))
        assertTrue(player.contains(newspaper))
    }

    @Test
    void testInventory_WhenEmpty() {
        for (String verbString : this.verbs.get("INVENTORY").getSynonyms()) {
            setup()

            mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    "You are currently carrying:",
                    "Nothing",
                    ""
            ])
        }
    }

    @Test
    void testInventory_WhenNotEmpty() {
        for (String verbString : this.verbs.get("INVENTORY").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent("get lamp"))

            mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    "You are currently carrying:",
                    "lamp",
                    ""
            ])
        }
    }

    @Test
    void testInventory_WhenPlayerHasMultipleItems() {
        for (String verbString : this.verbs.get("INVENTORY").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent("get lamp"))
            mainWindow.fireCommand(new CommandEvent("get newspaper"))

            mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    "You are currently carrying:",
                    "lamp",
                    "newspaper",
                    ""
            ])
        }
    }

    @Test
    void testTurnOnNoun() {
        for (String verbString : this.verbs.get("TURN ON {noun}").getSynonyms()) {
            setup()

            mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "lamp")))

            assertTrue(lamp.isSwitchedOn())

            assertMessagesAreCorrect([
                    "You turn on the lamp",
                    ""
            ])
        }
    }

    @Test
    void testTurnOnNoun_WhenItemHasCustomMessageDefined() {
        for (String verbString : this.verbs.get("TURN ON {noun}").getSynonyms()) {
            setup()

            mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "tv")))

            assertTrue(tv.isSwitchedOn())

            assertMessagesAreCorrect([
                    "the TV flickers into life",
                    ""
            ])
        }
    }

    @Test
    void testTurnOnNoun_WhenItemIsNotSwitchable() {
        for (String verbString : this.verbs.get("TURN ON {noun}").getSynonyms()) {
            setup()

            mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "newspaper")))

            assertTrue(newspaper.isSwitchedOff())

            assertMessagesAreCorrect([
                    "You can't turn on the newspaper",
                    ""
            ])
        }
    }

    @Test
    void testTurnOnNoun_WhenItemIsNotVisible() {
        for (String verbString : this.verbs.get("TURN ON {noun}").getSynonyms()) {
            setup()

            mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "remote")))

            assertMessagesAreCorrect([
                    "You cannot do that right now.",
                    ""
            ])
        }
    }

    @Test
    void testTurnOffNoun() {
        for (String verbString : this.verbs.get("TURN OFF {noun}").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent("turn on lamp"))

            mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "lamp")))

            assertTrue(lamp.isSwitchedOff())

            assertMessagesAreCorrect([
                    "You turn off the lamp",
                    ""
            ])
        }
    }

    @Test
    void testTurnOffNoun_WhenItemHasCustomMessageDefined() {
        for (String verbString : this.verbs.get("TURN OFF {noun}").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent("turn on tv"))

            mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "tv")))

            assertTrue(tv.isSwitchedOff())

            assertMessagesAreCorrect([
                    "the TV is now off",
                    ""
            ])
        }
    }

    @Test
    void testTurnOffNoun_WhenItemIsNotSwitchable() {
        for (String verbString : this.verbs.get("TURN OFF {noun}").getSynonyms()) {
            setup()

            mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "newspaper")))

            assertTrue(newspaper.isSwitchedOff())

            assertMessagesAreCorrect([
                    "You can't turn off the newspaper",
                    ""
            ])
        }
    }

    @Test
    void testTurnOffNoun_WhenItemIsNotVisible() {
        for (String verbString : this.verbs.get("TURN OFF {noun}").getSynonyms()) {
            setup()

            mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "remote")))

            assertMessagesAreCorrect([
                    "You cannot do that right now.",
                    ""
            ])
        }
    }

    @Test
    void testWait_WithDefaultWaitText() {
        for (String verbString : this.verbs.get("WAIT").getSynonyms()) {
            setup()
            testWait(verbString, "time passes...")
        }
    }

    @Test
    void testWait_WithDefaultCustomText() {
        for (String verbString : this.verbs.get("WAIT").getSynonyms()) {
            setup("This is the wait text")
            testWait(verbString, "This is the wait text")
        }
    }

    private void testWait(String command, String waitText) {
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command))

        assertMessagesAreCorrect([
                waitText,
                ""
        ])
    }

    @Test
    void testEatNoun() {
        for (String verbString : this.verbs.get("EAT {noun}").getSynonyms()) {
            setup()

            assertTrue(diningRoom.contains(sandwich))


            mainWindow.fireCommand(new CommandEvent("east"))
            mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "sandwich")))

            assertFalse(diningRoom.contains(sandwich))

            assertMessagesAreCorrect([
                    "You eat the sandwich",
                    ""
            ])
        }
    }

    @Test
    void testEatNoun_WithCustomMessageAndScript() {
        for (String verbString : this.verbs.get("EAT {noun}").getSynonyms()) {
            setup()

            assertTrue(diningRoom.contains(donut))

            mainWindow.fireCommand(new CommandEvent("east"))
            mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "donut")))

            assertFalse(diningRoom.contains(donut))

            assertMessagesAreCorrect([
                    "eating donut",
                    "onEatScript",
                    ""
            ])
        }
    }

    @Test
    void testEatNoun_WhenItemNotEdible() {
        for (String verbString : this.verbs.get("EAT {noun}").getSynonyms()) {
            setup()

            assertTrue(diningRoom.contains(donut))

            mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "tv")))

            assertMessagesAreCorrect([
                    "You cannot eat the TV",
                    ""
            ])

        }
    }

    @Test
    void testCustomVerbWithNoun_WatchNoun() {
        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("watch tv"))

        assertMessagesAreCorrect([
                "You watch the TV for a while.",
                ""
        ])
    }

    @Test
    void testCustomVerbWithoutNoun_Relax_WhenInTheLivingRoom() {
        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("relax"))

        assertMessagesAreCorrect([
                "You sit on the floor, and veg out in front of the TV.",
                ""
        ])
    }

    @Test
    void testCustomVerbWithoutNoun_Relax_WhenInTheGarden() {
        mainWindow.fireCommand(new CommandEvent("south"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("relax"))

        assertMessagesAreCorrect([
                "You sunbathe for a while.  Difficult without anything to sit on though.",
                ""
        ])
    }

    @Test
    void testAdditionalWordsBetweenVerbAndNoun() {
        mainWindow.fireCommand(new CommandEvent("get the lamp"))
        assertFalse(livingRoom.contains(lamp))
        assertTrue(player.contains(lamp))
    }

    @Test
    void testNounCanBeReferencedByItsSynonyms() {
        mainWindow.clearMessages()
        mainWindow.fireCommand(new CommandEvent("examine newspaper"))

        assertMessagesAreCorrect([
                newspaper.getDescription(),
                ""
        ])

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("examine paper"))

        assertMessagesAreCorrect([
                newspaper.getDescription(),
                ""
        ])
    }

    // TODO Add a test for the script functions isSwitchedOn and isSwitchedOff
    // TODO: Add test where two object have the same custom verb defined

    @Test
    void testRestart() {
        for (String verbString : this.verbs.get("RESTART").getSynonyms()) {
            setup()

            assertTrue(livingRoom.contains(lamp))
            assertTrue(livingRoom.contains(newspaper))

            mainWindow.fireCommand(new CommandEvent("GET lamp"))
            mainWindow.fireCommand(new CommandEvent("GET newspaper"))
            mainWindow.fireCommand(new CommandEvent("TURN ON lamp"))
            mainWindow.fireCommand(new CommandEvent("TURN ON tv"))

            mainWindow.fireCommand(new CommandEvent("NORTH"))
            mainWindow.fireCommand(new CommandEvent("DROP lamp"))

            assertEquals(kitchen, this.classUnderTest.getCurrentRoom())
            assertTrue(player.contains(newspaper))
            assertTrue(kitchen.contains(lamp))
            assertFalse(livingRoom.contains(lamp))
            assertFalse(livingRoom.contains(newspaper))
            assertTrue(lamp.isSwitchedOn())
            assertTrue(tv.isSwitchedOn())

            mainWindow.fireCommand(new CommandEvent(verbString))
            mainWindow.fireCommand(new CommandEvent("yes"))

            // check that the adventure has reverted to it's original state
            final newPlayer = this.classUnderTest.getPlayer()
            final newLivingRoom = this.classUnderTest.getAdventure().findRoom("livingRoom")
            final newKitchen = this.classUnderTest.getAdventure().findRoom("kitchen")
            final newLamp = newLivingRoom.getItem("lamp")
            final newTv = newLivingRoom.getItem("tv")

            assertEquals(newLivingRoom, this.classUnderTest.getCurrentRoom())
            assertFalse(newPlayer.contains(newspaper))
            assertFalse(newPlayer.contains(lamp))
            assertFalse(newKitchen.contains(lamp))
            assertTrue(newLivingRoom.contains(lamp))
            assertTrue(newLivingRoom.contains(newspaper))
            assertFalse(newLamp.isSwitchedOn())
            assertFalse(newTv.isSwitchedOn())
        }
    }


}
