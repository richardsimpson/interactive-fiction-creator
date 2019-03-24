package uk.co.rjsoftware.adventure.controller

import groovy.transform.TypeChecked
import org.junit.Before
import org.junit.Test
import uk.co.rjsoftware.adventure.model.*
import uk.co.rjsoftware.adventure.view.CommandEvent

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

@TypeChecked
class ContainerTest {

    private AdventureController classUnderTest
    private Item player = new Item("player")
    private IPlayerAppViewForTesting mainWindow

    private Room livingRoom = new Room("livingRoom", "This is the living room.")

    private Item chest = new Item("chest", "chest", "This is the chest.")

    private Item goldCoin = new Item("coin", "gold coin", ["coin"], "This coin is gold.")
    private Item notepad = new Item("notepad", "notepad", "A notebook with strange writing on it.")

    private final Map<String, Verb> verbs = new HashMap()

    @Before
    void before() {
        for (Verb verb : StandardVerbs.getVerbs()) {
            verbs.put(verb.getVerb(), verb)
        }

        chest.addItem(goldCoin)
        chest.addItem(notepad)

        chest.setContainer(true)
        chest.setOpenMessage("The chest is now open")
        chest.setCloseMessage("The chest is now closed")
        chest.setOnOpen {
            say('onOpenScript')
        }
        chest.setOnClose {
            say('onCloseScript')
        }
    }

    private void setup() {
        final Adventure adventure = new Adventure("Welcome to the Adventure!")

        livingRoom.addItem(chest)
        livingRoom.addItem(player)

        chest.setOpen(false)
        chest.setContentVisibility(ContentVisibility.AFTER_EXAMINE)
        chest.setItemExamined(false)
        chest.setOpenable(true)
        chest.setCloseable(true)

        adventure.addRoom(livingRoom)

        this.mainWindow = new IPlayerAppViewForTesting()
        this.classUnderTest = new AdventureController(mainWindow)
        this.classUnderTest.loadAdventure(adventure)
        this.player = this.classUnderTest.getPlayer()

        // get room and item references back from the AdventureController, as that now copies the adventure
        // during the loading process
        this.livingRoom = this.classUnderTest.getRoom(this.livingRoom.name)
        this.chest = this.classUnderTest.getItemByName(this.chest.getName())
        this.goldCoin = this.classUnderTest.getItemByName(this.goldCoin.getName())
        this.notepad = this.classUnderTest.getItemByName(this.notepad.getName())
    }

    private void assertMessagesAreCorrect(List<String> expectedMessages) {
        final String[] messages = this.mainWindow.getMessages()
        assertEquals(expectedMessages.join(System.lineSeparator()), messages.join(System.lineSeparator()))
    }

