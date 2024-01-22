let stompClient;
const names = ["alpha", "bravo", "charlie", "delta", "echo", "foxtrot", "golf", "hotel", "india", "juliet", "kilo", "lima", "mike", "november", "oscar", "papa", "quebec", "romeo", "sierra", "tango", "uniform", "victor", "whiskey", "xray", "yankee", "zulu"];
const playerId = names[Math.floor(Math.random() * names.length)] + Math.floor(Math.random() * 100);
let gameMode = "automatic"; // Default to automatic mode
function connect() {
    const socket = new SockJS('/game');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected to: ' + frame);
        stompClient.subscribe('/queue/errors', function (error) {
            const message = JSON.parse(error.body);
            displayMessage("Error: " + message.message);
        });
        stompClient.subscribe('/topic/game', function (message) {
            const move = JSON.parse(message.body);
            if (move.playerId !== playerId) {
                displayMessage("Received: " + move.number + " from " + move.playerId);
                console.log("Topic game:"+gameMode)
                playGame(move);
            }
        });
        registerPlayer();
    });
}
function registerPlayer() {
    stompClient.send("/app/register", {}, playerId);
}
function sendNumber() {
    const numberInput = document.getElementById('numberInput');
    const number = parseInt(numberInput.value);
    if (!isNaN(number)) {
        const move = {playerId: playerId, number: number};
        stompClient.send("/app/play", {}, JSON.stringify(move));
        displayMessage("Sent: " + number);
    } else {
        displayMessage("Invalid number");
    }
}
function displayMessage(message) {
    const response = document.getElementById('response');
    const p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(message));
    response.appendChild(p);
}
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('playerId').textContent = playerId;
    document.getElementById('sendButton').addEventListener('click', sendNumber);
    document.getElementById('mode').addEventListener('change', function(event) {
        gameMode = event.target.value;
    });
    connect();
});
function playGame(message) {
    if (message.number === 1) {
        displayMessage("Game over. The number is 1. Player: "+message.playerId+" won");
        return;
    }
    let adjustment;
    console.log("Play Mode: "+gameMode)
    if (gameMode === "manual") {
        adjustment = parseInt(prompt("Enter -1, 0, or 1 to adjust the number:"));
        if (isNaN(adjustment) || ![1, 0, -1].includes(adjustment)) {
            displayMessage("Invalid adjustment. Please enter -1, 0, or 1.");
            return;
        }
    } else {
        adjustment = (message.number % 3 === 0) ? 0 : (message.number % 3 === 1) ? -1 : 1;
    }
    const result = (message.number + adjustment) / 3;

    stompClient.send("/app/play", {}, JSON.stringify({playerId: playerId, number: result}));
    displayMessage("Added: "+ adjustment+ ", Sent: " + result);
    if (result === 1) {
        displayMessage("Game over. You won");
    }
}