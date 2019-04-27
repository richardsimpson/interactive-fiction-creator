package uk.co.rjsoftware.adventure.view.editor.model

import groovy.transform.TypeChecked
import javafx.beans.property.adapter.JavaBeanBooleanProperty
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder
import javafx.beans.property.adapter.JavaBeanStringProperty
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import uk.co.rjsoftware.adventure.model.CustomVerbInstance
import uk.co.rjsoftware.adventure.model.Direction
import uk.co.rjsoftware.adventure.model.Exit
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.model.Room

import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

@TypeChecked
class ObservableRoom implements ObservableDomainObject, ObservableItemContainer {

    private final Room room
    private final JavaBeanStringProperty name
    private final JavaBeanStringProperty description
    private final JavaBeanStringProperty descriptionScript
    private final JavaBeanBooleanProperty descriptionScriptEnabled
    private final JavaBeanStringProperty beforeEnterRoomScript
    private final JavaBeanStringProperty afterEnterRoomScript
    private final JavaBeanStringProperty afterLeaveRoomScript
    private final JavaBeanStringProperty beforeEnterRoomFirstTimeScript
    private final JavaBeanStringProperty afterEnterRoomFirstTimeScript
    private final ObservableList<ObservableVerbInstance> observableCustomVerbInstances
    private final ObservableList<ObservableItem> observableItems
    private final ObservableList<ObservableExit> observableExits
    private final ObservableList<ObservableEntrance> observableEntrances

    ObservableRoom(Room room) {
        this.room = room
        this.name = new JavaBeanStringPropertyBuilder().bean(room).name("name").build();
        this.description = new JavaBeanStringPropertyBuilder().bean(room).name("description").build();
        this.descriptionScript = new JavaBeanStringPropertyBuilder().bean(room).name("descriptionScript").build();
        this.descriptionScriptEnabled = new JavaBeanBooleanPropertyBuilder().bean(room).name("descriptionScriptEnabled").build();
        this.beforeEnterRoomScript = new JavaBeanStringPropertyBuilder().bean(room).name("beforeEnterRoomScript").build();
        this.afterEnterRoomScript = new JavaBeanStringPropertyBuilder().bean(room).name("afterEnterRoomScript").build();
        this.afterLeaveRoomScript = new JavaBeanStringPropertyBuilder().bean(room).name("afterLeaveRoomScript").build();
        this.beforeEnterRoomFirstTimeScript = new JavaBeanStringPropertyBuilder().bean(room).name("beforeEnterRoomFirstTimeScript").build();
        this.afterEnterRoomFirstTimeScript = new JavaBeanStringPropertyBuilder().bean(room).name("afterEnterRoomFirstTimeScript").build();

        // setup the observableVerbInstance's list
        final List<ObservableVerbInstance> customVerbInstances = room.getCustomVerbs().values().stream()
                .map { verbInstance -> new ObservableVerbInstance(verbInstance)}
                .collect(Collectors.toList())
        this.observableCustomVerbInstances = FXCollections.observableList(customVerbInstances)

        // listen to changes in the observableList of verbs, so that we can update the original items in the adventure
        observableCustomVerbInstances.addListener(new ListChangeListener<ObservableVerbInstance>() {
            @Override
            void onChanged(ListChangeListener.Change<? extends ObservableVerbInstance> c) {
                final Map<UUID, CustomVerbInstance> verbs = ObservableRoom.this.observableCustomVerbInstances.collectEntries {
                    verbInstance -> [verbInstance.getId(), verbInstance.getVerbInstance()]
                }

                room.setCustomVerbs(verbs)
            }
        })

        // setup the observableItem's list
        final List<ObservableItem> items = room.getItems().stream()
                .map { item -> new ObservableItem(item)}
                .collect(Collectors.toList())
        this.observableItems = FXCollections.observableList(items)

        // fixup the parent references
        for (ObservableItem observableItem : this.observableItems) {
            observableItem.setParent(this)
        }

        // listen to changes in the observableList of items, so that we can update the original items in the adventure
        observableItems.addListener(new ListChangeListener<ObservableItem>() {
            @Override
            void onChanged(ListChangeListener.Change<? extends ObservableItem> c) {
                final List<Item> tempItems = observableItems.stream()
                    .map {it.getItem()}
                    .collect(Collectors.toList())
                room.setItems(tempItems)
            }
        })

        // setup the observableExit's list
        final List<ObservableExit> currentExits = room.getExits().values().stream()
                .map { exit -> new ObservableExit(exit)}
                .collect(Collectors.toList())
        this.observableExits = FXCollections.observableList(currentExits)

        // Fixup the origins on the exits
        for (ObservableExit observableExit : currentExits) {
            observableExit.setObservableOrigin(this)
        }

        // listen to changes in the observableList of exits, so that we can update the original exits in the adventure
        observableExits.addListener(new ListChangeListener<ObservableExit>() {
            @Override
            void onChanged(ListChangeListener.Change<? extends ObservableExit> c) {
                final List<Exit> newExits = ObservableRoom.this.observableExits.collect {
                    it.getExit()
                }

                room.setExits(newExits)
            }
        })

        // setup the observableEntrances's list
        final List<ObservableEntrance> currentEntrances = room.getEntrances().stream()
                .map { entrance -> new ObservableEntrance(entrance)}
                .collect(Collectors.toList())
        this.observableEntrances = FXCollections.observableList(currentEntrances)
    }

