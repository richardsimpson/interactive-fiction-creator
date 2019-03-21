package uk.co.rjsoftware.adventure.view.editor.treeitems

import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.view.editor.components.CustomComponent

@TypeChecked
interface CustomTreeItem {

    CustomComponent getComponent()
}