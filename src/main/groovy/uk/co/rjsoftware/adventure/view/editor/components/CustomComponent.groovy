package uk.co.rjsoftware.adventure.view.editor.components

import groovy.transform.TypeChecked
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Border
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import uk.co.rjsoftware.adventure.view.editor.ChangeListener

@TypeChecked
abstract class CustomComponent extends VBox {

    private List<ChangeListener> changeListeners = new ArrayList<>()

    CustomComponent() {
        super(8)
        this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)))
        this.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)))
    }

    void addChangeListener(ChangeListener listener) {
        this.changeListeners.add(listener)
    }

    protected void fireChangeEvent() {
        for (ChangeListener listener : this.changeListeners) {
            listener.changed()
        }
    }

}