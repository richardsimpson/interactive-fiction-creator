package uk.co.rjsoftware.adventure.testing.groovyscript

import groovy.transform.TypeChecked
import org.junit.Before
import org.junit.Test
import uk.co.rjsoftware.adventure.controller.AdventureController
import uk.co.rjsoftware.adventure.controller.MainWindowForTesting
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.Direction
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.model.Room
import uk.co.rjsoftware.adventure.view.CommandEvent

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import static org.junit.Assert.assertEquals

@TypeChecked
class ScriptingTest {

    private AdventureController controller
    private MainWindowForTesting mainWindow

    private final Room study = new Room("study", "This is your study.")
    private final Room livingRoom = new Room("livingRoom", "This is the living room.")

    private final Item tv = new Item("tv", ["TV", "television"], "A 28\" TV")
    private final Item chest = new Item("chest", ["chest"], "This is the chest.")
    private final Item dummy = new Item("dummy", ["dummy"], "This is the dummy item.")
    private final Item dummy2 = new Item("dummy2", ["dummy2"], "This is the second dummy item.")
    private final Item player = new Item("player")

    @Before
    void before() {
        tv.setVisible(true)
        tv.setSwitchable(true)

        chest.setContainer(true)
        dummy.setContainer(true)
        dummy.setOpenable(true)
        dummy.setCloseable(true)
        dummy2.setContainer(true)
        dummy2.setOpenable(true)
        dummy2.setCloseable(true)

        //
        // study -------- livingRoom
        //

        study.addExit(Direction.EAST, livingRoom)
        livingRoom.addExit(Direction.WEST, study)

        final Adventure adventure = new Adventure("intro")

        livingRoom.addItem(tv)
        livingRoom.addItem(chest)
        livingRoom.addItem(dummy)
        livingRoom.addItem(player)

        study.addItem(dummy2)

        adventure.addRoom(study)
        adventure.addRoom(livingRoom)

        dummy.setOpen(false)
        dummy2.setOpen(false)

        this.mainWindow = new MainWindowForTesting()
        this.controller = new AdventureController(mainWindow)
        this.controller.loadAdventure(adventure)
    }

    private void assertMessagesAreCorrect(List<String> expectedMessages) {
        final String[] messages = this.mainWindow.getMessages()
        assertEquals(expectedMessages.join(System.lineSeparator()), messages.join(System.lineSeparator()))
    }

