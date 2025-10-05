package xyz.qweru.geo.client.event

abstract class InputEvent {
    var button = -1
    var action = -1
}

object MouseClickEvent : InputEvent() // TODO
object KeyboardInputEvent : InputEvent()

object MouseMoveEvent {
    var x = 0.0
    var y = 0.0
}