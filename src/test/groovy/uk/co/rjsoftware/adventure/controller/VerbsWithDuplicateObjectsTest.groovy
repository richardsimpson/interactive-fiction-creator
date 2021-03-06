package uk.co.rjsoftware.adventure.controller

import groovy.transform.TypeChecked
import org.junit.Before
import org.junit.Test
import uk.co.rjsoftware.adventure.model.*
import uk.co.rjsoftware.adventure.view.CommandEvent

import static org.junit.Assert.*

@TypeChecked
class VerbsWithDuplicateObjectsTest {

    private AdventureController classUnderTest
    private Item player = new Item("player")
    private MainWindowForTesting mainWindow

    private Room livingRoom = new Room("livingRoom", "This is the living room.")

    private Item redBox = new Item("redbox", ["red box", "box"], "This is the red box.")

    private Item blueBox = new Item("bluebox", ["blue box", "box"], "This is the blue box.")

    private final Map<String, Verb> verbs = new HashMap()

    @Before
    void before() {
        for (Verb verb : StandardVerbs.getVerbs()) {
            verbs.put(verb.getVerb(), verb)
        }
    }

    private void setup() {
        final Adventure adventure = new Adventure("Welcome to the Adventure!")

        livingRoom.addItem(redBox)
        livingRoom.addItem(blueBox)
        livingRoom.addItem(player)

        redBox.setVisible(true)
        blueBox.setVisible(false)

        adventure.addRoom(livingRoom)

        this.mainWindow = new MainWindowForTesting()
        this.classUnderTest = new AdventureController(mainWindow)
        this.classUnderTest.loadAdventure(adventure)
        this.player = this.classUnderTest.getPlayer()

        // get room and item references back from the AdventureController, as that now copies the adventure
        // during the loading process
        this.livingRoom = this.classUnderTest.getRoom(this.livingRoom.name)
        this.redBox = this.classUnderTest.getItem(this.redBox.id)
        this.blueBox = this.classUnderTest.getItem(this.blueBox.id)
    }

    private void assertMessagesAreCorrect(List<String> expectedMessages) {
        final String[] messages = this.mainWindow.getMessages()
        assertEquals(expectedMessages.join(System.lineSeparator()), messages.join(System.lineSeparator()))
    }

    @Test
    void testExamineBox_WhenRedBoxIsVisible() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "box")))

            assertMessagesAreCorrect([
                    redBox.getDescription(),
                    ""
            ])
        }
    }

    @Test
    void testExamineBox_WhenBlueBoxIsVisible() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()
            redBox.setVisible(false)
            blueBox.setVisible(true)

            this.mainWindow.clearMessages()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "box")))

            assertMessagesAreCorrect([
                    blueBox.getDescription(),
                    ""
            ])
        }
    }

    @Test
    void testExamineBox_WhenBothBoxesAreVisibleAndSelectTheRedBox() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()
            redBox.setVisible(true)
            blueBox.setVisible(true)

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "box")))

            assertMessagesAreCorrect([
                    "Examine what?",
                    "1) blue box",
                    "2) red box",
                    ""
            ])

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent("2"))

            assertMessagesAreCorrect([
                    redBox.getDescription(),
                    ""
            ])
        }
    }

    @Test
    void testExamineRedBox_WhenBothBoxesAreVisible() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()
            redBox.setVisible(true)
            blueBox.setVisible(true)

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "red box")))

            assertMessagesAreCorrect([
                    redBox.getDescription(),
                    ""
            ])
        }
    }

    @Test
    void testExamineBox_WhenBothBoxesAreVisibleAndSelectTheBlueBox() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()
            redBox.setVisible(true)
            blueBox.setVisible(true)

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "box")))

            assertMessagesAreCorrect([
                    "Examine what?",
                    "1) blue box",
                    "2) red box",
                    ""
            ])

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent("1"))

            assertMessagesAreCorrect([
                    blueBox.getDescription(),
                    ""
            ])
        }
    }

    @Test
    void testExamineBox_WhenBothBoxesAreVisibleAndSelectAnOptionThatDoesNotExist() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()
            redBox.setVisible(true)
            blueBox.setVisible(true)

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "box")))

            assertMessagesAreCorrect([
                    "Examine what?",
                    "1) blue box",
                    "2) red box",
                    ""
            ])

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent("3"))

            assertMessagesAreCorrect([
                    "I'm sorry, I don't understand",
                    ""
            ])
        }
    }

    @Test
    void testExamineBox_WhenBothBoxesAreVisibleAndSelectAnInvalidOption() {
        for (String verbString : this.verbs.get("EXAMINE {noun}").getSynonyms()) {
            setup()
            redBox.setVisible(true)
            blueBox.setVisible(true)

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "box")))

            assertMessagesAreCorrect([
                    "Examine what?",
                    "1) blue box",
                    "2) red box",
                    ""
            ])

            this.mainWindow.clearMessages()
            mainWindow.fireCommand(new CommandEvent("A"))

            assertMessagesAreCorrect([
                    "I'm sorry, I don't understand",
                    ""
            ])
        }
    }

    @Test
    void testGetBox_WhenRedBoxIsVisible() {
        for (String verbString : this.verbs.get("GET {noun}").getSynonyms()) {
            setup()

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "box")))
            assertFalse(livingRoom.contains(redBox))
            assertTrue(player.contains(redBox))
        }
    }

    @Test
    void testGetBox_WhenBlueBoxIsVisible() {
        for (String verbString : this.verbs.get("GET {noun}").getSynonyms()) {
            setup()
            redBox.setVisible(false)
            blueBox.setVisible(true)

            mainWindow.fireCommand(new CommandEvent(verbString.replaceAll("\\{noun}", "box")))
            assertFalse(livingRoom.contains(blueBox))
            assertTrue(player.contains(blueBox))
        }
    }

}
