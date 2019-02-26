//确认送达
$('#confirm-button').on('click', function () {
    if (confirm('确认已送达么?')) {
        $.ajax({
            type: 'put',
            url: '/restaurant/order/confirm',
            data: {orderId: $('meta[name="orderId"]').attr('content')},
            success: function (data) {
                if (data.result) {
                    console.log('成功');
                    history.go(0);
                } else {
                    $('#order-operation-info').hide().text('失败:' + data.info).fadeIn();
                }
            }
        });
    }
});