    @Test
    void testSay() {
        dummy.setOnOpen {say('hello')}
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "hello",
                ""
        ])
    }

    @Test
    void testIsSwitchedOn() {
        dummy.setOnOpen {
            if (isSwitchedOn('tv')) {
                say('tv is switched on')
            }
            else {
                say('tv is switched off')
            }
        }

        tv.switchOn()
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "tv is switched on",
                ""
        ])

        tv.switchOff()
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "tv is switched off",
                ""
        ])
    }

    @Test
    void testIsSwitchedOff() {
        dummy.setOnOpen {
            if (isSwitchedOff('tv')) {
                say('tv is switched off')
            } else {
                say('tv is switched on')
            }
        }

        tv.switchOn()
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "tv is switched on",
                ""
        ])

        tv.switchOff()
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "tv is switched off",
                ""
        ])
    }

    @Test
    void testIsOpen() {
        dummy.setOnOpen {
            if (isOpen('chest')) {
                say('chest is open')
            } else {
                say('chest is closed')
            }
        }

        chest.setOpen(true)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "chest is open",
                ""
        ])

        chest.setOpen(false)
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "chest is closed",
                ""
        ])
    }

    @Test
    void testIsClosed() {
        dummy.setOnOpen {
            if (isClosed('chest')) {
                say('chest is closed')
            } else {
                say('chest is open')
            }
        }

        chest.setOpen(true)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "chest is open",
                ""
        ])

        chest.setOpen(false)
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "chest is closed",
                ""
        ])
    }

    @Test
    void testExecuteAfterTurns() {
        dummy.setOnOpen {
            executeAfterTurns(5) {
                say('hello from closure')
            }
        }

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("wait"))
        mainWindow.fireCommand(new CommandEvent("wait"))
        mainWindow.fireCommand(new CommandEvent("wait"))
        mainWindow.fireCommand(new CommandEvent("wait"))

        assertMessagesAreCorrect([
                "time passes...", "",
                "time passes...", "",
                "time passes...", "",
                "time passes...", ""
        ])

        mainWindow.fireCommand(new CommandEvent("wait"))

        assertMessagesAreCorrect([
                "time passes...", "",
                "time passes...", "",
                "time passes...", "",
                "time passes...", "",
                "time passes...", "hello from closure",
                ""
        ])
    }

    @Test
    void testSetVisible() {
        dummy.setOnOpen {
            setVisible('tv')
        }

        tv.setVisible(false)
        assertFalse(tv.isVisible())

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertTrue(tv.isVisible())
    }

    @Test
    void testSetInvisible() {
        dummy.setOnOpen {
            setInvisible('tv')
        }

        tv.setVisible(true)
        assertTrue(tv.isVisible())

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertFalse(tv.isVisible())
    }

    @Test
    void testPlayerInRoom() {
        dummy.setOnOpen {
            if (playerInRoom('livingRoom')) {
                say('player is in the living room')
            } else {
                say('player is not in the living room')
            }
        }
        dummy2.setOnOpen(dummy.getOnOpen())

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "player is in the living room",
                ""
        ])

        mainWindow.fireCommand(new CommandEvent("west"))
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy2"))

        assertMessagesAreCorrect([
                "You open the dummy2",
                "player is not in the living room",
                ""
        ])
    }

    @Test
    void testPlayerNotInRoom() {
        dummy.setOnOpen {
            if (playerNotInRoom('livingRoom')) {
                say('player is not in the living room')
            } else {
                say('player is in the living room')
            }
        }
        dummy2.setOnOpen(dummy.getOnOpen())

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "player is in the living room",
                ""
        ])

        mainWindow.fireCommand(new CommandEvent("west"))
        dummy.setOpen(false)
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy2"))

        assertMessagesAreCorrect([
                "You open the dummy2",
                "player is not in the living room",
                ""
        ])
    }

    @Test
    void testMovePlayerTo() {
        dummy.setOnOpen {
            movePlayerTo('study')
        }

        assertEquals(livingRoom, this.controller.getCurrentRoom())

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertEquals(study, this.controller.getCurrentRoom())

        assertMessagesAreCorrect([
                "You open the dummy",
                "This is your study.",
                "You can also see:",
                "dummy2",
                ""
        ])
    }

    @Test
    void testMoveItemTo_WhenTargetIsARoom() {
        dummy.setOnOpen {
            moveItemTo('tv', 'study')
        }

        assertEquals(livingRoom, this.controller.getCurrentRoom())

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertFalse(this.livingRoom.contains(tv))
        assertTrue(this.study.contains(tv))
        assertEquals(this.study, this.tv.getParent())
    }

    @Test
    void testMoveItemTo_WhenTargetIsAnItem() {
        dummy.setOnOpen {
            moveItemTo('tv', 'dummy2')
        }

        assertEquals(livingRoom, this.controller.getCurrentRoom())

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertFalse(this.livingRoom.contains(tv))
        assertTrue(this.dummy2.contains(tv))
        assertEquals(this.dummy2, this.tv.getParent())
    }

    @Test
    void testGetItem() {
        dummy.setOnOpen {
            say(getItem('tv').getName())
        }

        assertEquals(livingRoom, this.controller.getCurrentRoom())

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))

        assertMessagesAreCorrect([
                "You open the dummy",
                "TV",
                ""
        ])
    }

    @Test
    void testGetCurrentRoom() {
        dummy.setOnOpen {
            say(getCurrentRoom().getName())
        }
        dummy2.setOnOpen {
            say(getCurrentRoom().getName())
        }

        assertEquals(livingRoom, this.controller.getCurrentRoom())

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))
        assertMessagesAreCorrect([
                "You open the dummy",
                "livingRoom",
                ""
        ])

        mainWindow.fireCommand(new CommandEvent("west"))
        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy2"))
        assertMessagesAreCorrect([
                "You open the dummy2",
                "study",
                ""
        ])

    }

    @Test
    void testGetPlayer() {
        dummy.setOnOpen {
            say(getPlayer().getName())
        }

        this.mainWindow.clearMessages()

        mainWindow.fireCommand(new CommandEvent("open dummy"))
        assertMessagesAreCorrect([
                "You open the dummy",
                "player",
                ""
        ])
    }

}
