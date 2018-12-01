'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/51', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({data: {"authorization": username}, type: 'JOIN'})
    )

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();

    if(messageContent && stompClient) {
        var chatMessage = {
        		data: {
        			roomId: 51,
        			speakerId: 54,
        			content: messageInput.value,
        			startTime: new Date().getTime(),
        			endTime: new Date().getTime() + 3600
        		},
            type: 'CHAT'
        };

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
	console.log(payload);
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else if (message.type === 'CHAT') {
    	messageElement.classList.add('chat-message');

    	var reporter = message.data.reporter;
    	var speaker = message.data.speaker
    	
        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(reporter.lastName[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(reporter.firstName + " " + reporter.lastName);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(reporter.firstName + " " + reporter.lastName);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    } 
//    else {
//        messageElement.classList.add('chat-message');
//
//        var avatarElement = document.createElement('i');
//        var avatarText = document.createTextNode(message.sender[0]);
//        avatarElement.appendChild(avatarText);
//        avatarElement.style['background-color'] = getAvatarColor(message.sender);
//
//        messageElement.appendChild(avatarElement);
//
//        var usernameElement = document.createElement('span');
//        var usernameText = document.createTextNode(message.sender);
//        usernameElement.appendChild(usernameText);
//        messageElement.appendChild(usernameElement);
//    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.data.startTime + " " + message.data.endTime + " " + message.data.speaker.firstName + message.data.speaker.lastName + ": " + message.data.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }

    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
