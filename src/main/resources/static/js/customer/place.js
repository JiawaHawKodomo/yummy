


//选择地址
$('.location-select-radio').on('click', function () {
	
    location = '/customer/place?location_id=' + $(this).val();
});

//搜索按钮
$('#search-button').on('click', function () {
    var url = '/customer/place';
    const locationValue = $('.location-select-radio:checked').val();
    if (locationValue !== undefined) {
        url += '?location_id=' + locationValue + '&search=' + $('#search-input').val();
    }
    location = url;
});
////跳转店铺
//$('#search-button').on('click', function () {
//  var url = '/customer/place';
//  const locationValue = $('.location-select-radio:checked').val();
//  if (locationValue !== undefined) {
//      url += '?location_id=' + locationValue + '&search=' + $('#search-input').val();
//  }
//  location = url;
//});

//将相关字符装换为红色
$('.restaurant-search-tip').each(function () {
    const search = $('#search-input').val();
    $(this).html($(this).text().replace(new RegExp(search, 'g'), '<em>' + search + '</em>'));
});