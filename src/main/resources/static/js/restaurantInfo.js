//添加点菜菜品
$('.offering-add-button').on('click', function () {
    const father = $(this).parents('.offering-div');
    const offeringId = father.attr('value');
    $('#offering-selected-table').append(
        $('<tr></tr>').attr('value', offeringId).attr('class', 'offering-selected-tr').append(
            $('<td></td>').text(father.find('.offering-name-h3').text())
        ).append(
            $('<td></td>').text(father.find('.offering-price-h3').text())
        ).append(
            $('<td></td>').append(
                $('<span></span>').text('1')
            ).append(
                $('<button></button>').text('+')
            ).append(
                $('<button></button>').text('-')
            )
        )
    )
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