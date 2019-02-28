const registerResponseInfo = $('#manager-register-info');
const strategyTable = $('#strategy-table');
const refundStrategyTable = $('#refund-strategy-table');
const customerLevelStrategyTable = $('#customer-level-strategy-table');

$('.restaurant-pass-button').on('click', function () {
    const id = $(this).attr('id').split('-')[2];
    tryToApprove(id, true);
});

$('.restaurant-no-pass-button').on('click', function () {
    const id = $(this).attr('id').split('-')[3];
    tryToApprove(id, false);
});

$('.refund-strategy-remove-button').on('click', function () {
    $(this).parents('.refund-strategy-tr').remove();
});

$('.strategy-remove-button').on('click', function () {
    $(this).parents('.strategy-tr').remove();
});

$('.customer-level-strategy-remove-button').on('click', function () {
    $(this).parents('.customer-level-strategy-tr').remove();
});

$('#customer-level-strategy-add-button').on('click', function () {
    customerLevelStrategyTable.append(
        $('<tr></tr>').attr('class', 'customer-level-strategy-tr').append(
            $('<td></td>').append($('<input>').attr('class', 'customer-level-strategy-level-input'))
        ).append(
            $('<td></td>').append($('<input>').attr('class', 'customer-level-strategy-amount-input'))
        ).append(
            $('<td></td>').append($('<input>').attr('class', 'customer-level-strategy-rate-input'))
        ).append(
            $('<td></td>').append($('<button></button>').text('删除').attr('class', 'customer-level-strategy-remove-button')
                .on('click', function () {
                    $(this).parents('.customer-level-strategy-tr').remove();
                }))
        )
    )
});

$('#refund-strategy-add-button').on('click', function () {
    refundStrategyTable.append(
        $('<tr></tr>').attr('class', 'refund-strategy-tr').append(
            $('<td></td>').append($('<input>').attr('class', 'refund-strategy-more-input'))
        ).append(
            $('<td></td>').append($('<input>').attr('class', 'refund-strategy-less-input'))
        ).append(
            $('<td></td>').append($('<input>').attr('class', 'refund-strategy-rate-input'))
        ).append(
            $('<td></td>').append($('<button></button>').text('删除').attr('class', 'refund-strategy-remove-button')
                .on('click', function () {
                    $(this).parents('.refund-strategy-tr').remove();
                }))
        )
    )
});

$('#strategy-add-button').on('click', function () {
    //添加一行
    strategyTable.append(
        $('<tr></tr>').attr('class', 'strategy-tr').append(
            $('<td></td>').append(
                $('<select></select>').append($('<option></option>').text('城市').attr('value', '0')).append($('<option></option>').text('订单数量').attr('value', '1'))
            )
        ).append(
            $('<td></td>').append($('<input>').attr('class', 'strategy-key-input'))
        ).append(
            $('<td></td>').append($('<input>').attr('class', 'strategy-rate-input'))
        ).append(
            $('<td></td>').append($('<button></button>').text('删除').attr('class', 'strategy-remove-button')
                .on('click', function () {
                    $(this).parents('.strategy-tr').remove();
                }))
        )
    );
});

//保存策略结果
$('#strategy-save-button').on('click', function () {
    var result = [];
    $('.strategy-tr').each(function () {
        const thisE = $(this);
        const type = thisE.find('option:selected').val();
        const key = thisE.find('.strategy-key-input').val();
        const rate = thisE.find('.strategy-rate-input').val();
        const o = {
            type: type,
            key: key,
            rate: rate
        };
        result.push(o);
    });

    $.ajax({
        type: 'post',
        url: '/management/orderStrategy',
        dataType: 'json',
        contentType: 'application/json;charsetset=UTF-8',
        data: JSON.stringify(result),
        success: function (data) {
            console.log(data);
            const infoDiv = $('#strategy-info-div');
            if (data.result) {
                infoDiv.text('成功');
            } else {
                infoDiv.text('失败:' + data.info);
            }
        }
    })
});

//保存退款策略结果
$('#refund-strategy-save-button').on('click', function () {
    var result = [];
    $('.refund-strategy-tr').each(function () {
        const e = $(this);
        result.push({
            more: e.find('.refund-strategy-more-input').val(),
            less: e.find('.refund-strategy-less-input').val(),
            rate: e.find('.refund-strategy-rate-input').val()
        })
    });

    $.ajax({
        type: 'post',
        url: '/management/orderRefundStrategy',
        dataType: 'json',
        contentType: 'application/json;charsetset=UTF-8',
        data: JSON.stringify(result),
        success: function (data) {
            console.log(data);
            const infoDiv = $('#refund-strategy-info-div');
            if (data.result) {
                infoDiv.text('成功');
            } else {
                infoDiv.text('失败:' + data.info);
            }
        }
    })
});

//保存会员等级策略
$('#customer-level-strategy-save-button').on('click', function () {
    var result = [];
    $('.customer-level-strategy-tr').each(function () {
        const e = $(this);
        result.push({
            level: e.find('.customer-level-strategy-level-input').val(),
            amount: e.find('.customer-level-strategy-amount-input').val(),
            rate: e.find('.customer-level-strategy-rate-input').val()
        });
    });

    $.ajax({
        type: 'post',
        url: '/management/customerLevelStrategy',
        dataType: 'json',
        contentType: 'application/json;charsetset=UTF-8',
        data: JSON.stringify(result),
        success: function (data) {
            console.log(data);
            const infoDiv = $('#customer-level-strategy-info-div');
            if (data.result) {
                infoDiv.text('成功');
            } else {
                infoDiv.text('失败:' + data.info);
            }
        }
    })
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