package uk.co.rjsoftware.adventure.controller.customscripts

import org.codehaus.groovy.control.CompilerConfiguration
import uk.co.rjsoftware.adventure.controller.AdventureController

/**
 * Created by richardsimpson on 29/05/2017.
 */
// TODO: Verify there is no memory leak.  See: https://www.google.co.uk/search?q=groovyshell+memory+leak&oq=groovyshell+mem&aqs=chrome.0.0j69i57j0l2.8805j0j7&sourceid=chrome&ie=UTF-8
// https://issues.apache.org/jira/browse/GROOVY-649
// https://stackoverflow.com/questions/36407119/groovyshell-in-java8-memory-leak-duplicated-classes-src-code-load-test-pr
// https://dzone.com/articles/groovyshell-and-memory-leaks
class ScriptExecutor {

    private AdventureController adventureController

    ScriptExecutor(AdventureController adventureController) {
        this.adventureController = adventureController
    }

    public void executeScript(String scriptText) {
        def config = new CompilerConfiguration();
        config.scriptBaseClass = 'uk.co.rjsoftware.adventure.controller.customscripts.AdventureScript'

        def shell = new GroovyShell(new Binding(), config)
        def script = shell.parse(scriptText)

        script.setAdventureController(this.adventureController)
        script.run()
    }


}