    @Test
    void testOpenChest() {
        for (String verbString : this.verbs.get("OPEN {noun}").getSynonyms()) {
            setup()

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "chest")))

            assertMessagesAreCorrect([
                    "The chest is now open",
                    "onOpenScript",
                    ""
            ])

            assertTrue(this.chest.isOpen())
        }
    }

    @Test
    void testCloseChest() {
        for (String verbString : this.verbs.get("CLOSE {noun}").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "chest")))

            assertMessagesAreCorrect([
                    "The chest is now closed",
                    "onCloseScript",
                    ""
            ])

            assertFalse(this.chest.isOpen())
        }
    }

    @Test
    void testOpenChestWhenItIsAlreadyOpen() {
        for (String verbString : this.verbs.get("OPEN {noun}").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "chest")))

            assertMessagesAreCorrect([
                    "chest is already open",
                    ""
            ])
        }
    }

    @Test
    void testCloseChestWhenItIsAlreadyClosed() {
        for (String verbString : this.verbs.get("CLOSE {noun}").getSynonyms()) {
            setup()

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "chest")))

            assertMessagesAreCorrect([
                    "chest is already closed",
                    ""
            ])
        }
    }

    @Test
    void testOpenChestWhenChestCannotBeOpened() {
        for (String verbString : this.verbs.get("OPEN {noun}").getSynonyms()) {
            setup()
            this.chest.setOpenable(false)

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "chest")))

            assertMessagesAreCorrect([
                    "You cannot open the chest",
                    ""
            ])

            assertFalse(this.chest.isOpen())
        }
    }

    @Test
    void testCloseChestWhenChestCannotBeClosed() {
        for (String verbString : this.verbs.get("CLOSE {noun}").getSynonyms()) {
            setup()
            this.chest.setCloseable(false)

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "chest")))

            assertMessagesAreCorrect([
                    "You cannot close the chest",
                    ""
            ])

            assert(this.chest.isOpen())
        }
    }

    @Test
    void testLookWhenContainerClosedAndContentsInitiallyHidden() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    "This is the living room.",
                    "You can also see:",
                    chest.getDisplayName(),
                    ""
            ])
        }
    }

    @Test
    void testLookWhenContainerOpenAndContentsInitiallyHidden() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    "This is the living room.",
                    "You can also see:",
                    chest.getDisplayName(),
                    ""
            ])
        }
    }

    @Test
    void testLookWhenContainerClosedAndContentsAlwaysVisible() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.ALWAYS)

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    "This is the living room.",
                    "You can also see:",
                    chest.getDisplayName(),
                    ""
            ])
        }
    }

    @Test
    void testLookWhenContainerOpenAndContentsAlwaysVisible() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.ALWAYS)

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    "This is the living room.",
                    "You can also see:",
                    chest.getDisplayName() + ", containing:",
                    "    " + goldCoin.getDisplayName(),
                    "    " + notepad.getDisplayName(),
                    ""
            ])
        }
    }

    @Test
    void testLookWhenContainerClosedAndContentsNeverVisible() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.NEVER)

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    "This is the living room.",
                    "You can also see:",
                    chest.getDisplayName(),
                    ""
            ])
        }
    }

    @Test
    void testLookWhenContainerOpenAndContentsNeverVisible() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.NEVER)

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    "This is the living room.",
                    "You can also see:",
                    chest.getDisplayName(),
                    ""
            ])
        }
    }

    @Test
    void testExamineWhenContainerClosedAndContentsInitiallyHidden() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "chest")))

            assertMessagesAreCorrect([
                    "This is the chest.",
                    ""
            ])
        }
    }

    @Test
    void testExamineWhenContainerOpenAndContentsInitiallyHidden() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "chest")))

            assertMessagesAreCorrect([
                    "This is the chest.  It contains:",
                    goldCoin.getDisplayName(),
                    notepad.getDisplayName(),
                    ""
            ])
        }
    }

    @Test
    void testExamineWhenContainerClosedAndContentsAlwaysVisible() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.ALWAYS)

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "chest")))

            assertMessagesAreCorrect([
                    "This is the chest.",
                    ""
            ])
        }
    }

    @Test
    void testExamineWhenContainerOpenAndContentsAlwaysVisible() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.ALWAYS)

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "chest")))

            assertMessagesAreCorrect([
                    "This is the chest.  It contains:",
                    goldCoin.getDisplayName(),
                    notepad.getDisplayName(),
                    ""
            ])
        }
    }

    @Test
    void testExamineWhenContainerClosedAndContentsNeverVisible() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.NEVER)

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "chest")))

            assertMessagesAreCorrect([
                    "This is the chest.",
                    ""
            ])
        }
    }

    @Test
    void testExamineWhenContainerOpenAndContentsNeverVisible() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()
            this.chest.setContentVisibility(ContentVisibility.NEVER)

            mainWindow.fireCommand(new CommandEvent("open chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "chest")))

            assertMessagesAreCorrect([
                    "This is the chest.",
                    ""
            ])
        }
    }

    @Test
    void testExamineWhenContainerClosedThatWasPreviouslyExaminedWhenOpenAndContentsInitiallyHidden() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent("open chest"))
            mainWindow.fireCommand(new CommandEvent("examine chest"))
            mainWindow.fireCommand(new CommandEvent("close chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "chest")))

            assertMessagesAreCorrect([
                    "This is the chest.",
                    ""
            ])
        }
    }

    @Test
    void testLookAfterExaminingAnOpenContainerAndContentsInitiallyHidden() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent("open chest"))
            mainWindow.fireCommand(new CommandEvent("examine chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    "This is the living room.",
                    "You can also see:",
                    chest.getDisplayName() + ", containing:",
                    "    " + goldCoin.getDisplayName(),
                    "    " + notepad.getDisplayName(),
                    ""
            ])
        }
    }

    @Test
    void testLookAfterExaminingAClosedContainerAndContentsInitiallyHidden() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent("examine chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    "This is the living room.",
                    "You can also see:",
                    chest.getDisplayName(),
                    ""
            ])
        }
    }

    @Test
    void testLookWhenTheContainerWasClosedAfterBeingExaminedAndContentsInitiallyHidden() {
        for (String verbString : this.verbs.get("LOOK").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent("open chest"))
            mainWindow.fireCommand(new CommandEvent("examine chest"))
            mainWindow.fireCommand(new CommandEvent("close chest"))

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString))

            assertMessagesAreCorrect([
                    "This is the living room.",
                    "You can also see:",
                    chest.getDisplayName(),
                    ""
            ])
        }
    }

}
