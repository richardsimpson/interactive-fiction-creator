package uk.co.rjsoftware.adventure.model

/**
  * Created by richardsimpson on 15/05/2017.
  */
class Item(private var name:String, private var description:String) {

    // TODO: To add:
    //          Edible flag, and message when eaten.
    //          Switchable flag, and message when switch on and switch off, extra object description when switched on (and off), and script to run when switch on (and off)
    //          Container...
    //          Use/Give...
    //          Player...

    private var customVerbs:List[CustomVerb] = Nil

    //var switchable:Boolean = false
    private var on:Boolean = false

    def getName : String = {
        this.name
    }

    def setName(name:String) : Unit = {
        this.name = name
    }

    def getDescription : String = {
        this.description
    }

    def setDescription(description:String) : Unit = {
        this.description = description
    }

    def addVerb(verb:CustomVerb) : Unit = {
        this.customVerbs ::= verb
    }

    def getVerbs : List[CustomVerb] = {
        this.customVerbs
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
}
