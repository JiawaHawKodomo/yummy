var locationInfo = null;

//注销账号
$('#cancellation-button').on('click', function () {
    const password = $('#cancellation-password-input').val();
    $.ajax({
        type: 'delete',
        url: '/customer',
        data: {password: password},
        success: function (data) {
            console.log(data);
            if (data.result) {
                bootbox.alert('注销成功',function () {
                    location = '/';
                });
            } else {
                $('#cancellation-info').text('失败:' + data.info);
            }
        }
    })
});

//地址删除
$('.delete-location-button').on('click', function () {
    const locationId = $(this).val();
    $.ajax({
        type: 'delete',
        url: '/customer/location',
        data: {locationId: locationId},
        success: function (data) {
            console.log(data);
            if (data.result) {
                bootbox.alert('删除成功',function () {
                    history.go(0);
                });
            } else {
                $('#delete-location-info').text('失败:' + data.info);
            }
        }
    })
});

//地址添加
$('#create-location-button').on('click', function () {
    if (locationInfo === null) {
        $('#create-location-info').text('未选择地址');
        return;
    }

    const note = $('#create-location-note').val();
    const telephone = $('#create-location-telephone').val();

    $.ajax({
        type: 'post',
        url: '/customer/location',
        data: {
            note: note,
            telephone: telephone,
            block: locationInfo.poiaddress,
            point: locationInfo.poiname,
            city: locationInfo.cityname,
            lat: locationInfo.latlng.lat,
            lng: locationInfo.latlng.lng
        },
        success: function (data) {
            console.info(data);
            if (data.result) {
                bootbox.alert('成功',function () {
                    history.go(0);
                });
            } else {
                $('#create-location-info').text('失败:' + data.info);
            }
        }
    })
});

//密码修改
$('#password-update-button').on('click', function () {
    const oldPasswordInput = $('#old-password-input');
    const newPasswordInput = $('#new-password-input');
    const newPasswordInput2 = $('#new-password-input2');
    const oldPassword = oldPasswordInput.val();
    const newPassword = newPasswordInput.val();
    const newPassword2 = newPasswordInput2.val();

    const info = $('#password-update-info');
    if (newPassword !== newPassword2) {
        info.text('两次输入的密码不一样');
        return;
    }

    $.ajax({
        type: 'put',
        url: '/customer/password',
        data: {oldPassword: oldPassword, password: newPassword},
        success: function (data) {
            console.log(data);
            if (data.result) {
                info.text('成功');
            } else {
                info.text('失败:' + data.info);
            }
            oldPasswordInput.val('');
            newPasswordInput.val('');
            newPasswordInput2.val('');
        }
    })
});

//修改信息
$('#info-update-button').on('click', function () {
    const name = $('#info-name-input').val();
    const telephone = $('#info-telephone-input').val();

    $.ajax({
        type: 'put',
        url: '/customer/info',
        data: {name: name, telephone: telephone},
        success: function (data) {
            console.log(data);
            const info = $('#info-update-info');
            if (data.result) {
                info.text('成功');
            } else {
                info.text('失败:' + data.info);
            }
        }
    })
});

window.addEventListener('message', function (event) {
    var loc = event.data;
    if (loc && loc.module === 'locationPicker') {
        console.log('location', loc);
        locationInfo = loc;
        setLocationInfo(loc)
    }
}, false);

function setLocationInfo(info) {
    const poiname = info.poiname === '我的位置' ? '' : info.poiname;
    $('#create-location-city').val(info.cityname);
    $('#create-location-block').val(info.poiaddress);
    $('#create-location-point').val(poiname);
}