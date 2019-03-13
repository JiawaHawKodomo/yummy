$('#recharge-button').on('click', function () {
    const amount = $('#recharge-amount-input').val();
    const infoText = $('#recharge-info');
    if (isNaN(Number(amount)) || Number(amount) <= 0) {
        infoText.hide().text('请填写正确数值').fadeIn();
        return;
    }

    $.ajax({
        type: 'post',
        url: '/customer/recharge',
        data: {amount: amount},
        success: function (data) {
            if (data.result) {
                alert('成功');
                history.go(0);
            } else {
                infoText.hide().text(data.info).fadeIn();
            }
        }
    })
});