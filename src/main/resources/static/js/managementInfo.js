const registerResponseInfo = $('#manager-register-info');

$('.restaurant-pass-button').on('click', function () {
    const id = $(this).attr('id').split('-')[2];
    tryToApprove(id, true);
});

$('.restaurant-no-pass-button').on('click', function () {
    const id = $(this).attr('id').split('-')[3];
    tryToApprove(id, false);
});

function tryToApprove(id, pass) {
    const td = $('#td-' + id);
    $.ajax({
        type: 'post',
        url: '/management/approve',
        data: {id: id, pass: pass},
        success: function (data) {
            if (data.result) {
                if (pass) {
                    td.html('已通过');
                } else {
                    td.html('未通过');
                }
            } else {
                alert(data.info);
            }
        }
    });
}

$('#manager-register-button').on('click', function () {
    const id = $('#manager-register-id').val();
    const password = $('#manager-register-password').val();
    const password2 = $('#manager-register-password2').val();

    if (password !== password2) {
        registerResponseInfo.html('两次输入的密码不一样');
        return;
    }

    $.ajax({
        type: 'post',
        url: '/management/register',
        data: {id: id, password: password},
        success: function (data) {
            if (data.result) {
                registerResponseInfo.html('注册成功');
            } else {
                registerResponseInfo.html('注册失败');
            }
        }
    });
});