package uk.co.rjsoftware.adventure.controller


import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.controller.load.Loader
import uk.co.rjsoftware.adventure.model.*
import uk.co.rjsoftware.adventure.view.editor.EditorAppView
import uk.co.rjsoftware.adventure.view.LoadEvent

@TypeChecked
class EditorController {

    private final EditorAppView view

    private Adventure adventure

    EditorController(EditorAppView view) {
        this.view = view
        view.addLoadListener(this.&loadAdventureInternal)
    }

    private void loadAdventureInternal(LoadEvent event) {
        if (event.getFile() != null) {
            final Adventure adventure = Loader.loadAdventure(event.getFile())
            loadAdventure(adventure)
        }
    }

    void loadAdventure(Adventure adventure) {
        this.adventure = adventure

        // initialise the view
        this.view.loadAdventure(adventure)
    }

}


