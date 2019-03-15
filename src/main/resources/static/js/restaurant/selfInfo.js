$('.type-remove-button').on('click', function () {
    $(this).parents('.type-li').remove();
});

$('#type-add-button').on('click', function () {
    $('#type-add-button-li').before(
        $('<li></li>').attr('class', 'type-li').append(
            $('<div></div>').attr('class', 'input-group').append(
                $('<input>').attr('class', 'type-input form-control')
            ).append(
                $('<span></span>').attr('class', 'type-remove-button input-group-addon').text('删除')
                    .on('click', function () {
                        $(this).parents('.type-li').remove();
                    })
            )
        )
    )
});

$('#restaurant-info-update-submit-button').on('click', function () {
    const infoText = $('#restaurant-info-update-info');
    var types = [];
    $('.type-input').each(function () {
        types.push($(this).val());
    });

    $.ajax({
        type: 'put',
        url: '/restaurant/info',
        dataType: 'json',
        contentType: 'application/json;charsetset=UTF-8',
        data: JSON.stringify({
            name: $('#name-input').val(),
            telephone: $('#telephone-input').val(),
            businessHours: $('#business-hours-input').val(),
            locationNote: $("#location-note-input").val(),
            types: types
        }),
        success: function (data) {
            if (data.result) {
                alert('成功');
                history.go(0);
            } else {
                infoText.hide().text('失败:' + data.info).fadeIn();
            }
        }
    })
});