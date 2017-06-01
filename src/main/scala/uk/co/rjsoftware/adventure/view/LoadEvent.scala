package uk.co.rjsoftware.adventure.view

import java.io.File

/**
  * Created by richardsimpson on 01/06/2017.
  */
class LoadEvent(private val file:File) {

    def getFile : File = {
        this.file
    }
}

