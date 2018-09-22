package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked
import uk.co.rjsoftware.adventure.controller.ScriptRuntimeDelegate

@TypeChecked
class Item implements ItemContainer, VerbContainer {
    private final String id
    private final List<String> synonyms
    private String description
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
    private boolean openable = true
    private boolean closeable = true
    private boolean open
    private String openMessage
    private String closeMessage
    private Closure onOpen
    private Closure onClose
    private ContentVisibility contentVisibility = ContentVisibility.AFTER_EXAMINE
    private boolean edible
    private String eatMessage
    private Closure onEat

    // TODO: To add:
    //          Use/Give...
    //          Player...

    private Map<String, Closure> customVerbs = new HashMap()
    private Map<String, Item> items = new TreeMap()
    private boolean itemPreviouslyExamined = false

    Item(String id, List<String> synonyms, String description) {
        this.id = id
        this.synonyms = synonyms
        this.description = description
    }

    Item(String id, String name, String description) {
        this(id, [name], description)
    }

    Item(String id, String name) {
        this(id, name, "")
    }

    Item(String id) {
        this(id, id)
    }

    String getId() {
        this.id
    }

    String getName() {
        this.synonyms.get(0)
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

    String getDescription() {
        this.description
    }

    void setDescription(String description) {
        this.description = description
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
            return this.itemPreviouslyExamined
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

            if (!result.endsWith(".")) {
                result += '.'
            }
        }

        if (shouldShowContents()) {
            result += "  It contains:" + System.lineSeparator()

            if (this.items.isEmpty()) {
                result += "Nothing."
            }

            for (Item item : this.items.values()) {
                result += item.getName() + System.lineSeparator()
            }
        }

        result
    }

    String getLookDescription() {
        String result = this.getName()

        if (shouldShowContents()) {
            result += ", containing:" + System.lineSeparator()

            if (this.items.isEmpty()) {
                result += "Nothing."
            }

            for (Item item : this.items.values()) {
                result += "    " + item.getName() + System.lineSeparator()
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

    void setItemPreviouslyExamined(boolean itemPreviouslyExamined) {
        this.itemPreviouslyExamined = itemPreviouslyExamined
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
