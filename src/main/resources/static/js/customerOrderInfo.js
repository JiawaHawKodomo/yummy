const operationInfoText = $('#order-operation-info');
const orderId = $('meta[name="orderId"]').attr('content');

//付款
$('#pay-button').on('click', function () {
    $.ajax({
        type: 'put',
        url: '/customer/order/pay',
        data: {orderId: orderId},
        success: function (data) {
            afterSuccess(data);
        }
    })
});

//确认收货
$('#confirm-button').on('click', function () {
    $.ajax({
        type: 'put',
        url: '/customer/order/confirm',
        data: {orderId: orderId},
        success: function (data) {
            afterSuccess(data);
        }
    })
});

//取消订单
$('#cancel-button').on('click', function () {
    $.ajax({
        type: 'put',
        url: '/customer/order/cancel',
        data: {orderId: orderId},
        success: function (data) {
            afterSuccess(data);
        }
    })
});

function afterSuccess(data) {
    if (data.result) {
        alert('成功');
        history.go(0);
    } else {
        operationInfoText.hide().text(data.info).fadeIn();
    }
}