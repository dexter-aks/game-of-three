package com.dexter.gameofthree.domain

interface GameAction

data class GameMessage(val playerId: String, val message: String): GameAction

data class GameMove(val playerId: String, val number: Int?): GameAction

