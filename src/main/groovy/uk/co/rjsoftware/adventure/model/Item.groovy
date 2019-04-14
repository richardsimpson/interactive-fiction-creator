package uk.co.rjsoftware.adventure.model

import groovy.transform.TypeChecked

@TypeChecked
class Item implements ItemContainer, VerbContainer {
    private final UUID id
    private String name
    private String displayName
    private final List<String> synonyms = new ArrayList<>()
    private String description
    private String descriptionScript
    private boolean descriptionScriptEnabled
    private boolean visible = true
    private boolean scenery
    private boolean gettable = true
    private boolean droppable = true

    private boolean switchable
    private boolean switchedOn
    private String switchOnMessage
    private String switchOffMessage
    private String extraDescriptionWhenSwitchedOn
    private String extraDescriptionWhenSwitchedOff

    private boolean container
    private boolean openable = false
    private boolean closeable = false
    private boolean open
    private String openMessage
    private String closeMessage
    private String onOpenScript
    private String onCloseScript
    private ContentVisibility contentVisibility = ContentVisibility.AFTER_EXAMINE

    private boolean edible
    private String eatMessage
    private String onEatScript

    private ItemContainer parent

    private final Map<UUID, CustomVerbInstance> customVerbs = new HashMap<>()
    // items map: key is the UPPER CASE name, to ensure the map is ordered by the name, and to ensure that items can be found regardless of case
    private final Map<String, Item> items = new TreeMap<>()
    private boolean itemExamined = false

    Item(String name, String displayName, List<String> synonyms, String description) {

        this.id = UUID.randomUUID()

        this.name = name
        this.displayName = displayName
        this.synonyms.add(displayName)
        this.synonyms.addAll(synonyms)
        this.description = description
    }

    Item(String name, String displayName, String description) {
        this(name, displayName, [], description)
    }

    Item(String name, String displayName) {
        this(name, displayName, "")
    }

    Item(String name) {
        this(name, name)
    }

    Item copy(ItemContainer parent) {
        final Item itemCopy = new Item(this.name, this.displayName, synonyms, description)

        itemCopy.descriptionScript = this.descriptionScript
        itemCopy.descriptionScriptEnabled = this.descriptionScriptEnabled
        itemCopy.visible = this.visible
        itemCopy.scenery = this.scenery
        itemCopy.gettable = this.gettable
        itemCopy.droppable = this.droppable
        itemCopy.switchable = this.switchable
        itemCopy.switchedOn = this.switchedOn
        itemCopy.switchOnMessage = this.switchOnMessage
        itemCopy.switchOffMessage = this.switchOffMessage
        itemCopy.extraDescriptionWhenSwitchedOn = this.extraDescriptionWhenSwitchedOn
        itemCopy.extraDescriptionWhenSwitchedOff = this.extraDescriptionWhenSwitchedOff
        itemCopy.container = this.container
        itemCopy.openable = this.openable
        itemCopy.closeable = this.closeable
        itemCopy.open = this.open
        itemCopy.openMessage = this.openMessage
        itemCopy.closeMessage = this.closeMessage
        itemCopy.onOpenScript = this.onOpenScript
        itemCopy.onCloseScript = this.onCloseScript
        itemCopy.contentVisibility = this.contentVisibility
        itemCopy.edible = this.edible
        itemCopy.eatMessage = this.eatMessage
        itemCopy.onEatScript = this.onEatScript

        itemCopy.parent = parent

        itemCopy.customVerbs.putAll(this.customVerbs)
        for (Map.Entry<String, Item> entry : this.items) {
            itemCopy.items.put(entry.key, entry.value.copy(itemCopy))
        }
        itemCopy.itemExamined = this.itemExamined

        itemCopy
    }

    UUID getId() {
        this.id
    }

    String getName() {
        this.name
    }

    void setName(String name) {
        this.name = name
    }

    String getDisplayName() {
        this.displayName
    }

    void setDisplayName(String displayName) {
        this.displayName = displayName
    }

    List<String> getSynonyms() {
        this.synonyms
    }

    void setSynonyms(List<String> synonyms) {
        this.synonyms.clear()
        this.synonyms.addAll(synonyms)
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

    String getDescriptionScript() {
        this.descriptionScript
    }

    void setDescriptionScript(String script) {
        this.descriptionScript = script
    }

    boolean isDescriptionScriptEnabled() {
        this.descriptionScriptEnabled
    }

    void setDescriptionScriptEnabled(boolean scriptEnabled) {
        this.descriptionScriptEnabled = scriptEnabled
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
            if (this.switchedOn && this.extraDescriptionWhenSwitchedOn != null) {
                result += "  " + this.extraDescriptionWhenSwitchedOn
            }
            else if (!this.switchedOn && this.extraDescriptionWhenSwitchedOff != null) {
                result += "  " + this.extraDescriptionWhenSwitchedOff
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
        this.customVerbs.containsKey(verb.getId())
    }

    void addVerb(CustomVerb verb, CustomVerbInstance verbInstance) {
        this.customVerbs.put(verb.getId(), verbInstance)
    }

    String getVerbScript(CustomVerb verb) {
        this.customVerbs.get(verb.getId()).getScript()
    }

    Map<UUID, CustomVerbInstance> getCustomVerbs() {
        this.customVerbs
    }

    void setCustomVerbs(Map<UUID, CustomVerbInstance> customVerbs) {
        this.customVerbs.clear()
        this.customVerbs.putAll(customVerbs)
    }

    //
    // Switchable
    //

    boolean isSwitchable() { this.switchable }
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

    String getExtraDescriptionWhenSwitchedOn() { this.extraDescriptionWhenSwitchedOn }
    void setExtraDescriptionWhenSwitchedOn(String message) { this.extraDescriptionWhenSwitchedOn = message }

    String getExtraDescriptionWhenSwitchedOff() { this.extraDescriptionWhenSwitchedOff }
    void setExtraDescriptionWhenSwitchedOff(String message) { this.extraDescriptionWhenSwitchedOff = message }

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

    String getOnOpenScript() { this.onOpenScript }
    void setOnOpenScript(String onOpenScript) { this.onOpenScript = onOpenScript }

    String getOnCloseScript() { this.onCloseScript }
    void setOnCloseScript(String onCloseScript) { this.onCloseScript = onCloseScript }

    ContentVisibility getContentVisibility() { this.contentVisibility }
    void setContentVisibility(ContentVisibility contentVisibility) { this.contentVisibility = contentVisibility }

    Map<String, Item> getItems() {
        this.items
    }

    Map<String, Item> getAllItems() {
        final Map<String, Item> items = new HashMap<>()
        items.putAll(this.items)

        for (Item item : this.items.values()) {
            items.putAll(item.getAllItems())
        }

        items
    }

    void addItem(Item item) {
        if (!contains(item)) {
            this.items.put(item.getName().toUpperCase(), item)
            item.setParent(this)
        }
    }

    Item getItemByName(String itemName) {
        this.items.get(itemName.toUpperCase())
    }

    void removeItem(Item item) {
        if (contains(item)) {
            this.items.remove(item.getName().toUpperCase())
            item.setParent(null)
        }
    }

    boolean contains(Item item) {
        this.items.containsKey(item.getName().toUpperCase())
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

    String getOnEatScript() { this.onEatScript }
    void setOnEatScript(String onEatScript) { this.onEatScript = onEatScript }

}
