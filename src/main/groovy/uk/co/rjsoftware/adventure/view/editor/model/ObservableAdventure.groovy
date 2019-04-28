package uk.co.rjsoftware.adventure.view.editor.model

import groovy.transform.TailRecursive
import groovy.transform.TypeChecked
import javafx.beans.property.adapter.JavaBeanObjectProperty
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder
import javafx.beans.property.adapter.JavaBeanStringProperty
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import uk.co.rjsoftware.adventure.model.Adventure
import uk.co.rjsoftware.adventure.model.CustomVerb
import uk.co.rjsoftware.adventure.model.Exit
import uk.co.rjsoftware.adventure.model.Item
import uk.co.rjsoftware.adventure.model.Room

import java.util.stream.Collectors

@TypeChecked
class ObservableAdventure implements ObservableDomainObject {

    private final Adventure adventure
    private final JavaBeanStringProperty introduction
    private final JavaBeanStringProperty title
    private final JavaBeanObjectProperty<Item> player
    private final JavaBeanStringProperty waitText
    private final JavaBeanStringProperty getText
    private final ObservableList<ObservableCustomVerb> observableCustomVerbs
    private final ObservableList<ObservableRoom> observableRooms

    ObservableAdventure(Adventure adventure) {
        this.adventure = adventure
        this.introduction = new JavaBeanStringPropertyBuilder().bean(adventure).name("introduction").build();
        this.title = new JavaBeanStringPropertyBuilder().bean(adventure).name("title").build();
        this.player = new JavaBeanObjectPropertyBuilder<Item>().bean(adventure).name("player").build()
        this.waitText = new JavaBeanStringPropertyBuilder().bean(adventure).name("waitText").build();
        this.getText = new JavaBeanStringPropertyBuilder().bean(adventure).name("getText").build();

        // setup the observableCustomVerb's list
        final List<ObservableCustomVerb> customVerbs = adventure.getCustomVerbs().stream()
                .map { verb -> new ObservableCustomVerb(verb)}
                .collect(Collectors.toList())
        this.observableCustomVerbs = FXCollections.observableList(customVerbs)

        // listen to changes in the observableList of verbs, so that we can update the original items in the adventure
        this.observableCustomVerbs.addListener(new ListChangeListener<ObservableCustomVerb>() {
            @Override
            void onChanged(ListChangeListener.Change<? extends ObservableCustomVerb> c) {
                List<CustomVerb> tempVerbs = observableCustomVerbs.stream()
                        .map{verb -> verb.getCustomVerb()}
                        .collect(Collectors.toList())
                adventure.setCustomVerbs(tempVerbs)
            }
        })

        // setup the observableRoom's list
        final List<ObservableRoom> rooms = adventure.getRooms().stream()
                .map { room -> new ObservableRoom(room)}
                .collect(Collectors.toList())
        this.observableRooms = FXCollections.observableList(rooms)

        // listen to changes in the observableList of rooms, so that we can update the original items in the adventure
        this.observableRooms.addListener(new ListChangeListener<ObservableRoom>() {
            @Override
            void onChanged(ListChangeListener.Change<? extends ObservableRoom> c) {
                List<Room> tempRooms = observableRooms.stream()
                        .map{room -> room.getRoom()}
                        .collect(Collectors.toList())
                adventure.setRooms(tempRooms)
            }
        })

        // now fixup the room references (exits). (this will also fixup the entrances)
        for (ObservableRoom observableRoom : this.observableRooms) {
            for (ObservableExit observableExit : observableRoom.getObservableExits()) {
                ObservableRoom destination = getRoomByName(observableExit.getExit().getDestination().getName())
                observableExit.setObservableDestination(destination)
            }
        }
    }

    JavaBeanStringProperty introductionProperty() {
        this.introduction
    }

    JavaBeanStringProperty titleProperty() {
        this.title
    }

    JavaBeanObjectProperty<Item> playerProperty() {
        this.player
    }

    JavaBeanStringProperty waitTextProperty() {
        this.waitText
    }

    JavaBeanStringProperty getTextProperty() {
        this.getText
    }

    ObservableList<ObservableCustomVerb> getObservableCustomVerbs() {
        this.observableCustomVerbs
    }

    ObservableList<ObservableRoom> getObservableRooms() {
        this.observableRooms
    }

    private ObservableRoom getRoomByName(String roomName) {
        this.observableRooms.find {room ->
            room.getName().equals(roomName)
        }
    }

    @Override
    JavaBeanStringProperty getTreeItemTextProperty() {
        titleProperty()
    }

    @Override
    ObservableList<? extends ObservableDomainObject> getObservableTreeItemChildren() {
        this.observableRooms
    }

    List<Item> getAllItems() {
        this.adventure.getAllItems()
    }

    List<CustomVerb> getCustomVerbs() {
        this.adventure.getCustomVerbs()
    }

    void deleteRoom(ObservableRoom room) {
        this.getObservableRooms().remove(room)
    }

    void addRoom(ObservableRoom room) {
        this.getObservableRooms().add(room)
    }

    List<ObservableItemContainer> getAllItemContainers() {
        final List<ObservableItemContainer> allItems = new ArrayList<>()
        allItems.addAll(this.observableRooms)
        allItems.addAll(getAllObservableItems())
        allItems
    }

    private List<ObservableItem> getAllObservableItems() {
        final List<ObservableItem> items = new ArrayList<>()

        for (ObservableRoom room : this.observableRooms) {
            items.addAll(room.getAllObservableItems())
        }

        items
    }

}

