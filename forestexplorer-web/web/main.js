var joy = new JoyStick("joyDiv");
var socketState = document.getElementById('socket-state');

var ws = new WebSocket("ws://" + window.location.host + "/ws");
ws.onopen = function(event) {
    setSocketState("WebSocket connected");
};

ws.onclose = function(event) {
    console.log(event);
    if(event.reason) {
        setSocketState("WebSocket closed (" + event.reason + ")");
    } else {
        setSocketState("WebSocket closed");
    }
};

setInterval(function() {
    if(ws.readyState == WebSocket.OPEN) {
        ws.send("joy" + joy.GetDir());
    }
}, 150);

function setSocketState(msg) {
    socketState.innerHTML = msg;
    console.log("New socket state: " + msg);
}

var headlightsEnabled = false;
var warningLightsEnabled = false;

var headlightsButton = document.getElementById("headlights");
var warningLightsButton = document.getElementById("warninglights");

headlightsButton.onclick = function() {
    headlightsEnabled = !headlightsEnabled;
    updateButton(headlightsButton, headlightsEnabled);
    ws.send("hl" + headlightsEnabled);
};

warningLightsButton.onclick = function() {
    warningLightsEnabled = !warningLightsEnabled;
    updateButton(warningLightsButton, warningLightsEnabled);
    ws.send("wl" + warningLightsEnabled);
};

function updateButton(object, enabled) {
    if(enabled) {
        object.classList.remove("button-switch-disabled");
        object.classList.add("button-switch-enabled");
    } else {
        object.classList.add("button-switch-disabled");
        object.classList.remove("button-switch-enabled");
    }
}

var brightnessSlider = document.getElementById("lightsBrightness");
var brightnessSpan = document.getElementById("brightness-val");

changeBrightness(100);
function changeBrightness(value) {
    brightness = value;
    brightnessSpan.innerHTML = brightness + "%";
}

function sendNewBrightness(value) {
    changeBrightness(value);
    ws.send("br" + value);
    console.log("New brightness sent: " + value);
}