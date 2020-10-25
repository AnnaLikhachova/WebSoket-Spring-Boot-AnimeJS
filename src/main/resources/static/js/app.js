var stompClient = null;
var username     = '';
var sendTo       = 'everyone';
var participants = [];
var messages     = [];

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();

    }
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        username = frame.headers['user-name'];
        setConnected(true);
      console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showMessage(JSON.parse(greeting.body).content);
        });
        stompClient.subscribe('/topic/participants', function(message) {
            showParticipants(JSON.parse(greeting.body).content);
            participants = JSON.parse(message.body);
        });

        stompClient.subscribe("/topic/chat.login", function(message) {
            participants.unshift({username: JSON.parse(message.body).username, typing : false});
        });

        stompClient.subscribe("/topic/chat.logout", function(message) {
            var username = JSON.parse(message.body).username;
            for(var index in participants) {
                if(participants[index].username == username) {
                    participants.splice(index, 1);
                }
            }
        });

        stompClient.subscribe('/topic/messages', function(greeting) {
            showMessage(JSON.parse(greeting.body).username + ": "+JSON.parse(greeting.body).message);
            messages.unshift(JSON.parse(greeting.body));
        });

        stompClient.subscribe("/app/chat.participants", function(message) {
            participants = JSON.parse(message.body);
            //participants.push(participant);
            showParticipants(participants);
        });

        stompClient.subscribe("/user/queue/reply", function(message) {
            var parsed = JSON.parse(message.body);
            parsed.priv = true;
            showPrivateMessage(JSON.parse(message.body).username + ": "+JSON.parse(message.body).message);
            messages.unshift(parsed);
        });

        stompClient.subscribe("/user/queue/reply/errors", function(message) {
            toastr.error('Error', "You have been banned", message.body);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function sendMessage() {
    var destination = "/app/chat.message";
    if( sendTo != "everyone") {
        destination = "/app/chat/private/" +  sendTo;
        $('#send-to-name').empty();
        $('#send-to-name').append(sendTo);
    }

    stompClient.send(destination, {}, JSON.stringify({'message': $("#newMessage").val()}));
}


function privateSending(username) {
    sendTo = (username != sendTo) ? username : 'everyone';
    $('#send-to-name').empty();
    $('#send-to-name').append(sendTo);
}


function showMessage(message) {
    $("#greetings").append("<tr><td><span class='chat-message'></span>" + message + "</td></tr>");
}

function showPrivateMessage(message) {
    $("#private").append("<tr><td><span class='private-message'>[private] </span>" + message + "</td></tr>");
}

function showParticipants(participant) {
    $("#participants-quantity").empty();
    for(var i in participant){
        $("#participants").append('<tr><td><i class="far fa-user"></i><a  id="participant" onclick="privateSending(\'' + participant[i].username + '\')">' + participant[i].username + '</a></td></tr>');
    }
    $("#participants-quantity").append(participant.length);
}

$(function () {
    connect();
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#send" ).click(function() { sendName(); });
    $( "#btnSearch" ).click(function() {  sendMessage(); });
    $( "#participant" ).click(function() {  privateSending(); });
});

