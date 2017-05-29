package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 15/05/2017.
  */
class Item(private var synonyms:List[String], private var description:String,
           private var visible:Boolean = true, private var scenery:Boolean = false,
           private var gettable:Boolean = true, private var droppable:Boolean = true,
           private var switchable:Boolean = false, private var switchOnMessage:String=null, private var switchOffMessage:String=null,
           private var extraMessageWhenSwitchedOn:String = null, private var extraMessageWhenSwitchedOff:String = null) {

    // TODO: To add:
    //          Edible flag, and message when eaten.
    //          Container...
    //          Use/Give...
    //          Player...

    private var customVerbs:Map[CustomVerb, String] = Map[CustomVerb, String]()

    private var on:Boolean = false

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

    def getDescription : String = {
        this.description
    }

    def setDescription(description:String) : Unit = {
        this.description = description
    }

    def isVisible : Boolean = {
        this.visible
    }

    def setVisible(visible:Boolean) : Unit = {
        this.visible = visible
    }

    def setScenery(scenery:Boolean) : Unit = {
        this.scenery = scenery
    }

    def setGettable(gettable:Boolean) : Unit = {
        this.gettable = gettable
    }

    def setDroppable(droppable:Boolean) : Unit = {
        this.droppable = droppable
    }

    def setSwitchable(switchable:Boolean) : Unit = {
        this.switchable = switchable
    }

    def isScenery : Boolean = {
        this.scenery
    }

    def isGettable: Boolean = {
        this.gettable
    }

    def isDroppable : Boolean = {
        this.droppable
    }

    def getItemDescription : String = {
        var result : String = this.description

        if (!result.endsWith(".")) {
            result += '.'
        }

        if (this.on && this.extraMessageWhenSwitchedOn != null) {
            result += "  " + this.extraMessageWhenSwitchedOn
        }
        else if (!this.on && this.extraMessageWhenSwitchedOff != null) {
            result += "  " + this.extraMessageWhenSwitchedOff
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

    def isSwitchable : Boolean = {
        this.switchable
    }

    def isOn : Boolean = {
        this.on
    }

    def isOff: Boolean = {
        !this.on
    }

    def switchOn() : Unit = {
        this.on = true
    }

    def switchOff() : Unit = {
        this.on = false
    }

    def getSwitchOnMessage : String = {
        this.switchOnMessage
    }

    def setSwitchOnMessage(switchOnMessage:String) : Unit = {
        this.switchOnMessage = switchOnMessage
    }

    def getSwitchOffMessage : String = {
        this.switchOffMessage
    }

    def setSwitchOffMessage(switchOffMessage:String) : Unit = {
        this.switchOffMessage = switchOffMessage
    }

    def getExtraMessageWhenSwitchedOn : String = {
        this.extraMessageWhenSwitchedOn
    }

    def setExtraMessageWhenSwitchedOn(extraMessageWhenSwitchedOn:String) : Unit = {
        this.extraMessageWhenSwitchedOn = extraMessageWhenSwitchedOn
    }

    def getExtraMessageWhenSwitchedOff : String = {
        this.extraMessageWhenSwitchedOff
    }

    def setExtraMessageWhenSwitchedOff(extraMessageWhenSwitchedOff:String) : Unit = {
        this.extraMessageWhenSwitchedOff = extraMessageWhenSwitchedOff
    }

}
