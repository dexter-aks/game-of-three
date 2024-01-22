package com.dexter.gameofthree.controller

import com.dexter.gameofthree.domain.GameAction
import com.dexter.gameofthree.domain.GameMessage
import com.dexter.gameofthree.domain.GameMove
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.messaging.simp.SimpMessagingTemplate

@ExtendWith(MockitoExtension::class)
class GameControllerTest {

    @Mock
    private lateinit var messagingTemplate: SimpMessagingTemplate

    @InjectMocks
    private lateinit var gameController: GameController

    @Captor
    private lateinit var messageCaptor: ArgumentCaptor<GameAction>

    @BeforeEach
    fun setUp() {}

    @Test
    fun `register two player and player1 can send move to other player`() {
        val playerId1 = "player1"
        val playerId2 = "player2"
        val move = GameMove(playerId1, 56)

        // Simulate that the player has already been registered
        gameController.register(playerId1)
        gameController.register(playerId2)

        gameController.play(move)

        verify(messagingTemplate, times(1)).convertAndSend(
            eq(GameController.GAME_QUEUE),
            messageCaptor.capture()
        )

        val capturedMessage: GameMove = messageCaptor.value as GameMove
        assertEquals(56, capturedMessage.number)
    }

    @Test
    fun `return waiting for 2nd player error when only one player is registered`() {
        val playerId = "player1"
        val move = GameMove(playerId, 56)

        gameController.register(playerId)

        gameController.play(move)

        verify(messagingTemplate, times(1)).convertAndSend(
            eq(GameController.ERROR_QUEUE),
            messageCaptor.capture()
        )

        val capturedMessage: GameMessage = messageCaptor.value as GameMessage
        assertEquals(GameController.WAITING_SECOND_PLAYER_ERROR_MESSAGE, capturedMessage.message)
    }

    @Test
    fun `game full error if 3rd player tries to registered`() {
        val playerId1 = "player1"
        val playerId2 = "player2"
        val playerId3 = "player3"

        gameController.register(playerId1)
        gameController.register(playerId2)
        gameController.register(playerId3)

        verify(messagingTemplate, times(1)).convertAndSend(
            eq(GameController.ERROR_QUEUE),
            messageCaptor.capture()
        )

        val capturedMessage: GameMessage = messageCaptor.value as GameMessage
        assertEquals(GameController.GAME_FULL_ERROR_MESSAGE + ": $playerId3", capturedMessage.message)
    }
}
