package uk.co.rjsoftware.adventure.view.editor.components

import groovy.transform.TypeChecked
import javafx.scene.layout.*
import javafx.scene.paint.Color
import uk.co.rjsoftware.adventure.view.editor.model.ObservableAdventure

@TypeChecked
abstract class CustomComponent extends VBox {

    CustomComponent() {
        super(8)
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)))
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)))
    }

    abstract void show(Pane pane, ObservableAdventure observableAdventure)
}