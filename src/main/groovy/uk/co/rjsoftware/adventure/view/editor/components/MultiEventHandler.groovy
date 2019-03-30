package uk.co.rjsoftware.adventure.view.editor.components

import javafx.event.Event
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

class MultiEventHandler<T extends Event> implements EventHandler<T> {

    private List<EventHandler<T>> handlers = new ArrayList<>()
    private EventHandler<T> baseHandler

    void addBaseHandler(EventHandler<? super Event> eventHandler) {
        this.baseHandler = eventHandler

        if (eventHandler != null) {
            handlers.add(eventHandler)
        }
    }

    void addHandler(EventHandler<? super Event> eventHandler) {
        if (eventHandler != null) {
            handlers.add(eventHandler)
        }
    }

    void removeHandler(EventHandler<T> eventHandler) {
        handlers.remove(eventHandler)
    }

    EventHandler<T> getBaseHandler() {
        this.baseHandler
    }

    @Override
    void handle(T event) {
        for (EventHandler<T> handler : handlers) {
            handler.handle(event)
        }

        event.consume()
    }
}