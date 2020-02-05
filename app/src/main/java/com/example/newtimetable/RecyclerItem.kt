package com.example.newtimetable

class RecyclerItem {
    var text: String? = null
    var type: String? = null
    var itemId: Int? = null

    constructor(text: String, itemId: Int) {
        this.text = text
        this.itemId = itemId
    }

    constructor(text: String, type: String, itemId: Int) {
        this.text = text
        this.type = type
        this.itemId = itemId
    }
}