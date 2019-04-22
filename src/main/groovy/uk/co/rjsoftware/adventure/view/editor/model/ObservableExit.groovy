package uk.co.rjsoftware.adventure.view.editor.model

import groovy.transform.TypeChecked
import javafx.beans.property.adapter.JavaBeanBooleanProperty
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder
import javafx.beans.property.adapter.JavaBeanObjectProperty
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder
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
class ObservableExit {

    private final Exit exit
    private final JavaBeanObjectProperty<Direction> direction
    private final JavaBeanObjectProperty<ObservableRoom> destination
    private final JavaBeanBooleanProperty scenery
    private final JavaBeanStringProperty prefix
    private final JavaBeanStringProperty suffix

    ObservableExit(Exit exit) {
        this.exit = exit
        this.direction = new JavaBeanObjectPropertyBuilder().bean(exit).name("direction").build();
        this.destination = new JavaBeanObjectPropertyBuilder().bean(exit).name("destination").build();
        this.scenery = new JavaBeanBooleanPropertyBuilder().bean(exit).name("scenery").build();
        this.prefix = new JavaBeanStringPropertyBuilder().bean(exit).name("prefix").build();
        this.suffix = new JavaBeanStringPropertyBuilder().bean(exit).name("suffix").build();

        // setup the observableVerbInstance's list
//        final List<ObservableVerbInstance> customVerbInstances = room.getCustomVerbs().values().stream()
//                .map { verbInstance -> new ObservableVerbInstance(verbInstance)}
//                .collect(Collectors.toList())
//        this.observableCustomVerbInstances = FXCollections.observableList(customVerbInstances)
//
//        // listen to changes in the observableList of verbs, so that we can update the original items in the adventure
//        observableCustomVerbInstances.addListener(new ListChangeListener<ObservableVerbInstance>() {
//            @Override
//            void onChanged(ListChangeListener.Change<? extends ObservableVerbInstance> c) {
//                Stream<ObservableVerbInstance> tempVerbs = observableCustomVerbInstances.stream()
//                Map<UUID, CustomVerbInstance> verbs = tempVerbs.collect(Collectors.toMap(
//                        new Function<ObservableVerbInstance, UUID>() {
//                            @Override
//                            UUID apply(ObservableVerbInstance verbInstance) {
//                                return verbInstance.getId()
//                            }
//                        },
//                        new Function<ObservableVerbInstance, CustomVerbInstance>() {
//                            @Override
//                            CustomVerbInstance apply(ObservableVerbInstance verbInstance) {
//                                return verbInstance.getVerbInstance()
//                            }
//                        }))
//
//                room.setCustomVerbs(verbs)
//            }
//        })
//
//        // setup the observableItem's list
//        final List<ObservableItem> items = room.getItems().stream()
//                .map { item -> new ObservableItem(item)}
//                .collect(Collectors.toList())
//        this.observableItems = FXCollections.observableList(items)
//
//        // fixup the parent references
//        for (ObservableItem observableItem : this.observableItems) {
//            observableItem.setParent(this)
//        }
//
//        // listen to changes in the observableList of items, so that we can update the original items in the adventure
//        observableItems.addListener(new ListChangeListener<ObservableItem>() {
//            @Override
//            void onChanged(ListChangeListener.Change<? extends ObservableItem> c) {
//                final List<Item> tempItems = observableItems.stream()
//                    .map {it.getItem()}
//                    .collect(Collectors.toList())
//                room.setItems(tempItems)
//            }
//        })

    }

    Exit getExit() {
        this.exit
    }
    
    JavaBeanObjectProperty directionProperty() {
        this.direction
    }

    JavaBeanObjectProperty<ObservableRoom> destinationProperty() {
        this.destination
    }

    JavaBeanBooleanProperty sceneryProperty() {
        this.scenery
    }

    JavaBeanStringProperty prefixProperty() {
        this.prefix
    }

    JavaBeanStringProperty suffixProperty() {
        this.suffix
    }

}

