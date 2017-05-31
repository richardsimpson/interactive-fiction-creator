package uk.co.rjsoftware.adventure.controller.load

import java.io.File
import java.net.URL

import org.scalatest.FunSuite
import org.scalatest.Matchers._
import uk.co.rjsoftware.adventure.model._

import scala.io.Source

/**
  * Created by richardsimpson on 29/05/2017.
  */
class LoaderTest extends FunSuite {

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
    }

    test("load the sample DSL") {
        val file:File = new File("/Users/richardsimpson/workspace/interactive-fiction-creator/src/test/scala/uk/co/rjsoftware/adventure/controller/load/SampleAdventure.groovy")
        val adventure:Adventure = Loader.loadAdventure(file)

        adventure.getTitle should equal ("Adventure Game")
        adventure.getIntroduction should equal ("Welcome to the Adventure!")

        adventure.getRooms.size should equal (2)

        adventure.getCustomVerbs.size should equal (1)
        val watch:CustomVerb = adventure.getCustomVerbs(0)
        watch.getSynonyms should equal (List("WATCH {noun}", "LOOK AT {noun}", "VIEW {noun}"))

        val bedroom:Room = adventure.findRoom("bedroom")
        val landing:Room = adventure.findRoom("landing")

        bedroom.getName should equal ("bedroom")
        bedroom.getDescription should equal ("custom description")
        bedroom.getBeforeEnterRoomScript should equal ("beforeEnterRoomScript")
        bedroom.getAfterEnterRoomScript should equal ("afterEnterRoomScript")
        bedroom.getAfterLeaveRoomScript should equal ("afterLeaveRoomScript")
        bedroom.getBeforeEnterRoomFirstTimeScript should equal ("beforeEnterRoomFirstTimeScript")
        bedroom.getAfterEnterRoomFirstTimeScript should equal ("afterEnterRoomFirstTimeScript")
        bedroom.getExits.size should equal (1)
        bedroom.getExit(Direction.EAST).get should equal (landing)

        landing.getName should equal ("landing")
        landing.getDescription should equal ("custom description2")
        landing.getBeforeEnterRoomScript should equal ("beforeEnterRoomScript2")
        landing.getAfterEnterRoomScript should equal ("afterEnterRoomScript2")
        landing.getAfterLeaveRoomScript should equal ("afterLeaveRoomScript2")
        landing.getBeforeEnterRoomFirstTimeScript should equal ("beforeEnterRoomFirstTimeScript2")
        landing.getAfterEnterRoomFirstTimeScript should equal ("afterEnterRoomFirstTimeScript2")
        landing.getExits.size should equal (1)
        landing.getExit(Direction.WEST).get should equal (bedroom)

        bedroom.getItems.size should equal (2)

        val lamp:Item = bedroom.getItem("lamp")
        lamp.getSynonyms should equal (List("lamp", "lampshade", "shade"))
        lamp.getDescription should equal ("description")
        lamp.isVisible should equal (true)
        lamp.isScenery should equal (false)
        lamp.isGettable should equal (true)
        lamp.isDroppable should equal (true)
        lamp.isSwitchable should equal (false)
        lamp.getSwitchOnMessage should equal ("switchOnMessage")
        lamp.getSwitchOffMessage should equal ("switchOffMessage")
        lamp.getExtraMessageWhenSwitchedOn should equal ("extraMessageWhenSwitchedOn")
        lamp.getExtraMessageWhenSwitchedOff should equal ("extraMessageWhenSwitchedOff")

        val tv:Item = bedroom.getItem("TV")
        tv.getSynonyms should equal (List("TV", "television"))
        tv.getDescription should equal ("description")
        tv.isVisible should equal (true)
        tv.isScenery should equal (false)
        tv.isGettable should equal (false)
        tv.isDroppable should equal (false)
        tv.isSwitchable should equal (true)
        tv.getSwitchOnMessage should equal ("switchOnMessage")
        tv.getSwitchOffMessage should equal ("switchOffMessage")
        tv.getExtraMessageWhenSwitchedOn should equal ("extraMessageWhenSwitchedOn")
        tv.getExtraMessageWhenSwitchedOff should equal ("extraMessageWhenSwitchedOff")

        tv.getVerbs.size should equal (1)
        tv.getVerbs.head._1.getVerb should equal ("WATCH {noun}")
        tv.getVerbs.head._2 should equal ("""if (isSwitchedOn('tv')) {
                    say('You watch the TV for a while.  It's showing a Western of some kind.')
                }
                else {
                    say('You watch the TV for a while.  It's just a black screen.')
                }""")

        adventure.getStartRoom.getName should equal ("bedroom")
    }
}
