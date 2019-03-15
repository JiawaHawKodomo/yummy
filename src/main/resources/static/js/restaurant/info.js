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
                bootbox.alert('成功', function () {
                    location = '/customer/order/' + data.result;
                });
            } else {
                infoText.hide().text(data.info).fadeIn();
            }
        }
    });
});

$('.offering-div-plus-button').on('click', function () {
    const father = $(this).parents('.offering-div');
    const offeringId = father.attr('value');
    const name = father.find('.offering-div-name-span').text();
    const price = father.find('.offering-div-price-span').text();
    var elem = findSelectedOffering(offeringId);
    if (elem.length > 0) {
        var numberElem = elem.find('.offering-selected-number');
        numberElem.text(Number(numberElem.text()) + 1);
    } else {
        createNewSelectedOffering(offeringId, name, price);
    }
    calculateTotal();
});

$('.offering-div-minus-button').on('click', function () {
    const offeringId = $(this).parents('.offering-div').attr('value');
    var elem = findSelectedOffering(offeringId);
    if (elem.length > 0) {
        var numberElem = elem.find('.offering-selected-number');
        const num = Number(numberElem.text());
        if (num === 1) {
            elem.remove();
        } else {
            numberElem.text(num - 1);
        }
        calculateTotal();
    }
});


function createNewSelectedOffering(id, name, price) {
    var newElement = $('#create-new-offering-selected-tr').clone(true);
    $('#offering-selected-table').append(newElement);
    newElement.find('.offering-name-id').text(name);
    newElement.find('.offering-price-td').text(price);
    newElement.attr('id', '').attr('class', 'offering-selected-tr').attr('value', id).show();
    newElement.find('.selected-offering-plus-button').on('click', function () {
        var numberElem = newElement.find('.offering-selected-number');
        numberElem.text(Number(numberElem.text()) + 1);
        calculateTotal();
    });
    newElement.find('.selected-offering-minus-button').on('click', function () {
        var numberElem = newElement.find('.offering-selected-number');
        const num = Number(numberElem.text());
        if (num === 1) {
            newElement.remove();
        } else {
            numberElem.text(num - 1);
        }
        calculateTotal();
    });
    newElement.find('.selected-offering-remove-button').on('click', function () {
        newElement.remove();
        calculateTotal();
    });
}

$('#offering-selected-clear-button').on('click', function () {
    $('.offering-selected-tr').remove();
    calculateTotal();
});

function findSelectedOffering(id) {
    return $('.offering-selected-tr[value="' + id + '"]');
}

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

$('.offering-type-radio:first').click();
