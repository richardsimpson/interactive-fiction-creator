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
import uk.co.rjsoftware.adventure.model.Room

import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

@TypeChecked
class ObservableRoom implements ObservableDomainObject {

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
                Stream<ObservableVerbInstance> tempVerbs = observableCustomVerbInstances.stream()
                Map<UUID, CustomVerbInstance> verbs = tempVerbs.collect(Collectors.toMap(
                        new Function<ObservableVerbInstance, UUID>() {
                            @Override
                            UUID apply(ObservableVerbInstance verbInstance) {
                                return verbInstance.getId()
                            }
                        },
                        new Function<ObservableVerbInstance, CustomVerbInstance>() {
                            @Override
                            CustomVerbInstance apply(ObservableVerbInstance verbInstance) {
                                return verbInstance.getVerbInstance()
                            }
                        }))

                room.setCustomVerbs(verbs)
            }
        })

        // setup the observableItem's list
        final List<ObservableItem> items = room.getItems().values().stream()
                .map { item -> new ObservableItem(item)}
                .collect(Collectors.toList())
        this.observableItems = FXCollections.observableList(items)
    }

    JavaBeanStringProperty nameProperty() {
        this.name
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
}

