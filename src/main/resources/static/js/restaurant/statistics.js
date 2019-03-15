const dayQuantityChart = echarts.init(document.getElementById('day-order-quantity'));
const dayAmountChart = echarts.init(document.getElementById('day-order-amount'));
const monthQuantityChart = echarts.init(document.getElementById('month-order-quantity'));
const monthAmountChart = echarts.init(document.getElementById('month-order-amount'));
const detailTable = $('#detail-table');

$.ajax({
    type: 'get',
    url: '/restaurant/statistics/data',
    data: {},
    success: function (data) {
        data.sort(function (a, b) {
            return new Date(a.time).getTime() - new Date(b.time).getTime()
        });

        drawDayAmountChart(data);
        drawDayQuantityChart(data);
        drawMonthAmountChart(data);
        drawMonthQuantityChart(data);
        loadDetails(data);
    }
});

function loadDetails(data) {
    const format = 'yyyy-MM-dd hh:mm:ss';
    $.each(data, function (i, e) {
        detailTable.append(
            $('<tr></tr>').append(
                $('<td></td>').text(new Date(e.time).format(format))
            ).append(
                $('<td></td>').text(e.orderId)
            ).append(
                $('<td></td>').text(e.moneyToRestaurant.toFixed(2) + '元')
            ).append(
                $('<td></td>').text(e.customerEmail)
            ).append(
                $('<td></td>').append(
                    $('<a></a>').attr('href', '/restaurant/order/' + e.orderId)
                        .text('查看')
                )
            )
        )
    })
}

function drawDayQuantityChart(data) {
    var byTime = {};
    const format = 'yyyy-MM-dd';
    $.each(data, function (i, e) {
        const day = new Date(e.time).format(format);
        if (byTime[day] === undefined) {
            byTime[day] = 1;
        } else {
            byTime[day] += 1;
        }
    });

    dayQuantityChart.setOption({
        title: {text: '订单数统计'},
        tooltip: {trigger: 'axis'},
        xAxis: {data: Object.keys(byTime), name: '日期'},
        yAxis: {name: '单数', splitLine: {show: true}},
        dataZoom: [{}, {type: 'inside'}],
        toolbox: {
            left: 'center', feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
            }
        },
        series: {
            name: '日期统计',
            type: 'line',
            data: Object.values(byTime)
        }
    });
    dayQuantityChart.on('click', function (param) {
        window.open('/restaurant/statistics/time/' + param.name + '?format=' + format)
    });
}

function drawDayAmountChart(data) {
    var byTime = {};
    const format = 'yyyy-MM-dd';
    $.each(data, function (i, e) {
        const day = new Date(e.time).format(format);
        if (byTime[day] === undefined) {
            byTime[day] = e.moneyToRestaurant;
        } else {
            byTime[day] += e.moneyToRestaurant;
        }
    });

    dayAmountChart.setOption({
        title: {text: '销售额统计'},
        tooltip: {trigger: 'axis'},
        xAxis: {data: Object.keys(byTime), name: '日期'},
        yAxis: {name: '销售额', splitLine: {show: true}},
        dataZoom: [{}, {type: 'inside'}],
        toolbox: {
            left: 'center', feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
            }
        },
        series: {
            name: '日期统计',
            type: 'line',
            data: Object.values(byTime)
        }
    });
    dayAmountChart.on('click', function (param) {
        window.open('/restaurant/statistics/time/' + param.name + '?format=' + format)
    });
}

function drawMonthQuantityChart(data) {
    var byTime = {};
    const format = 'yyyy-MM';
    $.each(data, function (i, e) {
        const day = new Date(e.time).format(format);
        if (byTime[day] === undefined) {
            byTime[day] = 1;
        } else {
            byTime[day] += 1;
        }
    });

    monthQuantityChart.setOption({
        title: {text: '订单数统计'},
        tooltip: {trigger: 'axis'},
        xAxis: {data: Object.keys(byTime), name: '月份'},
        yAxis: {name: '单数', splitLine: {show: true}},
        dataZoom: [{}, {type: 'inside'}],
        toolbox: {
            left: 'center', feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
            }
        },
        series: {
            name: '日期统计',
            type: 'line',
            data: Object.values(byTime)
        }
    });
    monthQuantityChart.on('click', function (param) {
        window.open('/restaurant/statistics/time/' + param.name + '?format=' + format)
    })
}

function drawMonthAmountChart(data) {
    var byTime = {};
    const format = 'yyyy-MM';
    $.each(data, function (i, e) {
        const day = new Date(e.time).format(format);
        if (byTime[day] === undefined) {
            byTime[day] = e.moneyToRestaurant;
        } else {
            byTime[day] += e.moneyToRestaurant;
        }
    });

    monthAmountChart.setOption({
        title: {text: '销售额统计'},
        tooltip: {trigger: 'axis'},
        xAxis: {data: Object.keys(byTime), name: '月份'},
        yAxis: {name: '销售额', splitLine: {show: true}},
        dataZoom: [{}, {type: 'inside'}],
        toolbox: {
            left: 'center', feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
            }
        },
        series: {
            name: '日期统计',
            type: 'line',
            data: Object.values(byTime)
        }
    });
    monthAmountChart.on('click', function (param) {
        window.open('/restaurant/statistics/time/' + param.name + '?format=' + format)
    });
}

Date.prototype.format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1,                 //月份
        "d+": this.getDate(),                    //日
        "h+": this.getHours(),                   //小时
        "m+": this.getMinutes(),                 //分
        "s+": this.getSeconds(),                 //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds()             //毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
};