package com.jarkendar.totabs.analyzer.note_parser

//Values source : http://www.michalkaszczyszyn.com/pl/lessons/notes.html

enum class Octave(val string: String, val baseValue: Double) {
    C("C", 65.406392),
    Cis("Cis", 69.295658),
    D("D", 73.416193),
    Dis("Dis", 77.781747),
    E("E", 82.406890),
    F("F", 87.307059),
    Fis("Fis", 92.498607),
    G("G", 97.998860),
    Gis("Gis", 103.826175),
    A("A", 110.000001),
    Ais("Ais", 116.540942),
    B("B", 123.470827)
}