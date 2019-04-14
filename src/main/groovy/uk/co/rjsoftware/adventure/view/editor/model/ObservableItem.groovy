package uk.co.rjsoftware.adventure.view.editor.model

import groovy.transform.TypeChecked
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.adapter.JavaBeanBooleanProperty
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder
import javafx.beans.property.adapter.JavaBeanObjectProperty
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder
import javafx.beans.property.adapter.JavaBeanStringProperty
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import uk.co.rjsoftware.adventure.model.ContentVisibility
import uk.co.rjsoftware.adventure.model.CustomVerbInstance
import uk.co.rjsoftware.adventure.model.Item

import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream

@TypeChecked
class ObservableItem implements ObservableDomainObject {

    private final Item item
    // General Tab
    private final JavaBeanStringProperty name
    private final JavaBeanStringProperty displayName
    private final ObservableList<String> synonyms
    private final SimpleStringProperty displayedSynonyms
    private final JavaBeanBooleanProperty visible
    private final JavaBeanBooleanProperty scenery
    private final JavaBeanBooleanProperty gettable
    private final JavaBeanBooleanProperty droppable
    private final JavaBeanStringProperty description
    private final JavaBeanStringProperty descriptionScript
    private final JavaBeanBooleanProperty descriptionScriptEnabled
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
    private final JavaBeanObjectProperty<ContentVisibility> contentVisibility
    private final JavaBeanStringProperty openMessage
    private final JavaBeanStringProperty closeMessage
    private final JavaBeanStringProperty onOpenScript
    private final JavaBeanStringProperty onCloseScript
    // Edible Tab
    private final JavaBeanStringProperty eatMessage
    private final JavaBeanStringProperty onEatScript

    private final ObservableList<ObservableItem> observableItems
    private final ObservableList<ObservableVerbInstance> observableCustomVerbInstances


    ObservableItem(Item item) {
        this.item = item

        // General Tab
        name = new JavaBeanStringPropertyBuilder().bean(item).name("name").build();
        displayName = new JavaBeanStringPropertyBuilder().bean(item).name("displayName").build();
        this.synonyms = FXCollections.observableList(item.getSynonyms())
        displayedSynonyms = new SimpleStringProperty(item.getSynonyms().toListString())
        visible = new JavaBeanBooleanPropertyBuilder().bean(item).name("visible").build();
        scenery = new JavaBeanBooleanPropertyBuilder().bean(item).name("scenery").build();
        gettable = new JavaBeanBooleanPropertyBuilder().bean(item).name("gettable").build();
        droppable = new JavaBeanBooleanPropertyBuilder().bean(item).name("droppable").build();
        description = new JavaBeanStringPropertyBuilder().bean(item).name("description").build();
        descriptionScript = new JavaBeanStringPropertyBuilder().bean(item).name("descriptionScript").build();
        descriptionScriptEnabled = new JavaBeanBooleanPropertyBuilder().bean(item).name("descriptionScriptEnabled").build();
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
        contentVisibility = new JavaBeanObjectPropertyBuilder().bean(item).name("contentVisibility").build()
        openMessage = new JavaBeanStringPropertyBuilder().bean(item).name("openMessage").build();
        closeMessage = new JavaBeanStringPropertyBuilder().bean(item).name("closeMessage").build();
        onOpenScript = new JavaBeanStringPropertyBuilder().bean(item).name("onOpenScript").build();
        onCloseScript = new JavaBeanStringPropertyBuilder().bean(item).name("onCloseScript").build();
        // Edible Tab
        eatMessage = new JavaBeanStringPropertyBuilder().bean(item).name("eatMessage").build();
        onEatScript = new JavaBeanStringPropertyBuilder().bean(item).name("onEatScript").build();


        // setup the observableVerbInstance's list
        final List<ObservableVerbInstance> customVerbInstances = item.getCustomVerbs().values().stream()
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

                item.setCustomVerbs(verbs)
            }
        })

        // setup the observableItem's list
        final List<ObservableItem> childItems = item.getItems().values().stream()
                .map { childItem -> new ObservableItem(childItem)}
                .collect(Collectors.toList())
        this.observableItems = FXCollections.observableList(childItems)

        // listen to changes in the observableList of items, so that we can update the original items in the adventure
        observableItems.addListener(new ListChangeListener<ObservableItem>() {
            @Override
            void onChanged(ListChangeListener.Change<? extends ObservableItem> c) {
                final List<Item> tempItems = observableItems.stream()
                        .map {it.getItem() }
                        .collect(Collectors.toList())
                item.setItems(tempItems)
            }
        })

    }

    ObservableItem() {
        this(new Item("New Item"))
    }

    Item getItem() {
        this.item
    }

    JavaBeanStringProperty nameProperty() {
        this.name
    }

    JavaBeanStringProperty displayNameProperty() {
        this.displayName
    }

    SimpleStringProperty displayedSynonymsProperty() {
        this.displayedSynonyms
    }

    ObservableList<String> getSynonyms() {
        FXCollections.observableArrayList(this.item.getSynonyms())
    }

    void setSynonyms(ObservableList<String> synonyms) {
        if (this.synonyms != synonyms) {
            this.synonyms.setAll(synonyms)
        }
        this.displayedSynonyms.set(synonyms.toListString())
        this.item.setSynonyms(synonyms)
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

    JavaBeanStringProperty descriptionScriptProperty() {
        this.descriptionScript
    }

    JavaBeanBooleanProperty descriptionScriptEnabledProperty() {
        this.descriptionScriptEnabled
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

    JavaBeanObjectProperty<ContentVisibility> contentVisibilityProperty() {
        this.contentVisibility
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

    ObservableList<ObservableVerbInstance> getObservableCustomVerbInstances() {
        this.observableCustomVerbInstances
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
