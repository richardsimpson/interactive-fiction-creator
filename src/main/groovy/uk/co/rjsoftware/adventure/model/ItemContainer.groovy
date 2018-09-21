package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
interface ItemContainer {

    Map<String, Item> getItems()

    void addItem(Item item)

    Item getItem(String itemName)

    void removeItem(Item item)

    boolean contains(Item item)
}
