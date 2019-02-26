var timeChart = echarts.init(document.getElementById('diagram-time'));
var moneyChart = echarts.init(document.getElementById('diagram-money'));
var restaurantChart = echarts.init(document.getElementById('diagram-restaurant'));
var moneyChartByMonth = echarts.init(document.getElementById('diagram-money-month'));

//加载按时间统计的结果
$.ajax({
    type: 'get',
    url: '/customer/statistics/data',
    data: {},
    success: function (data) {
        data.sort(function (a, b) {
            return new Date(a.time).getTime() - new Date(b.time).getTime()
        });
        drawTimeChart(data);
        drawMoneyChart(data);
        drawRestaurantChart(data);
        drawMoneyChartByMonth(data);
    }
});

function drawTimeChart(data) {
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

    timeChart.setOption({
        title: {text: '日期统计'},
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
            type: 'bar',
            data: Object.values(byTime)
        }
    });
    timeChart.on('click', function (param) {
        window.open('/customer/statistics/time/' + param.name + '?format=' + format)
    })
}

function drawMoneyChart(data) {
    moneyChart.setOption({
        title: {text: '金额统计/单'},
        tooltip: {trigger: 'axis'},
        xAxis: {
            data: data.map(function (t) {
                return new Date(t.time).format('yyyy-MM-dd hh:mm:ss');
            }),
            name: '时间'
        },
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
        yAxis: {splitLine: {show: true}, name: '订单付款金额'},
        series: {
            type: 'line',
            data: data.map(function (t) {
                return {value: t.money, name: t.orderId}
            })
        }
    });
    moneyChart.on('click', function (param) {
        window.open('/customer/order/' + param.data.name);
    });
}

function drawMoneyChartByMonth(data) {
    var byTime = {};
    const format = 'yyyy-MM';
    $.each(data, function (i, e) {
        const month = new Date(e.time).format(format);
        if (byTime[month] === undefined) {
            byTime[month] = e.money;
        } else {
            byTime[month] += e.money;
        }
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