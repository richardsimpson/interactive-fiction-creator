package uk.co.rjsoftware.adventure.view.editor.model

import javafx.beans.property.adapter.JavaBeanStringProperty
import javafx.collections.ObservableList

interface ObservableDomainObject {

    JavaBeanStringProperty getTreeItemTextProperty()

    ObservableList<? extends ObservableDomainObject> getObservableTreeItemChildren()
}