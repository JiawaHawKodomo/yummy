//提交订单
$('#offering-submit-button').on('click', function () {
    const infoText = $('#offering-submit-info');
    var details = [];
    const trs = $('.offering-selected-tr');
    if (trs.length === 0) {
        infoText.hide().text('未选择商品').fadeIn();
        return;
    }

    trs.each(function () {
        details.push({
            offeringId: $(this).attr('value'),
            quantity: $(this).find('.offering-selected-number').text()
        });
    });

    const location_id = $('.location-select-radio:checked').val();
    if (location_id === null || location_id === undefined) {
        infoText.hide().text('未选择地址').fadeIn();
        return;
    }

    const data = {
        restaurantId: $('meta[name="restaurantId"]').attr('content'),
        details: details,
        locationId: location_id
    };

    //发送
    $.ajax({
        type: 'post',
        url: '/customer/order',
        dataType: 'json',
        contentType: 'application/json;charsetset=UTF-8',
        data: JSON.stringify(data),
        success: function (data) {
            console.log(data);
            if (data.result) {
                alert('成功');
                location = '/customer/order/' + data.result;
            } else {
                infoText.hide().text(data.info).fadeIn();
            }
        }
    });
});

function offeringNumberPlus(tr) {
    const text = tr.find('.offering-selected-number');
    text.text(Number(text.text()) + 1);
}

//添加点菜菜品
$('.offering-div').on('click', function () {
    const father = $(this);
    const offeringId = father.attr('value');
    const tr = $('#offering-selected-tr-' + offeringId);
    if (tr.length > 0) {
        offeringNumberPlus(tr);
        console.log('offering number plus')
    } else {
        $('#offering-selected-table').append(
            $('<tr></tr>').attr('value', offeringId).attr('class', 'offering-selected-tr')
                .attr('id', 'offering-selected-tr-' + offeringId).append(
                $('<td></td>').text(father.find('.offering-name-h3').text())
            ).append(
                $('<td></td>').text(father.find('.offering-price-h3').text())
                    .attr('class', 'offering-price-td')
            ).append(
                $('<td></td>').append(
                    $('<span></span>').text('×')
                ).append(
                    $('<span></span>').text('1').attr('class', 'offering-selected-number')
                ).append(
                    $('<button></button>').text('+').attr('class', 'offering-selected-plus-button')
                        .on('click', function () {
                            //加
                            const father = $(this).parents('.offering-selected-tr');
                            offeringNumberPlus(father);
                            calculateTotal();
                        })
                ).append(
                    $('<button></button>').text('-').attr('class', 'offering-selected-minus-button')
                        .on('click', function () {
                            //减
                            const father = $(this).parents('.offering-selected-tr');
                            const text = father.find('.offering-selected-number');
                            const num = Number(text.text());
                            if (num <= 1) {
                                father.remove();
                            } else {
                                text.text(num - 1);
                            }
                            calculateTotal();
                        })
                )
            )
        );
    }
    calculateTotal();
});

function calculateTotal() {
    const beforeSpan = $('#offering-total-before-price-span');
    const afterSpan = $('#offering-total-price-span');
    beforeSpan.text('');

    var total = 0;
    $('.offering-selected-tr').each(function () {
        total += Number($(this).find('.offering-selected-number').text()) * Number($(this).find('.offering-price-td').text());
    });

    var discount = 0;
    $('meta[name="strategy"]').each(function () {
        const array = $(this).attr('content').split('-');
        const gt = Number(array[0]);
        const dis = Number(array[1]);
        if (total >= gt && discount <= dis) {
            discount = dis;
        }
    });

    if (discount !== 0) {
        beforeSpan.text(total);
        afterSpan.text(total - discount);
    } else {
        afterSpan.text(total);
    }
}

$('#offering-selected-toggle-button').on('click', function () {
    $('#offering-selected-div').toggle();
    if ($(this).text() === '↑') {
        $(this).text('↓');
    } else {
        $(this).text('↑');
    }
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
                thisDiv.slideDown();
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