const restaurantDistributeChart = echarts.init(document.getElementById('restaurant-region-distribution-chart'));
const restaurantTimeChart = echarts.init(document.getElementById('restaurant-register-time-chart'));
const customerRegisterTimeChart = echarts.init(document.getElementById('customer-register-time-chart'));
const customerLevelChart = echarts.init(document.getElementById('customer-level-chart'));
const customerConsumptionAmountChart = echarts.init(document.getElementById('customer-consumption-amount-chart'));
const customerOrderQuantityChart = echarts.init(document.getElementById('customer-order-quantity-chart'));
const orderTimeChart = echarts.init(document.getElementById('order-time-chart'));
const financialTimeChart = echarts.init(document.getElementById('financial-time-chart'));

//餐厅
$.ajax({
    type: 'get',
    url: '/management/statistics/restaurant',
    data: {},
    success: function (data) {
        data.sort(function (a, b) {
            return new Date(a.createTime).getTime() - new Date(b.createTime).getTime();
        });

        drawRestaurantDistributeChart(data);
        drawRestaurantTimeChart(data);
    }
});

//会员
$.ajax({
    type: 'get',
    url: '/management/statistics/customer',
    data: {},
    success: function (data) {
        data.sort(function (a, b) {
            return new Date(a.registerTime).getTime() - new Date(b.registerTime).getTime();
        });
        drawCustomerLevelChart(data);
        drawCustomerRegisterTimeChart(data);
        drawCustomerConsumptionAmountChart(data);
        drawCustomerOrderQuantityChart(data);
    }
});

//订单
$.ajax({
    type: 'get',
    url: '/management/statistics/order',
    data: {},
    success: function (orderData) {
        orderData.sort(function (a, b) {
            return new Date(a.time).getTime() - new Date(b.time).getTime();
        });
        drawOrderTimeChart(orderData);

        //充值记录
        $.ajax({
            type: 'get',
            url: '/management/statistics/recharge',
            data: {},
            success: function (rechargeData) {
                rechargeData.sort(function (a, b) {
                    return new Date(a.time).getTime() - new Date(b.time).getTime();
                });
                drawFinancialChart(orderData, rechargeData);
            }
        });
    }
});

function drawFinancialChart(orderData, rechargeData) {
    const orderStart = new Date(orderData[0].time);
    const orderEnd = new Date(orderData[orderData.length - 1].time);
    const rechargeStart = new Date(rechargeData[0].time);
    const rechargeEnd = new Date(rechargeData[rechargeData.length - 1].time);
    const start = orderStart.getTime() < rechargeStart.getTime() ? orderStart : rechargeStart;
    const end = orderEnd.getTime() < rechargeEnd.getTime() ? rechargeEnd : orderEnd;

    var orderByTime = getDaysByStartAndEnd(start, end);
    var rechargeByTime = getDaysByStartAndEnd(start, end);
    var finalByTime = getDaysByStartAndEnd(start, end);
    const format = 'yyyy-MM-dd';
    $.each(orderData, function (i, e) {
        const day = new Date(e.time).format(format);
        orderByTime[day] += e.moneyToRestaurant;
    });
    $.each(rechargeData, function (i, e) {
        const day = new Date(e.time).format(format);
        rechargeByTime[day] += e.amount;
    });
    $.each(Object.keys(finalByTime), function (i, e) {
        finalByTime[e] = rechargeByTime[e] - orderByTime[e];
    });

    financialTimeChart.setOption({
        title: {text: '财务统计'},
        tooltip: {trigger: 'axis'},
        xAxis: {data: Object.keys(orderByTime), name: '日期'},
        yAxis: {name: '元', splitLine: {show: true}},
        dataZoom: [{}, {type: 'inside'}],
        toolbox: {
            left: 'center', feature: {
                dataZoom: {
                    yAxisIndex: 'none'
                },
                restore: {},
                saveAsImage: {}
            }
        }, legend: {
            type: 'scroll',
            orient: 'vertical',
            right: 10,
            top: 20,
            bottom: 20
        },
        series: [{
            name: '支付给商家',
            type: 'line',
            data: Object.values(orderByTime)
        }, {
            name: '会员充值',
            type: 'line',
            data: Object.values(rechargeByTime)
        }, {
            name: '合计',
            type: 'line',
            data: Object.values(finalByTime)
        }]
    });
}

