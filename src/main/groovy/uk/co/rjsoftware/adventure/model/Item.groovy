package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.controller.ScriptRuntimeDelegate

@TypeChecked
class Item implements ItemContainer, VerbContainer {
    private final int id
    private String name
    private String displayName
    private final List<String> synonyms = new ArrayList<>()
    private String description
    private Closure descriptionClosure
    private boolean visible = true
    private boolean scenery
    private boolean gettable = true
    private boolean droppable = true
    private boolean switchable
    private boolean switchedOn
    private String switchOnMessage
    private String switchOffMessage
    private String extraMessageWhenSwitchedOn
    private String extraMessageWhenSwitchedOff
    private boolean container
    private boolean openable = false
    private boolean closeable = false
    private boolean open
    private String openMessage
    private String closeMessage
    private Closure onOpen
    private Closure onClose
    private ContentVisibility contentVisibility = ContentVisibility.AFTER_EXAMINE
    private boolean edible
    private String eatMessage
    private Closure onEat

    private ItemContainer parent

    private final Map<String, Closure> customVerbs = new HashMap()
    // TODO: Change items to be a list?  Is the key ever used?
    private final Map<Integer, Item> items = new TreeMap()
    private boolean itemExamined = false

    Item(int id, String name, String displayName, List<String> synonyms, String description) {
        this.id = id
        this.name = name
        this.displayName = displayName
        this.synonyms.add(displayName)
        this.synonyms.addAll(synonyms)
        this.description = description
    }

    Item(int id, String name, String displayName, String description) {
        this(id, name, displayName, [], description)
    }

    Item(int id, String name, String displayName) {
        this(id, name, displayName, "")
    }

    Item(int id, String name) {
        this(id, name, "")
    }

//    Item(String id) {
//        this(id, id)
//    }

    Item copy(ItemContainer parent) {
        final Item itemCopy = new Item(this.id, this.name, this.displayName, synonyms, description)

        itemCopy.descriptionClosure = this.descriptionClosure
        itemCopy.visible = this.visible
        itemCopy.scenery = this.scenery
        itemCopy.gettable = this.gettable
        itemCopy.droppable = this.droppable
        itemCopy.switchable = this.switchable
        itemCopy.switchedOn = this.switchedOn
        itemCopy.switchOnMessage = this.switchOnMessage
        itemCopy.switchOffMessage = this.switchOffMessage
        itemCopy.extraMessageWhenSwitchedOn = this.extraMessageWhenSwitchedOn
        itemCopy.extraMessageWhenSwitchedOff = this.extraMessageWhenSwitchedOff
        itemCopy.container = this.container
        itemCopy.openable = this.openable
        itemCopy.closeable = this.closeable
        itemCopy.open = this.open
        itemCopy.openMessage = this.openMessage
        itemCopy.closeMessage = this.closeMessage
        itemCopy.onOpen = this.onOpen
        itemCopy.onClose = this.onClose
        itemCopy.contentVisibility = this.contentVisibility
        itemCopy.edible = this.edible
        itemCopy.eatMessage = this.eatMessage
        itemCopy.onEat = this.onEat

        itemCopy.parent = parent

        itemCopy.customVerbs.putAll(this.customVerbs)
        for (Map.Entry<Integer, Item> entry : this.items) {
            itemCopy.items.put(entry.key, entry.value.copy(itemCopy))
        }
        itemCopy.itemExamined = this.itemExamined

        itemCopy
    }

    int getId() {
        this.id
    }

    String getName() {
        this.name
    }

    String getDisplayName() {
        this.displayName
    }

    List<String> getSynonyms() {
        this.synonyms
    }

    void clearSynonyms() {
        this.synonyms.clear()
    }

    void addSynonym(String synonym) {
        this.synonyms.add(synonym)
    }

    ItemContainer getParent() {
        this.parent
    }

    void setParent(ItemContainer newParent) {
        if (this.parent != newParent) {
            ItemContainer oldParent = this.parent

            if (oldParent != null) {
                oldParent.removeItem(this)
            }

            this.parent = newParent

            if (this.parent != null) {
                this.parent.addItem(this)
            }
        }
    }

    String getDescription() {
        this.description
    }

    void setDescription(String description) {
        this.description = description
    }

    Closure getDescriptionClosure() {
        this.descriptionClosure
    }

    void setDescriptionClosure(Closure closure) {
        this.descriptionClosure = closure
    }

    boolean isVisible() {
        this.visible
    }

    void setVisible(boolean visible) {
        this.visible = visible
    }

    boolean isScenery() {
        this.scenery
    }

    void setScenery(boolean scenery) {
        this.scenery = scenery
    }

    boolean isGettable() {
        this.gettable
    }

    void setGettable(boolean gettable) {
        this.gettable = gettable
    }

    boolean isDroppable() {
        this.droppable
    }

    void setDroppable(boolean droppable) {
        this.droppable = droppable
    }

    private boolean shouldShowContents() {
        if (!this.container) {
            return false
        }

        if (!this.open) {
            return false
        }

        if (this.contentVisibility == ContentVisibility.ALWAYS) {
            return true
        }

        if (this.contentVisibility == ContentVisibility.NEVER) {
            return false
        }

        if (this.contentVisibility == ContentVisibility.AFTER_EXAMINE) {
            return this.itemExamined
        }

        throw new RuntimeException("Unexpected value for ContentVisibility")
    }

