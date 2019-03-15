$('#management-login-button').on('click', function () {
    const id = $('#management-id').val();
    const password = $('#management-password').val();

    $.ajax({
        type: 'post',
        url: '/management/login',
        data: {id: id, password: password},
        success: function (data) {
            if (data.result) {
                location = '/management'
            } else {
                alert('登录失败');
            }
        }
    })
});