package uk.co.rjsoftware.adventure.controller

import groovy.transform.TailRecursive
import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.controller.load.Loader
import uk.co.rjsoftware.adventure.model.*
import uk.co.rjsoftware.adventure.utils.StringUtils
import uk.co.rjsoftware.adventure.view.CommandEvent
import uk.co.rjsoftware.adventure.view.EditorAppView
import uk.co.rjsoftware.adventure.view.IPlayerAppView
import uk.co.rjsoftware.adventure.view.LoadEvent

import java.util.concurrent.CopyOnWriteArrayList

import static java.util.stream.Collectors.toList

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


