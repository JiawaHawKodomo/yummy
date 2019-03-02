const timeChart = echarts.init(document.getElementById('diagram-time'));
const restaurantChart = echarts.init(document.getElementById('diagram-restaurant'));
const moneyChartByMonth = echarts.init(document.getElementById('diagram-money-month'));
const monthQuantityChart = echarts.init(document.getElementById('diagram-month-time'));

$.ajax({
    type: 'get',
    url: '/customer/statistics/data',
    data: {},
    success: function (data) {
        data.sort(function (a, b) {
            return new Date(a.time).getTime() - new Date(b.time).getTime()
        });
        drawTimeChart(data);
        drawOrderQuantityMonthChart(data);
        drawRestaurantChart(data);
        drawMoneyChartByMonth(data);
    }
});

function drawOrderQuantityMonthChart(data) {
    var byTime = getMonthsByStartAndEnd(data[0].time, data[data.length - 1].time);
    const format = 'yyyy-MM';
    $.each(data, function (i, e) {
        const day = new Date(e.time).format(format);
        byTime[day] += 1;
    });

    monthQuantityChart.setOption({
        title: {text: '订单数月份统计'},
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
    monthQuantityChart.on('click', function (param) {
        window.open('/customer/statistics/time/' + param.name + '?format=' + format)
    });
}

function drawTimeChart(data) {
    var byTime = getDaysByStartAndEnd(data[0].time, data[data.length - 1].time);
    const format = 'yyyy-MM-dd';
    $.each(data, function (i, e) {
        const day = new Date(e.time).format(format);
        byTime[day] += 1;
    });

    timeChart.setOption({
        title: {text: '订单数日期统计'},
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
    timeChart.on('click', function (param) {
        window.open('/customer/statistics/time/' + param.name + '?format=' + format)
    });
}

function drawMoneyChartByMonth(data) {
    var byTime = getMonthsByStartAndEnd(data[0].time, data[data.length - 1].time);
    const format = 'yyyy-MM';
    $.each(data, function (i, e) {
        const day = new Date(e.time).format(format);
        byTime[day] += e.money;
    });

    moneyChartByMonth.setOption({
        title: {text: '金额统计/月'},
        tooltip: {trigger: 'axis'},
        xAxis: {data: Object.keys(byTime), name: '月'},
        toolbox: {
            left: 'center', feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
            }
        },
        dataZoom: [{}, {type: 'inside'}],
        yAxis: {splitLine: {show: true}, name: '月消费额'},
        series: {
            type: 'line',
            data: Object.values(byTime)
        }
    });
}

function drawRestaurantChart(data) {
    var byRestaurant = {};
    $.each(data, function (i, e) {
        const restaurant = e.restaurantName;
        if (byRestaurant[restaurant] === undefined) {
            byRestaurant[restaurant] = {value: 1, name: e.restaurantId};
        } else {
            byRestaurant[restaurant].value += 1;
        }
    });
    restaurantChart.setOption({
        title: {text: '餐厅统计'},
        tooltip: {trigger: 'axis'},
        dataZoom: [{}, {type: 'inside'}],
        xAxis: {data: Object.keys(byRestaurant), name: '餐厅名称'},
        yAxis: {splitLine: {show: true}, name: '消费次数'},
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
            type: 'bar',
            data: Object.values(byRestaurant)
        }
    });
    restaurantChart.on('click', function (param) {
        window.open('/customer/statistics/restaurant/' + param.data.name);
    });
}

function getMonthsByStartAndEnd(start, end) {
    var result = {};
    const format = 'yyyy-MM';
    var date = new Date(start);
    var endDate = new Date(end);
    while (date.getTime() <= endDate.getTime()) {
        result[date.format(format)] = 0;
        date = getNextMonth(date);
        console.log(date);
    }
    return result;
}

function getNextMonth(date) {
    var month = Number(date.format('MM'));
    var year = Number(date.format('yyyy'));
    if (month === 12) {
        return new Date((year + 1) + '-' + '01');
    } else {
        return new Date(year + '-' + (month + 1));
    }
}

function getDaysByStartAndEnd(start, end) {
    var result = {};
    const format = 'yyyy-MM-dd';
    var date = new Date(start);
    var endDate = new Date(end);
    while (date.getTime() <= endDate.getTime()) {
        result[date.format(format)] = 0;
        date.setTime(date.getTime() + 24 * 60 * 1000);
    }
    return result;
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