package uk.co.rjsoftware.adventure.controller

import groovy.transform.TypeChecked
import org.junit.Before
import org.junit.Test
import uk.co.rjsoftware.adventure.model.*
import uk.co.rjsoftware.adventure.view.CommandEvent

import static org.junit.Assert.*

@TypeChecked
class VerbsTest {

    private Adventure adventure

    private AdventureController classUnderTest
    private Item player = new Item(1, "player")
    private IPlayerAppViewForTesting mainWindow

    private String waitText = "This is the wait text"
    private String getText = "This is the get text"

    private Room study = new Room(1, "study", "This is your study.")
    private Room livingRoom = new Room(2, "livingRoom", "This is the living room.")
    private Room garden = new Room(3, "garden", "This is the garden.")
    private Room kitchen = new Room(4, "kitchen", "This is the kitchen.")
    private Room diningRoom = new Room(5, "diningRoom", "This is the dining room.")
    private Room landing = new Room(6, "landing", "This is the landing.")
    private Room cellar = new Room(7, "cellar", "This is the cellar.")

    private Item lamp = new Item(2, "lamp", "lamp","A bedside lamp. with a simple on/off switch.")
    private Item tv = new Item(3, "tv", "TV", ["television"], "A 28\" TV")
    private Item newspaper = new Item(4, "paper", "newspaper", ["paper"], "The Daily Bugle.")
    private Item remote = new Item(5, "remote", "remote", "The TV remote")
    private Item sandwich = new Item(6, "sandwich", "sandwich", "A crusty old sandwich")
    private Item donut = new Item(7, "donut", "donut", "A delicious looking donut")

    private CustomVerb watch = new CustomVerb("Watch", "Watch", ["WATCH {noun}"])
    private CustomVerb relax = new CustomVerb("Relax", "Relax", ["RELAX"])

    private final Map<String, Verb> verbs = new HashMap()

    @Before
    void before() {
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
        donut.setOnEat {say('onEatScript')}

        tv.addVerb(watch) {say('You watch the TV for a while.')}
        livingRoom.addVerb(relax) {say('You sit on the floor, and veg out in front of the TV.')}
        garden.addVerb(relax) {say('You sunbathe for a while.  Difficult without anything to sit on though.')}

        //           kitchen    landing
        //                |      |
        //                N      U
        //                |      |
        //study ----W---- livingRoom ----E---- diningRoom
        //                |      |
        //                S      D
        //                |      |
        //            garden    cellar

        study.addExit(new Exit(Direction.EAST, livingRoom))
        livingRoom.addExit(new Exit(Direction.NORTH, kitchen))
        livingRoom.addExit(new Exit(Direction.SOUTH, garden))
        livingRoom.addExit(new Exit(Direction.EAST, diningRoom))
        livingRoom.addExit(new Exit(Direction.WEST, study))
        livingRoom.addExit(new Exit(Direction.UP, landing))
        livingRoom.addExit(new Exit(Direction.DOWN, cellar))
        kitchen.addExit(new Exit(Direction.SOUTH, livingRoom))
        diningRoom.addExit(new Exit(Direction.WEST, livingRoom))
        garden.addExit(new Exit(Direction.NORTH, livingRoom))

        for (Verb verb : StandardVerbs.getVerbs()) {
            verbs.put(verb.getVerb(), verb)
        }

        setup()
    }

    private void setup() {
        this.adventure = new Adventure("Welcome to the Adventure!")
        this.adventure.setWaitText(this.waitText)
        this.adventure.setGetText(this.getText)

        this.adventure.addCustomVerb(watch)
        this.adventure.addCustomVerb(relax)

        livingRoom.addItem(lamp)
        livingRoom.addItem(tv)
        livingRoom.addItem(newspaper)
        livingRoom.addItem(remote)
        livingRoom.addItem(player)

        diningRoom.addItem(donut)
        diningRoom.addItem(sandwich)

        lamp.switchOff()
        tv.switchOff()

        this.adventure.addRoom(study)
        this.adventure.addRoom(livingRoom)
        this.adventure.addRoom(garden)
        this.adventure.addRoom(kitchen)
        this.adventure.addRoom(diningRoom)
        this.adventure.addRoom(landing)
        this.adventure.addRoom(cellar)

        this.mainWindow = new IPlayerAppViewForTesting()
        this.classUnderTest = new AdventureController(mainWindow)
        this.classUnderTest.loadAdventure(this.adventure)
        this.player = this.classUnderTest.getPlayer()

        // get room and item references back from the AdventureController, as that now copies the adventure
        // during the loading process
        this.adventure = this.classUnderTest.getAdventure()

        this.study = this.classUnderTest.getRoom(this.study.name)
        this.livingRoom = this.classUnderTest.getRoom(this.livingRoom.name)
        this.garden = this.classUnderTest.getRoom(this.garden.name)
        this.kitchen = this.classUnderTest.getRoom(this.kitchen.name)
        this.diningRoom = this.classUnderTest.getRoom(this.diningRoom.name)
        this.landing = this.classUnderTest.getRoom(this.landing.name)
        this.cellar = this.classUnderTest.getRoom(this.cellar.name)

        this.lamp = this.classUnderTest.getItemByName(this.lamp.getName())
        this.tv = this.classUnderTest.getItemByName(this.tv.getName())
        this.newspaper = this.classUnderTest.getItemByName(this.newspaper.getName())
        this.remote = this.classUnderTest.getItemByName(this.remote.getName())
        this.sandwich = this.classUnderTest.getItemByName(this.sandwich.getName())
        this.donut = this.classUnderTest.getItemByName(this.donut.getName())
    }

