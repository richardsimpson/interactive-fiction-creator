package uk.co.rjsoftware.adventure.view.editor.model

import groovy.transform.TypeChecked
import javafx.beans.property.adapter.JavaBeanObjectProperty
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder
import javafx.beans.property.adapter.JavaBeanStringProperty
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder
import uk.co.rjsoftware.adventure.model.CustomVerbInstance

@TypeChecked
class ObservableVerbInstance {
    private final CustomVerbInstance verbInstance
    private final JavaBeanObjectProperty<UUID> id
    private final JavaBeanStringProperty script

    ObservableVerbInstance(CustomVerbInstance verbInstance) {
        this.verbInstance = verbInstance
        this.id = new JavaBeanObjectPropertyBuilder().bean(verbInstance).name("id").build()
        this.script = new JavaBeanStringPropertyBuilder().bean(verbInstance).name("script").build();
    }

    ObservableVerbInstance() {
        this(new CustomVerbInstance(null))
    }

    CustomVerbInstance getVerbInstance() {
        this.verbInstance
    }

    UUID getId() {
        this.id.get()
    }

    JavaBeanObjectProperty<UUID> idProperty() {
        this.id
    }

    void setId(UUID id) {
        this.id.set(id)
    }

    String getScript() {
        this.script.get()
    }

    JavaBeanStringProperty scriptProperty() {
        this.script
    }

    void setScript(String script) {
        this.script.set(script)
    }

}