    ObservableRoom() {
        this(new Room("New Room"))
    }

    JavaBeanStringProperty nameProperty() {
        this.name
    }

    String getName() {
        this.name.get()
    }

    JavaBeanStringProperty descriptionProperty() {
        this.description
    }

    JavaBeanStringProperty descriptionScriptProperty() {
        this.descriptionScript
    }

    JavaBeanBooleanProperty descriptionScriptEnabledProperty() {
        this.descriptionScriptEnabled
    }

    JavaBeanStringProperty beforeEnterRoomScriptProperty() {
        this.beforeEnterRoomScript
    }

    JavaBeanStringProperty afterEnterRoomScriptProperty() {
        this.afterEnterRoomScript
    }

    JavaBeanStringProperty afterLeaveRoomScriptProperty() {
        this.afterLeaveRoomScript
    }

    JavaBeanStringProperty beforeEnterRoomFirstTimeScriptProperty() {
        this.beforeEnterRoomFirstTimeScript
    }

    JavaBeanStringProperty afterEnterRoomFirstTimeScriptProperty() {
        this.afterEnterRoomFirstTimeScript
    }

    ObservableList<ObservableVerbInstance> getObservableCustomVerbInstances() {
        this.observableCustomVerbInstances
    }

    ObservableList<ObservableItem> getObservableItems() {
        this.observableItems
    }

    ObservableList<ObservableExit> getObservableExits() {
        this.observableExits
    }

    ObservableList<ObservableEntrance> getObservableEntrances() {
        this.observableEntrances
    }

    Room getRoom() {
        this.room
    }

    @Override
    JavaBeanStringProperty getTreeItemTextProperty() {
        nameProperty()
    }

    @Override
    ObservableList<? extends ObservableDomainObject> getObservableTreeItemChildren() {
        getObservableItems()
    }

    @Override
    void addItem(ObservableItem item) {
        if (!contains(item)) {
            this.observableItems.add(item)
            item.setParent(this)
        }
    }

    @Override
    void removeItem(ObservableItem item) {
        if (contains(item)) {
            this.observableItems.remove(item)
            item.setParent(null)
        }
    }

    boolean contains(ObservableItem item) {
        this.observableItems.any {it.getItem().getId().equals(item.getItem().id)}
    }

    List<ObservableItem> getAllObservableItems() {
        final List<ObservableItem> items = new ArrayList<>()
        items.addAll(this.observableItems)

        for (ObservableItem item : this.observableItems) {
            items.addAll(item.getAllObservableItems())
        }

        items
    }

    void addExit(ObservableExit exit) {
        if (!contains(exit)) {
            this.observableExits.add(exit)
        }
    }

    boolean contains(ObservableExit exit) {
        this.observableExits.any {it.getExit().getDirection().equals(exit.getExit().getDirection())}
    }


}

