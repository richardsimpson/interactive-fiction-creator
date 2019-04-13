package uk.co.rjsoftware.adventure.controller.load

import groovy.transform.TypeChecked
import org.junit.Test
import uk.co.rjsoftware.adventure.model.*

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

@TypeChecked
class LoaderTest {

    @Test
    void testLoadAdventure() {
        final File file = new File("/Users/richardsimpson/workspace/interactive-fiction-creator-groovy/src/test/groovy/uk/co/rjsoftware/adventure/controller/load/SampleAdventure.groovy")
        final Adventure adventure = Loader.loadAdventure(file)

        assertEquals("Adventure Game", adventure.getTitle())
        assertEquals("Welcome to the Adventure!", adventure.getIntroduction())

        assertEquals(3, adventure.getRooms().size())

        assertEquals(2, adventure.getCustomVerbs().size())
        final CustomVerb watch = adventure.getCustomVerbs().get(0)
        assertEquals(["WATCH {noun}", "LOOK AT {noun}", "VIEW {noun}"], watch.getSynonyms())

        final Map<String, CustomVerb> verbMap = new HashMap<>()
        for (CustomVerb verb : adventure.getCustomVerbs()) {
            verbMap.put(verb.getName(), verb)
        }

        assertEquals(["WATCH {noun}", "LOOK AT {noun}", "VIEW {noun}"], verbMap.get("Watch").getSynonyms())
        assertEquals(["THROW {noun}"], verbMap.get("Throw").getSynonyms())

        final Room bedroom = adventure.getRoomByName("bedroom")
        final Room landing = adventure.getRoomByName("landing")

        assertEquals("bedroom", bedroom.getName())
        assertEquals("custom description", bedroom.getDescription())
        assertFalse(bedroom.isDescriptionScriptEnabled())
        assertEquals("beforeEnterRoomScript", bedroom.getBeforeEnterRoomScript())
        assertEquals("afterEnterRoomScript", bedroom.getAfterEnterRoomScript())
        assertEquals("afterLeaveRoomScript", bedroom.getAfterLeaveRoomScript())
        assertEquals("beforeEnterRoomFirstTimeScript", bedroom.getBeforeEnterRoomFirstTimeScript())
        assertEquals("afterEnterRoomFirstTimeScript", bedroom.getAfterEnterRoomFirstTimeScript())
        assertEquals(1, bedroom.getExits().size())
        final Exit exitToLanding = bedroom.getExit(Direction.EAST)
        assertEquals(landing, exitToLanding.getDestination())
        assertTrue(exitToLanding.isScenery())
        assertEquals("prefix", exitToLanding.getPrefix())
        assertEquals("suffix", exitToLanding.getSuffix())

        assertEquals("landing", landing.getName())
        assertEquals("custom description2", landing.getDescription())
        assertFalse(landing.isDescriptionScriptEnabled())
        assertEquals("beforeEnterRoomScript2", landing.getBeforeEnterRoomScript())
        assertEquals("afterEnterRoomScript2", landing.getAfterEnterRoomScript())
        assertEquals("afterLeaveRoomScript2", landing.getAfterLeaveRoomScript())
        assertEquals("beforeEnterRoomFirstTimeScript2", landing.getBeforeEnterRoomFirstTimeScript())
        assertEquals("afterEnterRoomFirstTimeScript2", landing.getAfterEnterRoomFirstTimeScript())
        assertEquals(1, landing.getExits().size())
        final Exit exitToBedroom = landing.getExit(Direction.WEST)
        assertEquals(bedroom, exitToBedroom.getDestination())
        assertFalse(exitToBedroom.isScenery())

        assertEquals(3, bedroom.getItems().size())

        final Item lamp = bedroom.getItemByName("lamp")
        assertEquals(["lamp", "lampshade", "shade"], lamp.getSynonyms())
        assertEquals("description", lamp.getDescription())
        assertEquals(true, lamp.isVisible())
        assertEquals(false, lamp.isScenery())
        assertEquals(true, lamp.isGettable())
        assertEquals(true, lamp.isDroppable())
        assertEquals(false, lamp.isSwitchable())
        assertEquals("switchOnMessage", lamp.getSwitchOnMessage())
        assertEquals("switchOffMessage", lamp.getSwitchOffMessage())
        assertEquals("extraDescriptionWhenSwitchedOn", lamp.getExtraDescriptionWhenSwitchedOn())
        assertEquals("extraDescriptionWhenSwitchedOff", lamp.getExtraDescriptionWhenSwitchedOff())

        final Item tv = bedroom.getItemByName("tv")
        assertEquals(["tv", "television"], tv.getSynonyms())
        assertEquals("description", tv.getDescription())
        assertEquals(true, tv.isVisible())
        assertEquals(false, tv.isScenery())
        assertEquals(false, tv.isGettable())
        assertEquals(false, tv.isDroppable())
        assertEquals(true, tv.isSwitchable())
        assertEquals("switchOnMessage", tv.getSwitchOnMessage())
        assertEquals("switchOffMessage", tv.getSwitchOffMessage())
        assertEquals("extraDescriptionWhenSwitchedOn", tv.getExtraDescriptionWhenSwitchedOn())
        assertEquals("extraDescriptionWhenSwitchedOff", tv.getExtraDescriptionWhenSwitchedOff())

        assertTrue(tv.containsVerb(verbMap.get("Watch")))
        assertTrue(tv.containsVerb(verbMap.get("Throw")))

        assertEquals("say('watchVerb')", tv.getVerbScript(verbMap.get("Watch")))
        assertEquals("say('throwVerb')", tv.getVerbScript(verbMap.get("Throw")))

        final Room roomWithDescriptionClosure = adventure.getRoomByName("roomWithDescriptionClosure")
        final Item itemWithDescriptionClosure = roomWithDescriptionClosure.getItemByName("itemWithDescriptionClosure")
        assertTrue(roomWithDescriptionClosure.isDescriptionScriptEnabled())
        assertEquals("say 'roomDescriptionClosure'", roomWithDescriptionClosure.getDescriptionScript())
        assertEquals("say 'itemDescriptionClosure'", itemWithDescriptionClosure.getDescriptionScript())

        assertEquals(bedroom, adventure.getPlayer().getParent())
    }
}
