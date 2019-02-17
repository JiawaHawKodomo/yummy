$('#restaurant-login-button').on('click', function () {
    const id = $('#restaurant-id').val();
    const password = $('#restaurant-password').val();

    $.ajax({
        type: 'post',
        url: '/restaurant/login',
        data: {id: id, password: password},
        success: function (data) {
            if (data.result === true) {
                location = '/restaurant';
            } else {
                $('#restaurant-info').html('账号或密码错误');
            }
        }
    })
});