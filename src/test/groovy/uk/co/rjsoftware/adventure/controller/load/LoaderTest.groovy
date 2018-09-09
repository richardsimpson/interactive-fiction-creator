package uk.co.rjsoftware.adventure.controller.load

import org.junit.Test
import uk.co.rjsoftware.adventure.model.*

import static org.junit.Assert.assertEquals

class LoaderTest {

    @Test
    void testLoadAdventure() {
        final File file = new File("/Users/richardsimpson/workspace/interactive-fiction-creator/src/test/scala/uk/co/rjsoftware/adventure/controller/load/SampleAdventure.groovy")
        final Adventure adventure = Loader.loadAdventure(file)

        assertEquals("Adventure Game", adventure.getTitle())
        assertEquals("Welcome to the Adventure!", adventure.getIntroduction())

        assertEquals(2, adventure.getRooms().size)

        assertEquals(1, adventure.getCustomVerbs().size)
        final CustomVerb watch = adventure.getCustomVerbs().get(0)
        assertEquals(["WATCH {noun}", "LOOK AT {noun}", "VIEW {noun}"], watch.getSynonyms())

        final Room bedroom = adventure.findRoom("bedroom")
        final Room landing = adventure.findRoom("landing")

        assertEquals("bedroom", bedroom.getName())
        assertEquals("custom description", bedroom.getDescription())
        assertEquals("beforeEnterRoomScript", bedroom.getBeforeEnterRoomScript())
        assertEquals("afterEnterRoomScript", bedroom.getAfterEnterRoomScript())
        assertEquals("afterLeaveRoomScript", bedroom.getAfterLeaveRoomScript())
        assertEquals("beforeEnterRoomFirstTimeScript", bedroom.getBeforeEnterRoomFirstTimeScript())
        assertEquals("afterEnterRoomFirstTimeScript", bedroom.getAfterEnterRoomFirstTimeScript())
        assertEquals(1, bedroom.getExits().size())
        assertEquals(landing, bedroom.getExit(Direction.EAST))

        assertEquals("landing", landing.getName())
        assertEquals("custom description2", landing.getDescription())
        assertEquals("beforeEnterRoomScript2", landing.getBeforeEnterRoomScript())
        assertEquals("afterEnterRoomScript2", landing.getAfterEnterRoomScript())
        assertEquals("afterLeaveRoomScript2", landing.getAfterLeaveRoomScript())
        assertEquals("beforeEnterRoomFirstTimeScript2", landing.getBeforeEnterRoomFirstTimeScript())
        assertEquals("afterEnterRoomFirstTimeScript2", landing.getAfterEnterRoomFirstTimeScript())
        assertEquals(1, landing.getExits().size())
        assertEquals(bedroom, landing.getExit(Direction.WEST))

        assertEquals(2, bedroom.getItems().size())

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

        assertEquals(1, tv.getVerbs().size())

        for (CustomVerb customVerb : tv.getVerbs().keySet()) {
            assertEquals("WATCH {noun}", customVerb.getVerb())
        }

        final String customVerbExpectedScript = """if (isSwitchedOn('tv')) {
    say('You watch the TV for a while.  It's showing a Western of some kind.')
}
else {
    say('You watch the TV for a while.  It's just a black screen.')
}"""
        for (String script : tv.getVerbs().values()) {
            assertEquals(customVerbExpectedScript, script)
        }

        assertEquals("bedroom", adventure.getStartRoom().getName())
    }
}
