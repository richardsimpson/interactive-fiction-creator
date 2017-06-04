package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 15/05/2017.
  */
// TODO: Do we need this long this of constructor params since we are now creating the Items (and the Rooms) via the DSL
class Item(private var synonyms:List[String], private var description:String,
           private var visible:Boolean = true, private var scenery:Boolean = false,
           private var gettable:Boolean = true, private var droppable:Boolean = true,
           private var switchable:Boolean = false, private var switchedOn:Boolean = false,
           private var switchOnMessage:String=null, private var switchOffMessage:String=null,
           private var extraMessageWhenSwitchedOn:String = null, private var extraMessageWhenSwitchedOff:String = null,
           private var container:Boolean = false, private var openable:Boolean = true, private var closeable:Boolean = true,
           private var open:Boolean = false, private var openMessage:String = null, private var closeMessage:String = null,
           private var onOpenScript:String = null, private var onCloseScript:String = null,
           private var contentsInitiallyHidden:Boolean = true) extends ItemContainer with VerbContainer
{

    // TODO: To add:
    //          Edible flag, and message when eaten.
    //          Use/Give...
    //          Player...

    private var customVerbs:Map[CustomVerb, String] = Map[CustomVerb, String]()
    private var items:Map[String, Item] = Map[String, Item]()
    private var itemPreviouslyExamined : Boolean = false

    def this(name:String, description:String) {
        this(List(name), description)
    }

    def this(name:String) {
        this(name, "")
    }

    def getName : String = {
        this.synonyms.head
    }

    def getSynonyms : List[String] = {
        this.synonyms
    }

    def addSynonym(synonym : String) : Unit = {
        this.synonyms = this.synonyms :+ synonym
    }

    def getDescription : String = this.description
    def setDescription(description:String) : Unit = this.description = description

    def isVisible : Boolean = this.visible
    def setVisible(visible:Boolean) : Unit = this.visible = visible

    def isScenery : Boolean = this.scenery
    def setScenery(scenery:Boolean) : Unit = this.scenery = scenery

    def isGettable: Boolean = this.gettable
    def setGettable(gettable:Boolean) : Unit = this.gettable = gettable

    def isDroppable : Boolean = this.droppable
    def setDroppable(droppable:Boolean) : Unit = this.droppable = droppable

    def getItemDescription : String = {
        var result : String = this.description

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

        if (this.container && this.open) {
            result += "  It contains:" + System.lineSeparator()

            if (this.items.isEmpty) {
                result += "Nothing."
            }

            for(item <- this.items.values) {
                result += item.getName + System.lineSeparator()
            }
        }

        result
    }

    def getLookDescription : String = {
        var result : String = this.getName

        if (this.container && this.open && (!this.contentsInitiallyHidden || this.itemPreviouslyExamined)) {
            result += ", containing:" + System.lineSeparator()

            if (this.items.isEmpty) {
                result += "Nothing."
            }

            for(item <- this.items.values) {
                result += "    " + item.getName + System.lineSeparator()
            }
        }

        result
    }

    def getVerbs : Map[CustomVerb, String] = {
        this.customVerbs
    }

    def addVerb(verb:CustomVerb, script:String) : Unit = {
        this.customVerbs += (verb -> script)
    }

    //
    // Switchable
    //

    def isSwitchable : Boolean = this.switchable
    def setSwitchable(switchable:Boolean) : Unit = this.switchable = switchable

    def isSwitchedOn : Boolean = this.switchedOn
    def isSwitchedOff: Boolean = !this.switchedOn

    def switchOn() : Unit = this.switchedOn = true
    def switchOff() : Unit = this.switchedOn = false

    def getSwitchOnMessage : String = this.switchOnMessage
    def setSwitchOnMessage(message:String) : Unit = this.switchOnMessage = message

    def getSwitchOffMessage : String = this.switchOffMessage
    def setSwitchOffMessage(message:String) : Unit = this.switchOffMessage = message

    def getExtraMessageWhenSwitchedOn : String = this.extraMessageWhenSwitchedOn
    def setExtraMessageWhenSwitchedOn(message:String) : Unit = this.extraMessageWhenSwitchedOn = message

    def getExtraMessageWhenSwitchedOff : String = this.extraMessageWhenSwitchedOff
    def setExtraMessageWhenSwitchedOff(message:String) : Unit = this.extraMessageWhenSwitchedOff = message

    //
    // Container
    //

    def isContainer:Boolean = this.container
    def setContainer(container:Boolean) : Unit = this.container = container

    def isOpenable:Boolean = this.openable
    def setOpenable(openable:Boolean) : Unit = this.openable = openable

    def isCloseable:Boolean = this.closeable
    def setCloseable(closeable:Boolean) : Unit = this.closeable = closeable

    def isOpen:Boolean = this.open
    def setOpen(open:Boolean) : Unit = this.open = open

    def getOpenMessage:String = this.openMessage
    def setOpenMessage(openMessage:String) : Unit = this.openMessage = openMessage

    def getCloseMessage:String = this.closeMessage
    def setCloseMessage(closeMessage:String) : Unit = this.closeMessage = closeMessage

    def getOnOpenScript:String = this.onOpenScript
    def setOnOpenScript(onOpenScript:String) : Unit = this.onOpenScript = onOpenScript

    def getOnCloseScript:String = this.onCloseScript
    def setOnCloseScript(onCloseScript:String) : Unit = this.onCloseScript = onCloseScript

    def isContentsInitiallyHidden:Boolean = this.contentsInitiallyHidden
    def setContentsInitiallyHidden(contentsInitiallyHidden:Boolean) : Unit = this.contentsInitiallyHidden = contentsInitiallyHidden

    def getItems : Map[String, Item] = {
        this.items
    }

    def addItem(item:Item) : Unit = {
        this.items += (item.getName -> item)
    }

    def getItem(itemName: String): Item = {
        this.items.get(itemName).orNull
    }

    def removeItem(item:Item) : Unit = {
        this.items -= item.getName
    }

    def contains(item:Item) : Boolean = {
        this.items.contains(item.getName)
    }

    def setItemPreviouslyExamined(itemPreviouslyExamined:Boolean) : Unit = {
        this.itemPreviouslyExamined = itemPreviouslyExamined
    }
}
