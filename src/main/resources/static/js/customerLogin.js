$('#login-button').on('click', login);

function login() {
    const email = $('#customer-email-input').val();
    const password = $('#customer-password-input').val();

    $('#login-tip').text('请稍候, 登录中......');
    $.ajax({
        type: 'post',
        url: '/customer/login',
        data: {email: email, password: password},
        success: function (data) {
            if (data.result === true) {
                //密码验证成功
                location = '/customer/place';
            } else {
                alert(data.info);
            }
        }
    });
}