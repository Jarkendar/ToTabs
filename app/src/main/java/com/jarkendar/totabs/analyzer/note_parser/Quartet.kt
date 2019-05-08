package com.jarkendar.totabs.analyzer.note_parser

class Quartet<F, S, T, R>(val first: F, val second: S, var third: T, var fourth: R) {

    override fun equals(obj: Any?): Boolean {
        if (obj !is Quartet<*, *, *, *>) {
            return false
        }
        if (obj === this) {
            return true
        }
        val quartet = obj as Quartet<*, *, *, *>?
        return (first == quartet!!.first
                && second == quartet.second
                && third == quartet.third
                && fourth == quartet.fourth)
    }

    override fun hashCode(): Int {
        return ((first?.hashCode() ?: 0)
                xor (second?.hashCode() ?: 0)
                xor (third?.hashCode() ?: 0)
                xor (fourth?.hashCode() ?: 0))
    }
}
