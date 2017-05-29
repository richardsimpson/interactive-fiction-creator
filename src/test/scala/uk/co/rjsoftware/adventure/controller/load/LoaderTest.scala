package uk.co.rjsoftware.adventure.controller.load

import java.io.File
import java.net.URL

import org.scalatest.FunSuite
import org.scalatest.Matchers._
import uk.co.rjsoftware.adventure.model.{Adventure, Direction, Item, Room}

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

        adventure.getIntroduction should equal ("Welcome to the Adventure!")

        adventure.getRooms.size should equal (2)

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

        bedroom.getItems.size should equal (1)

        val lamp:Item = bedroom.getItem("lamp")
        lamp.getSynonyms should equal (List("lamp", "lampshade"))
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
    }
}
