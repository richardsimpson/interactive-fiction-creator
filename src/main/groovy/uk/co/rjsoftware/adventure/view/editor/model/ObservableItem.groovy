package uk.co.rjsoftware.adventure.view.editor.model

import groovy.transform.TypeChecked
import javafx.beans.property.adapter.JavaBeanBooleanProperty
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder
import javafx.beans.property.adapter.JavaBeanStringProperty
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import uk.co.rjsoftware.adventure.model.Item

import java.util.stream.Collectors

@TypeChecked
class ObservableItem implements ObservableDomainObject {

    private final Item item
    // General Tab
    private final JavaBeanStringProperty name
    private final JavaBeanStringProperty displayName
    private final JavaBeanBooleanProperty visible
    private final JavaBeanBooleanProperty scenery
    private final JavaBeanBooleanProperty gettable
    private final JavaBeanBooleanProperty droppable
    private final JavaBeanStringProperty description
    // Features Tab
    private final JavaBeanBooleanProperty switchable
    private final JavaBeanBooleanProperty container
    private final JavaBeanBooleanProperty edible
    // Switchable Tab
    private final JavaBeanBooleanProperty switchedOn
    private final JavaBeanStringProperty switchOnMessage
    private final JavaBeanStringProperty switchOffMessage
    private final JavaBeanStringProperty extraDescriptionWhenSwitchedOn
    private final JavaBeanStringProperty extraDescriptionWhenSwitchedOff
    // Container Tab
    private final JavaBeanBooleanProperty openable
    private final JavaBeanBooleanProperty closeable
    private final JavaBeanBooleanProperty open
//        this.contentVisibilityComboBox.setSelected(item.getContentVisibility())
    private final JavaBeanStringProperty openMessage
    private final JavaBeanStringProperty closeMessage
    private final JavaBeanStringProperty onOpenScript
    private final JavaBeanStringProperty onCloseScript
    // Edible Tab
    private final JavaBeanStringProperty eatMessage
    private final JavaBeanStringProperty onEatScript

    private final ObservableList<ObservableItem> observableItems


    ObservableItem(Item item) {
        this.item = item

        // General Tab
        name = new JavaBeanStringPropertyBuilder().bean(item).name("name").build();
        displayName = new JavaBeanStringPropertyBuilder().bean(item).name("displayName").build();
        visible = new JavaBeanBooleanPropertyBuilder().bean(item).name("visible").build();
        scenery = new JavaBeanBooleanPropertyBuilder().bean(item).name("scenery").build();
        gettable = new JavaBeanBooleanPropertyBuilder().bean(item).name("gettable").build();
        droppable = new JavaBeanBooleanPropertyBuilder().bean(item).name("droppable").build();
        description = new JavaBeanStringPropertyBuilder().bean(item).name("description").build();
        // Features Tab
        switchable = new JavaBeanBooleanPropertyBuilder().bean(item).name("switchable").build();
        container = new JavaBeanBooleanPropertyBuilder().bean(item).name("container").build();
        edible = new JavaBeanBooleanPropertyBuilder().bean(item).name("edible").build();
        // Switchable Tab
        switchedOn = new JavaBeanBooleanPropertyBuilder().bean(item).name("switchedOn").build();
        switchOnMessage = new JavaBeanStringPropertyBuilder().bean(item).name("switchOnMessage").build();
        switchOffMessage = new JavaBeanStringPropertyBuilder().bean(item).name("switchOffMessage").build();
        extraDescriptionWhenSwitchedOn = new JavaBeanStringPropertyBuilder().bean(item).name("extraDescriptionWhenSwitchedOn").build();
        extraDescriptionWhenSwitchedOff = new JavaBeanStringPropertyBuilder().bean(item).name("extraDescriptionWhenSwitchedOff").build();
        // Container Tab
        openable = new JavaBeanBooleanPropertyBuilder().bean(item).name("openable").build();
        closeable = new JavaBeanBooleanPropertyBuilder().bean(item).name("closeable").build();
        open = new JavaBeanBooleanPropertyBuilder().bean(item).name("open").build();
//        this.contentVisibilityComboBox.setSelected(item.getContentVisibility())
        openMessage = new JavaBeanStringPropertyBuilder().bean(item).name("openMessage").build();
        closeMessage = new JavaBeanStringPropertyBuilder().bean(item).name("closeMessage").build();
        onOpenScript = new JavaBeanStringPropertyBuilder().bean(item).name("onOpenScript").build();
        onCloseScript = new JavaBeanStringPropertyBuilder().bean(item).name("onCloseScript").build();
        // Edible Tab
        eatMessage = new JavaBeanStringPropertyBuilder().bean(item).name("eatMessage").build();
        onEatScript = new JavaBeanStringPropertyBuilder().bean(item).name("onEatScript").build();


        // setup the observableItem's list
        final List<ObservableItem> childItems = item.getItems().values().stream()
                .map { childItem -> new ObservableItem(childItem)}
                .collect(Collectors.toList())
        this.observableItems = FXCollections.observableList(childItems)
    }

    ObservableItem() {
        this(new Item("New Item"))
    }

    JavaBeanStringProperty nameProperty() {
        this.name
    }
    JavaBeanStringProperty displayNameProperty() {
        this.displayName
    }
    JavaBeanBooleanProperty visibleProperty() {
        this.visible
    }
    JavaBeanBooleanProperty sceneryProperty() {
        this.scenery
    }
    JavaBeanBooleanProperty gettableProperty() {
        this.gettable
    }
    JavaBeanBooleanProperty droppableProperty() {
        this.droppable
    }
    JavaBeanStringProperty descriptionProperty() {
        this.description
    }
    JavaBeanBooleanProperty switchableProperty() {
        this.switchable
    }
    JavaBeanBooleanProperty containerProperty() {
        this.container
    }
    JavaBeanBooleanProperty edibleProperty() {
        this.edible
    }
    JavaBeanBooleanProperty switchedOnProperty() {
        this.switchedOn
    }
    JavaBeanStringProperty switchOnMessageProperty() {
        this.switchOnMessage
    }
    JavaBeanStringProperty switchOffMessageProperty() {
        this.switchOffMessage
    }
    JavaBeanStringProperty extraDescriptionWhenSwitchedOnProperty() {
        this.extraDescriptionWhenSwitchedOn
    }
    JavaBeanStringProperty extraDescriptionWhenSwitchedOffProperty() {
        this.extraDescriptionWhenSwitchedOff
    }
    JavaBeanBooleanProperty openableProperty() {
        this.openable
    }
    JavaBeanBooleanProperty closeableProperty() {
        this.closeable
    }
    JavaBeanBooleanProperty openProperty() {
        this.open
    }
    JavaBeanStringProperty openMessageProperty() {
        this.openMessage
    }
    JavaBeanStringProperty closeMessageProperty() {
        this.closeMessage
    }
    JavaBeanStringProperty onOpenScriptProperty() {
        this.onOpenScript
    }
    JavaBeanStringProperty onCloseScriptProperty() {
        this.onCloseScript
    }
    JavaBeanStringProperty eatMessageProperty() {
        this.eatMessage
    }
    JavaBeanStringProperty onEatScriptProperty() {
        this.onEatScript
    }

    ObservableList<ObservableItem> getObservableItems() {
        this.observableItems
    }

    @Override
    JavaBeanStringProperty getTreeItemTextProperty() {
        nameProperty()
    }

    @Override
    ObservableList<? extends ObservableDomainObject> getObservableTreeItemChildren() {
        this.observableItems
    }
}
