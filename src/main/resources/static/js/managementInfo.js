const registerResponseInfo = $('#manager-register-info');
const strategyTable = $('#strategy-table');
registerStrategyRemove();

$('.restaurant-pass-button').on('click', function () {
    const id = $(this).attr('id').split('-')[2];
    tryToApprove(id, true);
});

$('.restaurant-no-pass-button').on('click', function () {
    const id = $(this).attr('id').split('-')[3];
    tryToApprove(id, false);
});

function registerStrategyRemove() {
    $('.strategy-remove-button').on('click', function () {
        $(this).parent().parent().remove();
    });
}

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
            $('<td></td>').append($('<button></button>').text('删除').attr('class', 'strategy-remove-button'))
        )
    );
    //重新注册
    registerStrategyRemove();
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

    console.log(JSON.stringify(result));
    $.ajax({
        type: 'post',
        url: '/management/orderStrategy',
        dataType: 'json',
        data: {"jsonData": JSON.stringify(result)},
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