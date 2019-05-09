package com.jarkendar.totabs.analyzer.note_parser

//Values source : http://www.michalkaszczyszyn.com/pl/lessons/notes.html

/**
 * Staff position represent distance from center staff line
 */
enum class Octave(val string: String, val baseValue: Double, val staffPosition: Float, val isHalfTone: Boolean) {
    C("C", 65.406392, -6.5f, false),
    Cis("Cis", 69.295658, -6.5f, true),
    D("D", 73.416193, -6.0f, false),
    Dis("Dis", 77.781747, -6.0f, true),
    E("E", 82.406890, -5.5f, false),
    F("F", 87.307059, -5.0f, false),
    Fis("Fis", 92.498607, -5.0f, true),
    G("G", 97.998860, -4.5f, false),
    Gis("Gis", 103.826175, -4.5f, true),
    A("A", 110.000001, -4.0f, false),
    Ais("Ais", 116.540942, -4.0f, true),
    B("B", 123.470827, -3.5f, false)
}