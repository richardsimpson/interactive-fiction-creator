package uk.co.rjsoftware.adventure.controller.load

import groovy.transform.TypeChecked
import org.junit.Test
import uk.co.rjsoftware.adventure.model.*

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

@TypeChecked
class LoaderTest {

    class ClosureChecker {
        String lastMessage

        void say(String message) {
            lastMessage = message
        }
    }

    private ClosureChecker checker = new ClosureChecker()

    void verifyClosure(String expected, Closure closure) {
        checker.tap(closure)
        assertEquals(expected, checker.lastMessage)
    }

    @Test
    void testLoadAdventure() {
        final File file = new File("/Users/richardsimpson/workspace/interactive-fiction-creator-groovy/src/test/groovy/uk/co/rjsoftware/adventure/controller/load/SampleAdventure.groovy")
        final Adventure adventure = Loader.loadAdventure(file)

        assertEquals("Adventure Game", adventure.getTitle())
        assertEquals("Welcome to the Adventure!", adventure.getIntroduction())

        assertEquals(2, adventure.getRooms().size())

        assertEquals(2, adventure.getCustomVerbs().size())

        final Map<String, CustomVerb> verbMap = new HashMap<>()
        for (CustomVerb verb : adventure.getCustomVerbs()) {
            verbMap.put(verb.id, verb)
        }

        assertEquals(["WATCH {noun}", "LOOK AT {noun}", "VIEW {noun}"], verbMap.get("Watch").getSynonyms())
        assertEquals(["THROW {noun}"], verbMap.get("Throw").getSynonyms())

        final Room bedroom = adventure.getRoom("bedroom")
        final Room landing = adventure.getRoom("landing")

        assertEquals("bedroom", bedroom.getName())
        assertEquals("custom description", bedroom.getDescription())
        verifyClosure("beforeEnterRoomScript", bedroom.getBeforeEnterRoom())
        verifyClosure("afterEnterRoomScript", bedroom.getAfterEnterRoom())
        verifyClosure("afterLeaveRoomScript", bedroom.getAfterLeaveRoom())
        verifyClosure("beforeEnterRoomFirstTimeScript", bedroom.getBeforeEnterRoomFirstTime())
        verifyClosure("afterEnterRoomFirstTimeScript", bedroom.getAfterEnterRoomFirstTime())
        assertEquals(1, bedroom.getExits().size())
        assertEquals(landing, bedroom.getExit(Direction.EAST))

        assertEquals("landing", landing.getName())
        assertEquals("custom description2", landing.getDescription())
        verifyClosure("beforeEnterRoomScript2", landing.getBeforeEnterRoom())
        verifyClosure("afterEnterRoomScript2", landing.getAfterEnterRoom())
        verifyClosure("afterLeaveRoomScript2", landing.getAfterLeaveRoom())
        verifyClosure("beforeEnterRoomFirstTimeScript2", landing.getBeforeEnterRoomFirstTime())
        verifyClosure("afterEnterRoomFirstTimeScript2", landing.getAfterEnterRoomFirstTime())
        assertEquals(1, landing.getExits().size())
        assertEquals(bedroom, landing.getExit(Direction.WEST))

        assertEquals(3, bedroom.getItems().size())

        final Item lamp = bedroom.getItem("lamp")
        assertEquals(["lampshade", "lamp", "shade"], lamp.getSynonyms())
        assertEquals("description", lamp.getDescription())
        assertEquals(true, lamp.isVisible())
        assertEquals(false, lamp.isScenery())
        assertEquals(true, lamp.isGettable())
        assertEquals(true, lamp.isDroppable())
        assertEquals(false, lamp.isSwitchable())
        assertEquals("switchOnMessage", lamp.getSwitchOnMessage())
        assertEquals("switchOffMessage", lamp.getSwitchOffMessage())
        assertEquals("extraMessageWhenSwitchedOn", lamp.getExtraMessageWhenSwitchedOn())
        assertEquals("extraMessageWhenSwitchedOff", lamp.getExtraMessageWhenSwitchedOff())

        final Item tv = bedroom.getItem("TV")
        assertEquals(["television", "tv"], tv.getSynonyms())
        assertEquals("description", tv.getDescription())
        assertEquals(true, tv.isVisible())
        assertEquals(false, tv.isScenery())
        assertEquals(false, tv.isGettable())
        assertEquals(false, tv.isDroppable())
        assertEquals(true, tv.isSwitchable())
        assertEquals("switchOnMessage", tv.getSwitchOnMessage())
        assertEquals("switchOffMessage", tv.getSwitchOffMessage())
        assertEquals("extraMessageWhenSwitchedOn", tv.getExtraMessageWhenSwitchedOn())
        assertEquals("extraMessageWhenSwitchedOff", tv.getExtraMessageWhenSwitchedOff())

        assertTrue(tv.containsVerb(verbMap.get("Watch")))
        assertTrue(tv.containsVerb(verbMap.get("Throw")))

        verifyClosure("watchVerb", tv.getVerbClosure(verbMap.get("Watch")))
        verifyClosure("throwVerb", tv.getVerbClosure(verbMap.get("Throw")))

        assertEquals(bedroom, adventure.getPlayer().getParent())
    }
}
