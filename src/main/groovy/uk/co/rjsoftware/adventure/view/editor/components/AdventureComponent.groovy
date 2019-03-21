package uk.co.rjsoftware.adventure.view.editor.components

import groovy.transform.TypeChecked
import javafx.scene.control.Button
import uk.co.rjsoftware.adventure.model.Adventure

@TypeChecked
class AdventureComponent extends CustomComponent {

    private static final double MIN_WIDTH = 160.0
    private static final double MIN_HEIGHT = 130.0

    private Button button = new Button()
    private Adventure adventure

    AdventureComponent(Adventure adventure) {
        this.adventure = adventure

        this.setMinSize(MIN_WIDTH, MIN_HEIGHT)

        this.button.textProperty().set(adventure.getTitle())
        this.getChildren().add(button)
    }

    String getText() {
        return adventure.getTitle()
    }

}