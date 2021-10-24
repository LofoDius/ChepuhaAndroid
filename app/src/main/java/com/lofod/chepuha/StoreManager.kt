package com.lofod.chepuha

object StoreManager {

    private var store: Store? = null

    fun getInstance(): Store {
        if (store == null) {
            store = Store()
        }
        return store!!
    }
}