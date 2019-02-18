var locationInfo = null;
const responseP = $('#response-p');

$('#submit-button').on('click', submit);

function submit() {
    const name = $('#restaurant-name').val();
    const password = $('#restaurant-password').val();
    const password2 = $('#restaurant-password2').val();
    const tel = $('#restaurant-telephone').val();
    const time = $('#business-hours').val();
    const type = $('#restaurant-type').val();
    const note = $('#note').val();
    const addressNote = $('#restaurant-address').val();

    if (password !== password2) {
        responseP.html('两次输入的密码不一样');
        return;
    }

    $.ajax({
        type: 'post',
        url: '/restaurant/register',
        data: {
            name: name,
            password: password,
            tel: tel,
            time: time,
            type: type,
            note: note,
            city: locationInfo.cityname,
            lat: locationInfo.latlng.lat,
            lng: locationInfo.latlng.lng,
            poiaddress: locationInfo.poiaddress,
            poiname: locationInfo.poiname,
            addressNote: addressNote
        },
        success: function (data) {
            console.log(data);
            if (data.result) {
                alert('注册成功');
                location = '/restaurant';
            } else {
                responseP.html(data.info);
            }
        }
    });
}

window.addEventListener('message', function (event) {
    var loc = event.data;
    if (loc && loc.module === 'locationPicker') {
        console.log('location', loc);
        locationInfo = loc;
    }
}, false);
