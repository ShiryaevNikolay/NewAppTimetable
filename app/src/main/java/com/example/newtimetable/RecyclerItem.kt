package com.example.newtimetable

class RecyclerItem(_text: String, _itemId: Int) {
    var text: String = _text
        set(value) {
            field = value
        }
        get() {
            return field
        }
    var itemId: Int = _itemId
        set(value) {
            field = value
        }
        get() {
            return field
        }
}