const registerResponseInfo = $('#manager-register-info');
const strategyTable = $('#strategy-table');
const refundStrategyTable = $('#refund-strategy-table');
const customerLevelStrategyTable = $('#customer-level-strategy-table');
const restaurantModificationInfo = $('#restaurant-modification-info');

const restaurantRigisterBadge = $('#approve-restaurant-register-badge');
const restaurantModifyBadge = $('#approve-restaurant-modify-badge');

$('.restaurant-modification-pass-button').on('click', function () {
    restaurantModificationConfirm(true, $(this).parents('.restaurant-modification-tr'));
});

$('.restaurant-modification-not-pass-button').on('click', function () {
    restaurantModificationConfirm(false, $(this).parents('.restaurant-modification-tr'));
});

function restaurantModificationConfirm(pass, elem) {
    $.ajax({
        type: 'put',
        url: '/management/restaurantModificationConfirm',
        data: {pass: pass, id: elem.attr('value')},
        success: function (data) {
            if (data.result) {
                restaurantModificationInfo.hide().text('成功').fadeIn();
                elem.remove();
                const num = Number(restaurantModifyBadge.text());
                if (num === 1) {
                    restaurantModifyBadge.remove();
                } else {
                    restaurantModifyBadge.text(num - 1);
                }
            } else {
                restaurantModificationInfo.hide().text('失败:' + data.info).fadeIn();
            }
        }
    })
}

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
    var newElement = $('#create-new-customer-level-strategy-tr').clone(true);
    customerLevelStrategyTable.append(newElement);
    newElement.attr('id', '').attr('class', 'customer-level-strategy-tr').show();
});

$('#refund-strategy-add-button').on('click', function () {
    addNewRefundStrategy();
});

$('#strategy-add-button').on('click', function () {
    //添加一行
    var newElement = $('#create-new-order-settlement-strategy-tr').clone(true);
    strategyTable.append(newElement);
    newElement.attr('class', 'strategy-tr').attr('id', '').show();
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
                    bootbox.alert('已通过', function () {
                        history.go(0);
                    });
                } else {
                    bootbox.alert('未能通过', function () {
                        history.go(0);
                    })
                }

                const num = Number(restaurantRigisterBadge.text());
                if (num === 1) {
                    restaurantRigisterBadge.remove();
                } else {
                    restaurantRigisterBadge.text(num - 1);
                }
            } else {
                bootbox.alert(data.info);
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

//线性添加
$('#refund-strategy-function-add-button').on('click', function () {
    const granularity = Number($('#granularity-input').val());
    const gradient = Number($('#gradient-input').val());
    const start = Number($('#refund-start-input').val());
    const end = Number($('#refund-end-input').val());
    const firstValue = Number($('#first-value-input').val());

    $.each(calculateIntervals(start, end, granularity), function (i, e) {
        addNewRefundStrategy(e[0], e[1], firstValue - i * gradient);
    })
});

function addNewRefundStrategy(start, end, value) {
    var newElement = $('#create-new-refund-strategy-tr').clone(true);
    refundStrategyTable.append(newElement);
    newElement.attr('id', '').attr('class', 'refund-strategy-tr').show();
    if (start !== undefined && end !== undefined && value !== undefined) {
        newElement.find('.refund-strategy-more-input').val(start);
        newElement.find('.refund-strategy-less-input').val(end);
        newElement.find('.refund-strategy-rate-input').val(value.toFixed(2));
    }
}

function calculateIntervals(start, end, granularity) {
    var result = [];
    for (var i = start; i < end; i += granularity) {
        result.push([i, i + granularity - 1]);
    }
    return result;
}

$('#refund-strategy-clear-button').on('click', function () {
    $('.refund-strategy-tr').remove();
});