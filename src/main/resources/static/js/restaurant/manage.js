$('.strategy-delete-button').on('click', function () {
    const father = $(this).parents('.strategy-tr');
    const id = father.attr('value');
    if (id === undefined) {
        father.remove();
    } else {
    //已有的策略, 删除
        $.ajax({
            type: 'delete',
            url: '/restaurant/strategy',
            data: {id: id},
            success: function (data) {
                if (data.result) {
                    $('#strategy-info').hide().text('成功').fadeIn();
                    father.remove();
                } else {
                    $('#strategy-info').hide().text('失败:' + data.info).fadeIn();
                }
            }
        });
    }
});

//删除商品
$('.offering-delete-button').on('click', function () {
    const father = $(this).parents('.offering-div');
    const id = father.attr('value');
    if (id === undefined || id === null) {
        father.remove();
        return;
    }

    bootbox.confirm('确定删除该商品吗?', function (data) {
        if (data) {
            //发送
            $.ajax({
                type: 'delete',
                url: '/restaurant/offering',
                data: {id: id},
                success: function (data) {
                    console.log(data);
                    if (data.result) {
                        father.remove();
                        $('#offering-info').hide().text('成功').fadeIn();
                    } else {
                        $('#offering-info').hide().text('失败:' + data.info).fadeIn();
                    }
                }
            });
        }
    });
});

//保存
$('.offering-save-button').on('click', function () {
    tryToSaveNewOffering($(this).parents('.offering-div'));
});

//上升按钮
$('.type-up-button').on('click', function () {
    const thisTr = $(this).parents('.type-tr');
    const prevTr = thisTr.prev();
    if (prevTr.find('.type-name-input').length > 0) {
        thisTr.fadeOut().fadeIn();
        prevTr.before(thisTr);
    }
});

//下降按钮
$('.type-down-button').on('click', function () {
    const thisTr = $(this).parents('.type-tr');
    const nextTr = thisTr.next();
    if (nextTr.find('.type-name-input').length > 0) {
        //thisTr.insertAfter(nextTr);
        thisTr.fadeOut().fadeIn();
        nextTr.after(thisTr);
    }
});

//删除按钮
$('.type-delete-button').on('click', function () {
    $(this).parents('.type-tr').remove();
});

function tryToSaveNewOffering(father) {
    const name = father.find('.offering-name-input').val();
    const price = father.find('.offering-price-input').val();
    const note = father.find('.offering-note-input').val();
    const id = father.attr('value');
    const startTime = father.find('.offering-start-time-input').val();
    const endTime = father.find('.offering-end-time-input').val();
    const remaining = father.find('.offering-remaining-input').val();
    const types = [];

    father.find('.offering-type-checkbox:checked').each(function () {
        types.push($(this).val());
    });

    const data = {
        name: name,
        price: price,
        note: note,
        id: id,
        types: types,
        startTime: startTime,
        endTime: endTime,
        remaining: remaining
    };

    //发送
    $.ajax({
        type: 'post',
        url: '/restaurant/offering',
        dataType: 'json',
        contentType: 'application/json;charsetset=UTF-8',
        data: JSON.stringify(data),
        success: function (data) {
            console.log(data);
            if (data.result) {
                bootbox.alert('成功', function () {
                    history.go(0);
                });
            } else {
                $('#offering-info').hide().text('失败:' + data.info).fadeIn();
            }
        }
    });
}

//类型添加按钮
$('#type-add-button').on('click', function () {
    var newElement = $('#create-new-type-tr').clone(true);
    newElement.attr('id', '');
    $('#type-table').append(newElement);
    newElement.show();
});

//保存按钮
$('#type-save-button').on('click', function () {
    var data = [];
    var error = '';
    const typeInfo = $('#type-info');
    $('.type-tr').each(function () {
        const name = $(this).find('.type-name-input').val();
        if (name === null || name === '') {
            error = '类型不能为空';
        } else {
            data.push({
                id: $(this).attr('value'),
                name: name
            });
        }
    });

    if (error !== '') {
        typeInfo.text(error);
        return;
    }

    //发送
    $.ajax({
        type: 'put',
        url: '/restaurant/type',
        dataType: 'json',
        contentType: 'application/json;charsetset=UTF-8',
        data: JSON.stringify(data),
        success: function (data) {
            console.log(data);
            if (data.result) {
                bootbox.alert('成功', function () {
                    history.go(0);
                });
            } else {
                typeInfo.text('失败:' + data.info);
            }
        }
    });
});

//添加菜品按钮
$('#offering-add-button').on('click', function () {
    var newElement = $('#create-new-offering-div').clone(true);
    newElement.attr('id', '');
    $(this).after(newElement);
    newElement.slideDown();
});

//展示菜品
$('.offering-type-radio').on('click', function () {
    const typeId = $(this).attr('value');
    $('.offering-div').each(function () {
        const thisDiv = $(this);
        thisDiv.hide();
        const id = thisDiv.attr('id');
        if (id !== undefined) {
            var list = id.split('-');
            list.shift();
            if (list.indexOf(typeId) >= 0) {
                //需要展示, 调整selected的位置
                thisDiv.slideDown();
                thisDiv.find('.offering-type-option').each(function () {
                    if ($(this).val() === typeId) {
                        $(this).select();
                    }
                });
            }
        }
    });
});
$('.offering-type-radio:first').click();

//无类别商品
$('#offering-non-type-radio').on('click', function () {
    $('.offering-div').each(function () {
        $(this).hide();
        const id = $(this).attr('id');
        if (id !== undefined && id === 'non') {
            $(this).slideDown();
        }
    })
});

//添加策略
$('#strategy-add-button').on('click', function () {
    var newElement = $('#create-new-strategy-tr').clone(true);
    newElement.attr('id', '');
    $('#strategy-table').append(newElement);
    newElement.show();
});

//保存策略
$('#strategy-save-button').on('click', function () {
    var strategies = [];
    $('.strategy-tr').each(function () {
        if ($(this).find('.strategy-m-input').length > 0 && $(this).attr('id') !== 'create-new-strategy-tr') {
            strategies.push({
                greaterThan: $(this).find('.strategy-m-input').val(),
                discount: $(this).find('.strategy-n-input').val()
            })
        }
    });

    //发送
    $.ajax({
        type: 'post',
        url: '/restaurant/strategy',
        dataType: 'json',
        contentType: 'application/json;charsetset=UTF-8',
        data: JSON.stringify(strategies),
        success: function (data) {
            console.log(data);
            if (data.result) {
                bootbox.alert('成功', function () {
                    history.go(0);
                });
            } else {
                $('#strategy-info').hide().text(data.info).fadeIn();
            }
        }
    });
});