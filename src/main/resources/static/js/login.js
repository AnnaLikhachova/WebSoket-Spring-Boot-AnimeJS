var serverContext = '/';

function validateUser (){
    var searchName = $("#username").val();
    var result = null;
    $.post(serverContext + "name-unique", searchName, function (data) {
        result=data.result;
        if (result ==="true") {
            $("#error-dialog-error-label").show().html("Name is not unique.");
        } else {
            formSubmit();
        }
    })
        .fail(function (data) {
            $("#error-info").show().append(data.msg);
        });

    if (searchName.length < 1){
        $("#error-dialog-error-label").show().html("Please enter your name.");
    } else if (searchName.length > 16){
        $("#error-dialog-error-label").show().html("Name too long.");
    }
}

function formSubmit() {
    var formData = $('form').serialize();
    $.post(serverContext + "login", formData, function (data) {
        window.location.href = serverContext + "chat.html";
    })
        .fail(function (data) {
            $("#error-info").show().append(data.error);
        });

}

$(document).ready(function () {
    $('form').submit(function (event) {
        event.preventDefault();
        validateUser();
    });
});