    String getItemDescription() {
        String result = this.description

        if (!result.endsWith(".")) {
            result += '.'
        }

        if (this.switchable) {
            if (this.switchedOn && this.extraMessageWhenSwitchedOn != null) {
                result += "  " + this.extraMessageWhenSwitchedOn
            }
            else if (!this.switchedOn && this.extraMessageWhenSwitchedOff != null) {
                result += "  " + this.extraMessageWhenSwitchedOff
            }

            // TODO: Check for other punctuation here (:,;?!)
            if (!result.endsWith(".")) {
                result += '.'
            }
        }

        if (shouldShowContents()) {
            result += "  It contains:"

            if (this.items.isEmpty()) {
                result += System.lineSeparator() + "Nothing."
            }

            for (Item item : this.items.values()) {
                result += System.lineSeparator() + item.getDisplayName()
            }
        }

        result
    }

    String getLookDescription() {
        String result = this.getDisplayName()

        if (shouldShowContents()) {
            result += ", containing:"

            if (this.items.isEmpty()) {
                result += System.lineSeparator() + "Nothing."
            }

            for (Item item : this.items.values()) {
                result += System.lineSeparator() + "    " + item.getDisplayName()
            }
        }

        result
    }

    boolean containsVerb(CustomVerb verb) {
        this.customVerbs.containsKey(verb.id)
    }

    void addVerb(CustomVerb verb, @DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=ScriptRuntimeDelegate) Closure closure) {
        this.customVerbs.put(verb.getId(), closure)
    }

    Closure getVerbClosure(CustomVerb verb) {
        this.customVerbs.get(verb.id)
    }

    //
    // Switchable
    //

    Boolean isSwitchable() { this.switchable }
    void setSwitchable(boolean switchable) { this.switchable = switchable }

    boolean isSwitchedOn() { this.switchedOn }
    boolean isSwitchedOff() { !this.switchedOn }

    void setSwitchedOn(boolean switchedOn) {this.switchedOn = switchedOn}
    void switchOn() { this.switchedOn = true }
    void switchOff() { this.switchedOn = false }

    String getSwitchOnMessage() { this.switchOnMessage}
    void setSwitchOnMessage(String message) { this.switchOnMessage = message }

    String getSwitchOffMessage() { this.switchOffMessage }
    void setSwitchOffMessage(String message) { this.switchOffMessage = message }

    String getExtraMessageWhenSwitchedOn() { this.extraMessageWhenSwitchedOn }
    void setExtraMessageWhenSwitchedOn(String message) { this.extraMessageWhenSwitchedOn = message }

    String getExtraMessageWhenSwitchedOff() { this.extraMessageWhenSwitchedOff }
    void setExtraMessageWhenSwitchedOff(String message) { this.extraMessageWhenSwitchedOff = message }

    //
    // Container
    //

    boolean isContainer() { this.container }
    void setContainer(boolean container) { this.container = container }

    boolean isOpenable() { this.openable }
    void setOpenable(boolean openable) { this.openable = openable }

    boolean isCloseable() { this.closeable }
    void setCloseable(boolean closeable) { this.closeable = closeable }

    boolean isOpen() { this.open }
    void setOpen(boolean open) { this.open = open }

    String getOpenMessage() { this.openMessage }
    void setOpenMessage(String openMessage) { this.openMessage = openMessage }

    String getCloseMessage() { this.closeMessage }
    void setCloseMessage(String closeMessage) { this.closeMessage = closeMessage }

    Closure getOnOpen() { this.onOpen }
    void setOnOpen(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=ScriptRuntimeDelegate) Closure closure) {
        this.onOpen = closure
    }

    Closure getOnClose() { this.onClose }
    void setOnClose(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=ScriptRuntimeDelegate) Closure closure) {
        this.onClose = closure
    }

    ContentVisibility getContentVisibility() { this.contentVisibility }
    void setContentVisibility(ContentVisibility contentVisibility) { this.contentVisibility = contentVisibility }

    Map<Integer, Item> getItems() {
        this.items
    }

    Map<Integer, Item> getAllItems() {
        final Map<Integer, Item> items = new HashMap<>()
        items.putAll(this.items)

        for (Item item : this.items.values()) {
            items.putAll(item.getAllItems())
        }

        items
    }

    void addItem(Item item) {
        if (!contains(item)) {
            this.items.put(item.getId(), item)
            item.setParent(this)
        }
    }

    Item getItemByName(String itemName) {
        this.items.values().find {item ->
            item.getName().equals(itemName)
        }
    }

    void removeItem(Item item) {
        if (contains(item)) {
            this.items.remove(item.id)
            item.setParent(null)
        }
    }

    boolean contains(Item item) {
        this.items.containsKey(item.id)
    }

    void setItemExamined(boolean itemExamined) {
        this.itemExamined = itemExamined
    }

    //
    // Edible
    //

    boolean isEdible() { this.edible }
    void setEdible(boolean edible) { this.edible = edible }

    String getEatMessage() { this.eatMessage }
    void setEatMessage(String eatMessage) { this.eatMessage = eatMessage }

    Closure getOnEat() { this.onEat }
    void setOnEat(@DelegatesTo(strategy=Closure.DELEGATE_ONLY, value=ScriptRuntimeDelegate) Closure closure) {
        this.onEat = closure
    }
}
