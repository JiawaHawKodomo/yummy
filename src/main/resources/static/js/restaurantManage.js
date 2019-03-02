registerAll();

function registerAll() {
    registerTypeButtons();
    registerOfferingButtons();
    registerStrategyButtons();
}

function registerStrategyButtons() {
    $('.strategy-delete-button').on('click', function () {
        const father = $(this).parents('.strategy-tr');
        if (father.find('.strategy-m-input').length === 0) {
            //已有的策略, 删除
            const id = father.attr('value');
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
            })
        } else {
            father.remove();
        }
    });
}

function registerOfferingButtons() {
    registerOfferingTypeRemoveButton();
    var options = [];
    $('.offering-type-radio').each(function () {
        options.push(
            $('<option></option>').attr('class', 'offering-type-option').val($(this).val())
                .text($(this).parents('label').text())
        )
    });

    //添加种类按钮
    $('.offering-type-add-button').on('click', function () {
        $(this).before(
            $('<div></div>').attr('class', 'offering-type-div').append(
                $('<select></select>').attr('class', 'offering-type-select').append(
                    options
                )
            ).append(
                $('<button></button>').attr('class', 'offering-type-remove-button').text('×')
            )
        );
        registerOfferingTypeRemoveButton();
    });

    //保存
    $('.offering-save-button').on('click', function () {
        const father = $(this).parents('.offering-div');
        const name = father.find('.offering-name-input').val();
        const price = father.find('.offering-price-input').val();
        const note = father.find('.offering-note-input').val();
        const id = father.attr('value');
        const startTime = father.find('.offering-start-time-input').val();
        const endTime = father.find('.offering-end-time-input').val();
        const types = [];

        father.find('.offering-type-option:selected').each(function () {
            types.push($(this).val());
        });

        const data = {
            name: name,
            price: price,
            note: note,
            id: id,
            types: types,
            startTime: startTime,
            endTime: endTime
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
                    alert('成功');
                    history.go(0);
                } else {
                    $('#offering-info').hide().text('失败:' + data.info).fadeIn();
                }
            }
        });
    });

    //删除商品
    $('.offering-delete-button').on('click', function () {
        const father = $(this).parents('.offering-div');
        const id = father.attr('value');
        if (id === undefined || id === null) {
            father.remove();
            return;
        }

        if (!confirm('确定删除该商品吗?')) {
            return;
        }

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
    });
}

function registerOfferingTypeRemoveButton() {
    //删除
    $('.offering-type-remove-button').on('click', function () {
        $(this).parents('.offering-type-div').remove();
    });
}

function registerTypeButtons() {
    //上升按钮
    $('.type-up-button').on('click', function () {
        const thisTr = $(this).parents('.type-tr');
        const prevTr = thisTr.prev();
        if (prevTr.find('.type-name-input').length > 0) {
            thisTr.insertBefore(prevTr);
        }
    });

    //下降按钮
    $('.type-down-button').on('click', function () {
        const thisTr = $(this).parents('.type-tr');
        const nextTr = thisTr.next();
        if (nextTr.find('.type-name-input').length > 0) {
            thisTr.insertAfter(nextTr);
        }
    });

    //删除按钮
    $('.type-delete-button').on('click', function () {
        $(this).parents('.type-tr').remove();
    });
}

//类型添加按钮
$('#type-add-button').on('click', function () {
    $('#type-table').append(
        $('<tr></tr>').attr('class', 'type-tr').append(
            $('<td></td>').append(
                $('<input>').attr('class', 'type-name-input')
            )
        ).append(
            $('<td></td>').append(
                $('<button></button>').attr('class', 'type-up-button').text('↑')
            ).append(
                $('<button></button>').attr('class', 'type-down-button').text('↓')
            ).append(
                $('<button></button>').attr('class', 'type-delete-button').text('删除')
            )
        )
    );
    registerTypeButtons();
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
                alert('成功');
                history.go(0);
            } else {
                typeInfo.text('失败:' + data.info);
            }
        }
    });
});

//添加菜品按钮
$('#offering-add-button').on('click', function () {
    const selectedType = $('.offering-type-radio[name="type"]:checked');
    $(this).after(
        $('<div></div>').attr('class', 'offering-div').append(
            $('<div></div>').append(
                $('<label></label>').append('名称:').append(
                    $('<input>').attr('class', 'offering-name-input')
                )
            )
        ).append(
            $('<div></div>').append(
                $('<label></label>').append('价格:').append(
                    $('<input>').attr('class', 'offering-price-input')
                ).append('元')
            )
        ).append(
            $('<div></div>').append(
                $('<label></label>').append('备注:').append(
                    $('<input>').attr('class', 'offering-note-input')
                )
            )
        ).append(
            $('<div></div>').append(
                $('<label></label>').append('起售日期:').append(
                    $('<input>').attr('class', 'offering-start-time-input').attr('type', 'date')
                )
            ).append(
                $('<label></label>').append('结束日期:').append(
                    $('<input>').attr('class', 'offering-end-time-input').attr('type', 'date')
                )
            )
        ).append(
            $('<div></div>').append('分类').append(
                $('<div></div>').attr('class', 'offering-type-div').append(
                    $('<select></select>').attr('class', 'offering-type-select').attr('disabled', 'disabled').append(
                        $('<option></option>').val(selectedType.val())
                            .attr('class', 'offering-type-option')
                            .text(selectedType.parents('label').text())
                    )
                )
            ).append(
                $('<button></button>').attr('class', 'offering-type-add-button').text('添加到新的类型')
            )
        ).append(
            $('<button></button>').attr('class', 'offering-save-button').text('保存')
        ).append(
            $('<button></button>').attr('class', 'offering-delete-button').text('删除商品')
        )
    );
    registerOfferingButtons();
});

//展示菜品
$('.offering-type-radio').on('click', function () {
    const typeId = $(this).val();
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
$('.offering-type-radio:checked').click();

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
    $('#strategy-table').append(
        $('<tr></tr>').attr('class', 'strategy-tr').append(
            $('<td></td>').append(
                $('<input>').attr('class', 'strategy-m-input')
            )
        ).append(
            $('<td></td>').append(
                $('<input>').attr('class', 'strategy-n-input')
            )
        ).append(
            $('<td></td>').append(
                $('<button></button>').attr('class', 'strategy-delete-button').append('删除')
            )
        )
    );
    registerStrategyButtons();
});

//保存策略
$('#strategy-save-button').on('click', function () {
    var strategies = [];
    $('.strategy-tr').each(function () {
        if ($(this).find('.strategy-m-input').length > 0) {
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
                alert('成功');
                history.go(0);
            } else {
                $('#strategy-info').hide().text(data.info).fadeIn();
            }
        }
    });
});