    private void assertMessagesAreCorrect(List<String> expectedMessages) {
        final String[] messages = this.mainWindow.getMessages()
        assertEquals(expectedMessages.join(System.lineSeparator()), messages.join(System.lineSeparator()))
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
                    "From here you can go North, South, East, West, Up, Down",
                    "You can also see:",
                    "lamp",
                    "newspaper",
                    ""
            ])
        }
    }

    @Test
    void testLook_WithPrefix() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()
            livingRoom.getExits().values().forEach {Exit exit ->
                exit.setPrefix("p")
            }

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    livingRoom.getDescription(),
                    "From here you can go p North, p South, p East, p West, p Up, p Down",
                    "You can also see:",
                    "lamp",
                    "newspaper",
                    ""
            ])
        }
    }

    @Test
    void testLook_WithSuffix() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()
            livingRoom.getExits().values().forEach {Exit exit ->
                exit.setSuffix("s")
            }

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    livingRoom.getDescription(),
                    "From here you can go North s, South s, East s, West s, Up s, Down s",
                    "You can also see:",
                    "lamp",
                    "newspaper",
                    ""
            ])
        }
    }

    @Test
    void testLook_WithPrefixAndSuffix() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()
            livingRoom.getExits().values().forEach {Exit exit ->
                exit.setPrefix("p")
                exit.setSuffix("s")
            }

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    livingRoom.getDescription(),
                    "From here you can go p North s, p South s, p East s, p West s, p Up s, p Down s",
                    "You can also see:",
                    "lamp",
                    "newspaper",
                    ""
            ])
        }
    }

    @Test
    void testLook_WhenExitsAreScenery() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()

            livingRoom.getExits().values().forEach {Exit exit ->
                exit.setScenery(true)
            }

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
                    "From here you can go East",
                    ""
            ])
        }
    }

    @Test
    void testLook_WhenThereAreNoItemsInTheRoom_AndExitsAreScenery() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()

            study.getExits().values().forEach {Exit exit ->
                exit.setScenery(true)
            }

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
    void testExits() {
        this.mainWindow.clearMessages()
        mainWindow.fireCommand(new CommandEvent("exits"))

        assertMessagesAreCorrect([
                "From here you can go North, South, East, West, Up, Down",
                ""
        ])
    }

    @Test
    void testExits_WithPrefix() {
        livingRoom.getExits().values().forEach {Exit exit ->
            exit.setPrefix("p")
        }

        this.mainWindow.clearMessages()
        mainWindow.fireCommand(new CommandEvent("exits"))

        assertMessagesAreCorrect([
                "From here you can go p North, p South, p East, p West, p Up, p Down",
                ""
        ])
    }

    @Test
    void testExits_WithSuffix() {
        livingRoom.getExits().values().forEach {Exit exit ->
            exit.setSuffix("s")
        }

        this.mainWindow.clearMessages()
        mainWindow.fireCommand(new CommandEvent("exits"))

        assertMessagesAreCorrect([
                "From here you can go North s, South s, East s, West s, Up s, Down s",
                ""
        ])
    }

    @Test
    void testExits_WithPrefixAndSuffix() {
        livingRoom.getExits().values().forEach {Exit exit ->
            exit.setPrefix("p")
            exit.setSuffix("s")
        }

        this.mainWindow.clearMessages()
        mainWindow.fireCommand(new CommandEvent("exits"))

        assertMessagesAreCorrect([
                "From here you can go p North s, p South s, p East s, p West s, p Up s, p Down s",
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
            this.adventure.setWaitText(null)
            testWait(verbString, "time passes...")
        }
    }

    @Test
    void testWait_WithDefaultCustomText() {
        for (String verbString : this.verbs.get("WAIT").getSynonyms()) {
            setup()
            testWait(verbString, this.waitText)
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
    void testGet_WithDefaultGetText() {
        for (String verbString : this.verbs.get("GET {noun}").getSynonyms()) {
            setup()
            this.adventure.setGetText(null)
            testGet(verbString, "You pick up the lamp")
        }
    }

    @Test
    void testGet_WithDefaultCustomText() {
        for (String verbString : this.verbs.get("GET {noun}").getSynonyms()) {
            setup()
            testGet(verbString, this.getText)
        }
    }

    private void testGet(String command, String getText) {
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent(command.replaceAll("\\{noun}", "lamp")))

        assertMessagesAreCorrect([
                getText,
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
            final newLivingRoom = this.classUnderTest.getRoom("livingRoom")
            final newKitchen = this.classUnderTest.getRoom("kitchen")
            final newLamp = newLivingRoom.getItemByName("lamp")
            final newTv = newLivingRoom.getItemByName("tv")

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
