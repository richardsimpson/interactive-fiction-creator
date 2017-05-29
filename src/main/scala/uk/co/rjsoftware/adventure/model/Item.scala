package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 15/05/2017.
  */
class Item(private val synonyms:List[String], private val description:String,
           private val visible:Boolean = true, private val scenery:Boolean = false,
           private val gettable:Boolean = true, private val droppable:Boolean = true,
           private val switchable:Boolean = false, private val switchOnMessage:String=null, private val switchOffMessage:String=null,
           private val extraMessageWhenSwitchedOn:String = null, private val extraMessageWhenSwitchedOff:String = null) {

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

    def getDescription : String = {
        this.description
    }

    def isVisible : Boolean = {
        this.visible
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

    def getSwitchOnMessage : Option[String] = {
        Option.apply(this.switchOnMessage)
    }

    def getSwitchOffMessage : Option[String] = {
        Option.apply(this.switchOffMessage)
    }

    def getExtraMessageWhenSwitchedOn : String = {
        this.extraMessageWhenSwitchedOn
    }

    def getExtraMessageWhenSwitchedOff : String = {
        this.extraMessageWhenSwitchedOff
    }
}
