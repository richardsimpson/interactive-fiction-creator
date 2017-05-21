package uk.co.rjsoftware.adventure.testing

import javax.script.ScriptEngineManager

import org.scalatest.FunSuite


/**
  * Created by richardsimpson on 17/05/2017.
  * See https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/prog_guide/javascript.html
  */
class ScriptVarsNashorn extends FunSuite {

    val manager = new ScriptEngineManager
    val globalEngine = manager.getEngineByName("nashorn")

    override def withFixture(test: NoArgTest) = {
        // Shared setup (run at beginning of each test)
        try
            test()
        finally {
            // Shared cleanup (run at end of each test)
        }
    }

    ignore("run 1000 scripts") {
        val startTime:Long = System.currentTimeMillis()

        for (i <- 1 to 1000) {
            val manager = new ScriptEngineManager
            val engine = manager.getEngineByName("nashorn")

            engine.put("t", new Testing)
            engine.eval(
                "function doStuff2() { t.doStuff() }\n" +
                        "\n" +
                        "doStuff2();")
        }

        System.out.println("time taken: " + (System.currentTimeMillis() - startTime)/1000d + " seconds.")
    }

    ignore("run 1000 scripts with single engine") {
        val startTime:Long = System.currentTimeMillis()

        for (i <- 1 to 1000) {
            globalEngine.put("t", new Testing)
            globalEngine.eval(
                "function doStuff2() { t.doStuff() }\n" +
                        "\n" +
                        "doStuff2();")
        }

        System.out.println("time taken: " + (System.currentTimeMillis() - startTime)/1000d + " seconds.")
    }
}