function drawOrderTimeChart(data) {
    var byTime = getDaysByStartAndEnd(data[0].time, data[data.length - 1].time);
    const format = 'yyyy-MM-dd';
    $.each(data, function (i, e) {
        const day = new Date(e.time).format(format);
        byTime[day] += 1;
    });
    orderTimeChart.setOption({
        title: {text: '已完成订单折线图'},
        tooltip: {trigger: 'axis'},
        xAxis: {data: Object.keys(byTime), name: '日期'},
        yAxis: {name: '订单数', splitLine: {show: true}},
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
}

function listToCountList(intervals, data) {
    var result = [];
    $.each(intervals, function (i, e) {
        const isSingle = e.length === 1;
        var obj = {
            value: 0,
            name: isSingle ? e[0] + '以上' : e[0] + '-' + e[1]
        };
        $.each(data, function (dataIndex, dataE) {
            if (dataE >= e[0] && (isSingle || dataE <= e[1])) {
                obj.value += 1;
            }
        });
        result.push(obj);
    });
    return result;
}

//订单数
function drawCustomerOrderQuantityChart(data) {
    var byOrderQuantity = listToCountList(
        [[0, 5],
            [6, 20],
            [21, 50],
            [51, 100],
            [101]], data.map(function (item) {
            return item.orderQuantity;
        }));

    customerOrderQuantityChart.setOption({
        title: {text: '会员订单数分布', left: 'center'},
        tooltip: {
            trigger: 'item', formatter: function (params) {
                return '订单数:' + params.name + '<br>会员数:' + params.value;
            }
        },
        legend: {
            type: 'scroll',
            orient: 'vertical',
            right: 10,
            top: 20,
            bottom: 20
        },
        series: {
            type: 'pie',
            center: ['50%', '50%'],
            radius: '70%',
            data: byOrderQuantity
        }
    });
}

//消费金额
function drawCustomerConsumptionAmountChart(data) {
    var byAmount = listToCountList(
        [[0, 100],
            [101, 500],
            [501, 1000],
            [1001, 2000],
            [2001]], data.map(function (item) {
            return item.consumptionAmount;
        }));
    customerConsumptionAmountChart.setOption({
        title: {text: '会员消费金额分布', left: 'center'},
        tooltip: {
            trigger: 'item', formatter: function (params) {
                return '消费金额:' + params.name + '<br>会员数:' + params.value;
            }
        },
        legend: {
            type: 'scroll',
            orient: 'vertical',
            right: 10,
            top: 20,
            bottom: 20
        },
        series: {
            type: 'pie',
            center: ['50%', '50%'],
            radius: '70%',
            data: byAmount
        }
    });
}

function drawCustomerRegisterTimeChart(data) {
    var byTime = getDaysByStartAndEnd(data[0].registerTime, data[data.length - 1].registerTime);
    const format = 'yyyy-MM-dd';
    $.each(data, function (i, e) {
        const day = new Date(e.registerTime).format(format);
        byTime[day] += 1;
    });

    customerRegisterTimeChart.setOption({
        title: {text: '会员注册时间趋势'},
        tooltip: {trigger: 'axis'},
        xAxis: {data: Object.keys(byTime), name: '日期'},
        yAxis: {name: '注册数', splitLine: {show: true}},
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
}

function drawCustomerLevelChart(data) {
    var byLevel = getLevelList(data);
    customerLevelChart.setOption({
        title: {text: '会员等级分布', left: 'center'},
        tooltip: {
            trigger: 'item', formatter: function (params) {
                return '等级:' + params.name + '<br>数量:' + params.value;
            }
        },
        legend: {
            type: 'scroll',
            orient: 'vertical',
            right: 10,
            top: 20,
            bottom: 20
        },
        series: {
            type: 'pie',
            center: ['50%', '50%'],
            radius: '70%',
            data: byLevel
        }
    });
}

function getLevelList(data) {
    var result = [];
    $.each(data, function (i, e) {
        var found = false;
        $.each(result, function (i1, e1) {
            if (e1.name === e.level) {
                e1.value += 1;
                found = true;
            }
        });
        if (!found) {
            result.push({
                name: e.level,
                value: 1
            });
        }
    });
    return result;
}

function drawRestaurantDistributeChart(data) {
    var d = [];
    $.each(data, function (i, e) {
        d.push({
            name: e.name,
            value: [e.lng, e.lat, e.restaurantId]
        });
    });

    var option = {
        title: {
            text: '餐厅分布',
            left: 'center'
        },
        tooltip: {
            trigger: 'item',
            formatter: function (params) {
                return params.name + ' : ' + params.value[2];
            }
        },
        bmap: {
            center: [120.13066322374, 30.240018034923],
            zoom: 5,
            roam: true,
            mapStyle: {
                styleJson: [{
                    'featureType': 'water',
                    'elementType': 'all',
                    'stylers': {
                        'color': '#d1d1d1'
                    }
                }, {
                    'featureType': 'land',
                    'elementType': 'all',
                    'stylers': {
                        'color': '#f3f3f3'
                    }
                }, {
                    'featureType': 'railway',
                    'elementType': 'all',
                    'stylers': {
                        'visibility': 'off'
                    }
                }, {
                    'featureType': 'highway',
                    'elementType': 'all',
                    'stylers': {
                        'color': '#fdfdfd'
                    }
                }, {
                    'featureType': 'highway',
                    'elementType': 'labels',
                    'stylers': {
                        'visibility': 'off'
                    }
                }, {
                    'featureType': 'arterial',
                    'elementType': 'geometry',
                    'stylers': {
                        'color': '#fefefe'
                    }
                }, {
                    'featureType': 'arterial',
                    'elementType': 'geometry.fill',
                    'stylers': {
                        'color': '#fefefe'
                    }
                }, {
                    'featureType': 'poi',
                    'elementType': 'all',
                    'stylers': {
                        'visibility': 'off'
                    }
                }, {
                    'featureType': 'green',
                    'elementType': 'all',
                    'stylers': {
                        'visibility': 'off'
                    }
                }, {
                    'featureType': 'subway',
                    'elementType': 'all',
                    'stylers': {
                        'visibility': 'off'
                    }
                }, {
                    'featureType': 'manmade',
                    'elementType': 'all',
                    'stylers': {
                        'color': '#d1d1d1'
                    }
                }, {
                    'featureType': 'local',
                    'elementType': 'all',
                    'stylers': {
                        'color': '#d1d1d1'
                    }
                }, {
                    'featureType': 'arterial',
                    'elementType': 'labels',
                    'stylers': {
                        'visibility': 'off'
                    }
                }, {
                    'featureType': 'boundary',
                    'elementType': 'all',
                    'stylers': {
                        'color': '#fefefe'
                    }
                }, {
                    'featureType': 'building',
                    'elementType': 'all',
                    'stylers': {
                        'color': '#d1d1d1'
                    }
                }, {
                    'featureType': 'label',
                    'elementType': 'labels.text.fill',
                    'stylers': {
                        'color': '#999999'
                    }
                }]
            }
        },
        series: [{
            type: 'scatter',
            coordinateSystem: 'bmap',
            data: d
        }]
    };

    restaurantDistributeChart.setOption(option);
}

function drawRestaurantTimeChart(data) {
    var byTime = getDaysByStartAndEnd(data[0].createTime, data[data.length - 1].createTime);
    const format = 'yyyy-MM-dd';
    $.each(data, function (i, e) {
        const day = new Date(e.createTime).format(format);
        byTime[day] += 1;
    });

    restaurantTimeChart.setOption({
        title: {text: '餐厅注册时间趋势'},
        tooltip: {trigger: 'axis'},
        xAxis: {data: Object.keys(byTime), name: '日期'},
        yAxis: {name: '注册数', splitLine: {show: true}},
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