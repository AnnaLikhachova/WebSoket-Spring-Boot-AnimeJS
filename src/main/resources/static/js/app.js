var stompClient = null;
var username     = '';
var sendTo       = 'everyone';
var participants = [];
var messages     = [];
var privateMessages = [];
var privateMessage = $("#private");

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
        stompClient.subscribe('/topic/participants', function(message) {
            showParticipants(JSON.parse(greeting.body).content);
            participants = JSON.parse(message.body);
        });

        stompClient.subscribe("/topic/chat.login", function(message) {
            participants.unshift({username: JSON.parse(message.body).username});
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
            showParticipants(participants);
        });

        stompClient.subscribe("/user/queue/reply", function(message) {
            var parsed = JSON.parse(message.body);
            parsed.priv = true;
            showPrivateMessage(JSON.parse(message.body).username + ": "+JSON.parse(message.body).message);
            privateMessages.unshift(parsed);
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

function sendMessage() {
    var destination = "/app/chat.message";
    if( sendTo != "everyone") {
        destination = "/app/chat/private/" +  sendTo;
        $('#send-to-name').empty();
        $('#send-to-name').append(sendTo);
    }
    stompClient.send(destination, {}, JSON.stringify({'message': $("#newMessage").val()}));
    $('#newMessage').val('');
}

function privateSending(username) {
    sendTo = username;
    $('#send-to-name').empty();
    $('#send-to-name').append(sendTo);
}

function groupSending() {
    sendTo = 'everyone';
    $('#send-to-name').empty();
    $('#send-to-name').append(sendTo);
}

function showMessage(message) {
    $("#greetings").append("<tr><td><span class='chat-message'></span>" + message + "</td></tr>");
    $("#greetings").scrollTop( $("#greetings").offset().top );
}

function showPrivateMessage(message) {
    $("#private").append("<tr><td><span class='private-message'>[private] </span>" + message + "</td></tr>");
    $("#private").scrollTop( $("#private").offset().top );
}

function showParticipants(participant) {
    $("#participants-quantity").empty();
    for(var i in participant){
        $("#participants").append('<tr><td><div class="participant-scroll"><i class="far fa-user"></i><a  id="participant" onclick="privateSending(\'' + participant[i].username + '\')">' + participant[i].username + '</a></div></td></tr>');
    }
    $("#participants-quantity").append(participant.length);
}

$(function () {
    connect();
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#btnSearch" ).click(function() {  sendMessage(); });
    $( "#participant" ).click(function() {  privateSending(); });
});

