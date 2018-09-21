package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
class Player implements ItemContainer {
    private final Map<String, Item> items = new TreeMap()

    Map<String, Item> getItems() {
        this.items
    }

    void addItem(Item item) {
        this.items.put(item.getId().toUpperCase(), item)
    }

    Item getItem(String itemId) {
        this.items.get(itemId.toUpperCase())
    }

    void removeItem(Item item) {
        this.items.remove(item.getId().toUpperCase())
    }

    boolean contains(Item item) {
        this.items.containsKey(item.getId().toUpperCase())
    }
}
