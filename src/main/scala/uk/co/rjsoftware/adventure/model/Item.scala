package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 15/05/2017.
  */
class Item(private val name:String, private val description:String,
           private val switchable:Boolean, private val switchOnMessage:String=null, private val switchOffMessage:String=null) {

    // TODO: To add:
    //          Edible flag, and message when eaten.
    //          Extra object description when switched on (and off), and script to run when switch on (and off)
    //          Container...
    //          Use/Give...
    //          Player...

    private var customVerbs:Map[CustomVerb, String] = Map[CustomVerb, String]()

    private var on:Boolean = false

    def this(name:String, description:String) {
        this(name, description, false, null, null)
    }

    def getName : String = {
        this.name
    }

    def getDescription : String = {
        this.description
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
}
