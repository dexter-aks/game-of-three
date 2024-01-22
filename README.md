# Game of Three - Coding Challenge

Game of Three is a simple multiplayer game where two players play by exchanging numbers until they reach the number 1 after a series of steps.

## Prerequisites

To run the game, you will need:

- Java 17 or higher
- Gradle (to build and run the project)
- Docker (to run RabbitMQ as the message broker)

## Building and Running the Game

1. Start RabbitMQ using Docker Compose:
`docker-compose up -d`

This will start RabbitMQ with the management console available at `http://localhost:15672` and `rabbitmq_stomp` plugin enabled
2. Build the project using Gradle:
`./gradlew build`

3. Run the Spring Boot application:
`./gradlew bootRun`

The game will be available at `http://localhost:8080`.

Note: if you face access issue after cloning the repo and then building it, please `run chmod -R 777 . ` from parent directory

## Playing the Game
1. Open the game URL in two separate browser tabs or windows to simulate two players.
2. Each player will be assigned a random player ID displayed on the screen.
3. There are two modes `automatic` and `manual`. This can be chosen from dropdown on UI
4. Any of the two player can start the game by entering a random whole number and clicking the "Send" button.
5. `Automatic`: The second player receive the move and make the automatic adjustment on client side and then send to other player 
6. `Manual`: The second player will receive the number and must choose to add either -1, 0, or 1 to make the result divisible by 3, divide it by 3, and send it back.
7. Players take turns until one player reaches the number 1 after the division, winning the game.
8. In case only one player joined the game, error will be shown as Waiting for 2nd Player
9. In case more than two player joins the game, error will be shown as Game is full

## Tests
Unit tests have been written for various use cases of the GameController.
To run the tests, use the following command:
`./gradlew test`

## Implementation Details

Client and Server architecture for better accessibility and easiness to play the game in browser

### Server-Side

The server-side application is built using Spring Boot with Kotlin and manages player sessions, and message passing between clients. It uses RabbitMQ as a message broker to relay STOMP messages over WebSockets.

Key features of the server-side implementation include:

- `GameController` to handle WebSocket connections and STOMP message mappings.
- `SimpMessagingTemplate` for sending messages to specific users and topics.
- `ConcurrentHashMap` to track active player sessions and ensure only two players can play at a time.
- Error handling to manage scenarios such as a full game or unregistered players.

### Client-Side `Since role is for Senior Backend Engineer - wrote basic javascript code`

The client-side application is implemented in JavaScript and uses SockJS and STOMP to establish a WebSocket connection to the server. The client provides a user interface where players can enter numbers and view messages from the server. The UI is built with HTML and styled with CSS.

Key features of the client-side implementation include:

- Connection management to handle WebSocket connections and reconnections.
- Subscription to server-sent messages for game updates and error handling.
- Input validation and game logic to calculate the next number in the sequence.
- Display of player IDs and game messages to the user.

## Reference 
[Game of Three - Coding Challenge.pdf] and [GamePlay.png ]attached with source

