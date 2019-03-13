afterLoad();

function afterLoad() {
    if (getUrlParam('role') === '0') {
        //顾客登录
        $('#customer-modal').modal('show');
    } else if (getUrlParam('role') === '1') {
        //餐厅登录
        $('#restaurant-modal').modal('show');
    }
}

/**
 * @return {string}
 */
function getUrlParam(paraName) {
    var url = document.location.toString();
    var arrObj = url.split("?");

    if (arrObj.length > 1) {
        var arrPara = arrObj[1].split("&");
        var arr;

        for (var i = 0; i < arrPara.length; i++) {
            arr = arrPara[i].split("=");

            if (arr !== null && arr[0] === paraName) {
                return arr[1];
            }
        }
        return "";
    }
    else {
        return "";
    }
}

$('#customer-login-button').on('click', function () {
    const email = $('#customer-email-input').val();
    const password = $('#customer-password-input').val();

    const button = $(this);
    button.button('loading');
    $.ajax({
        type: 'post',
        url: '/customer/login',
        data: {email: email, password: password},
        success: function (data) {
            button.button('reset');
            if (data.result === true) {
                //密码验证成功
                location = '/customer/place';
            } else {
                alert(data.info);
            }
        }
    });
});

$('#restaurant-login-button').on('click', function () {
    const id = $('#restaurant-id-input').val();
    const password = $('#restaurant-password-input').val();

    const button = $(this);
    button.button('loading');
    $.ajax({
        type: 'post',
        url: '/restaurant/login',
        data: {id: id, password: password},
        success: function (data) {
            button.button('reset');
            if (data.result === true) {
                location = '/restaurant';
            } else {
                $('#restaurant-info').html('账号或密码错误');
            }
        }
    })
});