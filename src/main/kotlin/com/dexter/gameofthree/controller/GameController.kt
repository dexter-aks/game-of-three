package com.dexter.gameofthree.controller

import com.dexter.gameofthree.domain.GameMessage
import com.dexter.gameofthree.domain.GameMove
import mu.KLogging
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.util.concurrent.ConcurrentHashMap

@Controller
class GameController (private val messagingTemplate: SimpMessagingTemplate) {

    private val playerSessions = ConcurrentHashMap.newKeySet<String>()

    @MessageMapping("/register")
    fun register(playerId: String) {
        when {
            playerSessions.size >= 2 -> {
                messagingTemplate.convertAndSend(
                    ERROR_QUEUE,
                    GameMessage(playerId, "$GAME_FULL_ERROR_MESSAGE: $playerId")
                )
                return
            }
            else -> {
                playerSessions.add(playerId)
                logger.info { "Player added: $playerId" }
            }
        }
    }

    @MessageMapping("/play")
    fun play(move: GameMove) {
        when {
            !playerSessions.contains(move.playerId) -> {
                messagingTemplate.convertAndSend(
                    ERROR_QUEUE,
                    GameMessage(move.playerId, "$GAME_FULL_ERROR_MESSAGE: ${move.playerId}")
                )
                return
            }
            playerSessions.size == 1 -> {
                messagingTemplate.convertAndSend(
                    ERROR_QUEUE,
                    GameMessage(move.playerId, WAITING_SECOND_PLAYER_ERROR_MESSAGE)
                )
                return
            }
            // Relay the move to the other player
            else -> messagingTemplate.convertAndSend(GAME_QUEUE, move)
        }
    }

    companion object : KLogging() {
        const val GAME_FULL_ERROR_MESSAGE = "The game is full for player"
        const val WAITING_SECOND_PLAYER_ERROR_MESSAGE = "Waiting for Second Player"
        const val ERROR_QUEUE = "/queue/errors"
        const val GAME_QUEUE = "/topic/game"
    }
}

