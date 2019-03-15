const operationInfoText = $('#order-operation-info');
const orderId = $('meta[name="orderId"]').attr('content');

//付款
$('#pay-button').on('click', function () {
    $.ajax({
        type: 'put',
        url: '/customer/order/pay',
        data: {orderId: orderId, password: $('#pay-password-input').val()},
        success: function (data) {
            afterSuccess(data);
        }
    })
});

//确认收货

$('#confirm-button').on('click', function () {
    bootbox.confirm('确认收货吗? 收货后钱款将付给商家',function (data) {
        if (data){
            $.ajax({
                type: 'put',
                url: '/customer/order/confirm',
                data: {orderId: orderId},
                success: function (data) {
                    afterSuccess(data);
                }
            });
        }
    });
});

//取消订单
$('#cancel-button').on('click', function () {
    bootbox.confirm('确认取消么? 配送过程中取消将扣去一定程度的违约金',function (data) {
        if(data){
            $.ajax({
                type: 'put',
                url: '/customer/order/cancel',
                data: {orderId: orderId},
                success: function (data) {
                    afterSuccess(data);
                }
            });
        }
    });
});


function afterSuccess(data) {
    if (data.result) {
        bootbox.alert('成功',function () {
            history.go(0);
        });
    } else {
        operationInfoText.hide().text(data.info).fadeIn();
    }
}