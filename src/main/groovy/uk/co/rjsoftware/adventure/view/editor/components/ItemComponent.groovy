package uk.co.rjsoftware.adventure.view.editor.components

import groovy.transform.TypeChecked
import javafx.scene.control.Button
import uk.co.rjsoftware.adventure.model.Item

@TypeChecked
class ItemComponent extends CustomComponent {

    private static final double MIN_WIDTH = 160.0
    private static final double MIN_HEIGHT = 130.0

    private Button button = new Button()
    private Item item

    ItemComponent(Item item) {
        this.item = item

        this.setMinSize(MIN_WIDTH, MIN_HEIGHT)

        this.button.setText(item.getName())
        this.getChildren().add(button)
    }

    String getText() {
        return item.getName()
    }

}