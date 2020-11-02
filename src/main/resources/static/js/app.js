var stompClient = null;
var username     = '';
var sendTo       = 'everyone';
var participants = [];
var showMenu = false;

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
        stompClient.subscribe("/topic/chat.participants", function(message) {
            participants = JSON.parse(message.body);
            showParticipants(JSON.parse(message.body));
        });

        stompClient.subscribe("/topic/chat.login", function(message) {
            stompClient.send("/app/chat.participants", {});
            participants.unshift(JSON.parse(message.body));
            showParticipants(participants);
        });

        stompClient.subscribe("/topic/chat.logout", function(message) {
            var username = JSON.parse(message.body).username;
            for(var index in participants) {
                if(participants[index].username == username) {
                    participants.splice(index, 1);
                }
            }
            stompClient.send("/app/chat.participants", {});
        });

        stompClient.subscribe('/topic/messages', function(message) {
            showMessage(JSON.parse(message.body));
        });

        stompClient.subscribe("/app/chat.participants", function(message) {
            participants = JSON.parse(message.body);
            showParticipants(participants);
        });

        stompClient.subscribe("/app/chat.private.messages" + sendTo, function(message) {
            showPrivateMessage(JSON.parse(message.body).username + ": "+JSON.parse(message.body).message+" "+JSON.parse(message.body).time);
        });

        stompClient.subscribe("/user/queue/reply", function(message) {
            showPrivateMessagesToUser(JSON.parse(message.body));
        });

        stompClient.subscribe("/user/queue/notification", function(message) {
            showNotification(JSON.parse(message.body).senderName);
        });

        stompClient.subscribe("/user/queue/reply/errors", function(message) {
            toastr.error('Error', "You have been banned", message.body);
        });
    });
}

function sendMessage() {
    var destination = "/app/chat.message";
    if( sendTo != "everyone") {
        destination = "/app/chat/private/" +  sendTo;
        $('#send-to-name').empty();
        $('#send-to-name').append(sendTo);
        //    stompClient.send("/chat.private.messages/"+sendTo, {}, JSON.stringify({'message': $("#newMessage").val()}));
    }
    stompClient.send(destination, {}, JSON.stringify({'message': $("#newMessage").val()}));
    $('#newMessage').val('');
}

function showPrivateMessages(touser) {
    if( sendTo != "everyone") {
        destination = "/app/private/" +  username + "/" + touser;
        $('#send-to-name').empty();
        $('#send-to-name').append(sendTo);
        stompClient.send(destination, {});
        $('#newMessage').val('');
    }
}

function showNotification(sendername) {
   if (!$('a:contains('+sendername+')').children().is('.far')){
       $('a:contains('+sendername+')').append('<i id="envelope-'+sendername+'" class="far fa-envelope" ></i>');
   }
}

function handleNotification(participantName) {
    if ($('a:contains('+participantName+')').children().is('.far')) {
        $('#envelope-'+participantName+'').remove();
    }
}

function privateSending(username) {
    sendTo = username;
    destination = "/app/private/" +  username + "/" + sendTo;
    $('#send-to-name').empty();
    $('#send-to-name').append(sendTo);
    stompClient.send(destination, {});
    $('#newMessage').val('');
    changeToPrivateChatWindow();
    showPrivateMessages(username);
    handleNotification(username);
}

function changeToPrivateChatWindow() {
    $("#row-greetings").hide();
    $("#row-private").show();
}

function changeToGroupChatWindow() {
    $("#row-private").hide();
    $("#row-greetings").show();
}

function groupSending() {
    sendTo = 'everyone';
    $('#send-to-name').empty();
    $('#send-to-name').append(sendTo);
    changeToGroupChatWindow();
}

function showMessage(message) {
    $("#greetings").empty();
    for(var i in message){
        $("#greetings").append('<tr><td><div class="message-scroll"><div class="chat-message-body-username">'+ message[i].username +'</div><div class="chat-message-body-message">' +message[i].message+'</div><div class="chat-message-body-time">' + message[i].time + '</div></div></td></tr>');
    }
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

function showPrivateMessagesToUser(messages) {
    $("#private").empty();
    for(var i in messages){
        $("#private").append('<tr><td><div class="message-scroll"><div class="chat-message-body-username"><span class="private-message">[private] </span>'+ messages[i].username +'</div><div class="chat-message-body-message">' +messages[i].message+'</div><div class="chat-message-body-time">' + messages[i].time + '</div></div></td></tr>');
    }
}

function participantsMenu(){
    if(!showMenu){
        $("#participants-menu").empty();
        $("#participants-menu").append("HIDE");
        $("#participants").show();
        showMenu = true;
    }else {
        $("#participants-menu").empty();
        $("#participants-menu").append("SHOW");
        $("#participants").hide();
        showMenu = false;
    }
}

$(function () {
    connect();
    changeToGroupChatWindow();
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#btnSearch" ).click(function() {  sendMessage(); });
    $( "#participant" ).click(function() {  privateSending(); });
    $( "#participants-menu" ).click(function() {  participantsMenu(); });
});