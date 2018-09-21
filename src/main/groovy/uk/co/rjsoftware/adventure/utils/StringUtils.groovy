package uk.co.rjsoftware.adventure.utils

import groovy.transform.TypeChecked

@TypeChecked
class StringUtils {

    static String sanitiseString(String str) {
        str.stripIndent().trim()
    }

}


