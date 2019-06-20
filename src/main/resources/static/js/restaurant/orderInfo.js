//确认送达
$('#confirm-button').on('click', function () {
    bootbox.confirm('确认已送达么?', function (data) {
        if (data) {
            $.ajax({
                type: 'put',
                url: '/restaurant/order/confirm',
                data: {orderId: $('meta[name="orderId"]').attr('content')},
                success: function (data) {
                    if (data.result) {
                        bootbox.alert(
                            {
                                title:"操作提示",
                                message:"成功",
                                callback:function () {
                                    history.go(0);
                                }
                            });
                    } else {
                        $('#order-operation-info').hide().text('失败:' + data.info).fadeIn();
                    }
                }
            });
        }
    });
});