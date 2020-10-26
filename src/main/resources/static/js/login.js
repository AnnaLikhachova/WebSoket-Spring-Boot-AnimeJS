var serverContext = '/';
function validateUser (event){
    event.preventDefault();
    if ($("#username").val().length < 1){
        $("#error-dialog-error-label").show().html("Please enter your name.");
        return;
    } else if ($("#username").val().length > 16){
        $("#error-dialog-error-label").show().html("Name too long.");
        return;
    }
    var formData = $('form').serialize();
    $.post(serverContext + "login", formData, function (data) {
        window.location.href = serverContext + "chat.html";
    })
        .fail(function (data) {
            $("#error-info").show().append(data.responseJSON.statusText);
        });
}

$(document).ready(function () {
    $('form').submit(function (event) {
        validateUser(event);
    });
});