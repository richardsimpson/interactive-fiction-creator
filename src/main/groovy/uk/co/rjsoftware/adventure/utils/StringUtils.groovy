package uk.co.rjsoftware.adventure.utils

import groovy.transform.TypeChecked

@TypeChecked
class StringUtils {

    static String sanitiseString(String str) {
        str.stripIndent().trim()
    }

    // TODO: Use this everywhere you check if a string is null or ""
    static Boolean isEmpty(String str) {
        (str == null) || (str.isEmpty())
    }

    // TODO: Use this everywhere you check if a string is NOT null or ""
    static Boolean hasValue(String str) {
        !isEmpty(str)
    }

}


