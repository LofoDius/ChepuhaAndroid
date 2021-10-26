package com.lofod.chepuha

import com.lofod.chepuha.model.Player
import java.util.*

data class Store(
    var gameCode: String = "",
    var userName: String = "",
    var player: Player = Player("", UUID.randomUUID()),
    var isStarter: Boolean = false
)