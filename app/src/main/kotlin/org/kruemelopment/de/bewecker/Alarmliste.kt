package org.kruemelopment.de.bewecker

class Alarmliste(
    var id: String?,
    var name: String?,
    var absender: String?,
    val gruppe: String?,
    var aktiv: Int,
    var packageName: String?,
    val nachricht: String?,
    val songuri: String,
    val volume: Int,
    val increase: Int,
    val vibrate: Int,
    val nwv: String?,
    val nwn: String?,
    val wdh: Int,
    val schlummer: String?,
    val wartezeit: Int,
    val exitsleep: Int,
    var importance: Int,
    val songtitel: String?
)
