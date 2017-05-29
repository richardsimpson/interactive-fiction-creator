package uk.co.rjsoftware.adventure.controller

import org.codehaus.groovy.control.CompilerConfiguration

/**
 * Created by richardsimpson on 29/05/2017.
 */
class ScriptExecutor {

    private AdventureController adventureController

    ScriptExecutor(AdventureController adventureController) {
        this.adventureController = adventureController
    }

    public void executeScript(String scriptText) {
        def config = new CompilerConfiguration();
        config.scriptBaseClass = 'uk.co.rjsoftware.adventure.controller.AdventureScript'

        def shell = new GroovyShell(new Binding(), config)
        def script = shell.parse(scriptText)

        script.setAdventureController(this.adventureController)
        script.run()
    }


}
