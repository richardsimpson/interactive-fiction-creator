package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
class Player implements ItemContainer {
    private final Map<String, Item> items = new TreeMap()

    Map<String, Item> getItems() {
        this.items
    }

    void addItem(Item item) {
        if (!contains(item)) {
            this.items.put(item.getId().toUpperCase(), item)
            item.setParent(this)
        }
    }

    Item getItem(String itemId) {
        this.items.get(itemId.toUpperCase())
    }

    void removeItem(Item item) {
        if (contains(item)) {
            this.items.remove(item.getId().toUpperCase())
            item.setParent(null)
        }
    }

    boolean contains(Item item) {
        this.items.containsKey(item.getId().toUpperCase())
    }
}
