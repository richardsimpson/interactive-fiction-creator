package uk.co.rjsoftware.adventure.view.editor.model

import groovy.transform.TypeChecked
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.adapter.JavaBeanStringProperty
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import uk.co.rjsoftware.adventure.model.CustomVerb

@TypeChecked
class ObservableCustomVerb {
    private final JavaBeanStringProperty name
    private final JavaBeanStringProperty displayName
    private final SimpleStringProperty displayedSynonyms
    private final ObservableList<String> synonyms

    private final CustomVerb customVerb

    ObservableCustomVerb(CustomVerb customVerb) {
        this.name = new JavaBeanStringPropertyBuilder().bean(customVerb).name("name").build();
        this.displayName = new JavaBeanStringPropertyBuilder().bean(customVerb).name("displayName").build();
        this.displayedSynonyms = new SimpleStringProperty(customVerb.getSynonyms().toListString())
        this.synonyms = FXCollections.observableList(customVerb.getSynonyms())

        this.customVerb = customVerb
    }

    ObservableCustomVerb() {
        this(new CustomVerb("", "", ""))
    }

    CustomVerb getCustomVerb() {
        this.customVerb
    }

    String getName() {
        this.name.get()
    }

    JavaBeanStringProperty nameProperty() {
        this.name
    }

    void setName(String name) {
        this.name.set(name)
    }

    String getDisplayName() {
        this.displayName.get()
    }

    JavaBeanStringProperty displayNameProperty() {
        this.displayName
    }

    void setDisplayName(String displayName) {
        this.displayName.set(displayName)
    }

    SimpleStringProperty displayedSynonymsProperty() {
        this.displayedSynonyms
    }

    ObservableList<String> getSynonyms() {
        FXCollections.observableArrayList(this.customVerb.getSynonyms())
    }

    void setSynonyms(ObservableList<String> synonyms) {
        if (this.synonyms != synonyms) {
            this.synonyms.setAll(synonyms)
        }
        this.displayedSynonyms.set(synonyms.toListString())
        this.customVerb.setSynonyms(synonyms)
    }

}
