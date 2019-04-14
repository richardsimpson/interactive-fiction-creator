package uk.co.rjsoftware.adventure.view.editor.model

import javafx.beans.property.adapter.JavaBeanStringProperty

interface ObservableItemContainer {

    JavaBeanStringProperty nameProperty()

    void addItem(ObservableItem item)

    void removeItem(ObservableItem item)

}
