package com.kodomo.yummy.bl.customer;

import com.kodomo.yummy.controller.vo.CustomerLevelStrategyVo;
import com.kodomo.yummy.controller.vo.CustomerStatisticsVo;
import com.kodomo.yummy.dao.CustomerDao;
import com.kodomo.yummy.dao.CustomerLevelStrategyDao;
import com.kodomo.yummy.dao.ManagerDao;
import com.kodomo.yummy.entity.Customer;
import com.kodomo.yummy.entity.CustomerLevelStrategy;
import com.kodomo.yummy.entity.CustomerLevelStrategyDetail;
import com.kodomo.yummy.entity.Manager;
import com.kodomo.yummy.exceptions.ParamErrorException;
import com.kodomo.yummy.exceptions.UserNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-27 10:35
 */
@Component
public class CustomerStrategyHelper {

    private final CustomerLevelStrategyDao customerLevelStrategyDao;
    private final ManagerDao managerDao;
    private final CustomerDao customerDao;

    @Autowired
    public CustomerStrategyHelper(CustomerLevelStrategyDao customerLevelStrategyDao, ManagerDao managerDao, CustomerDao customerDao) {
        this.customerLevelStrategyDao = customerLevelStrategyDao;
        this.managerDao = managerDao;
        this.customerDao = customerDao;
    }

    CustomerLevelStrategy getCurrentCustomerLevelStrategy() {
        return customerLevelStrategyDao.getCurrentStrategy();
    }

    void saveCustomerLevelStrategy(List<CustomerLevelStrategyVo> vos, String managerId) throws UserNotExistsException, ParamErrorException {
        Manager manager = managerDao.findById(managerId).orElse(null);
        if (manager == null) {
            throw new UserNotExistsException();
        }

        CustomerLevelStrategy strategy = new CustomerLevelStrategy();
        Set<CustomerLevelStrategyDetail> details = dealWithCustomerLevelStrategyVo(vos);
        strategy.setDetails(details);
        strategy.setManager(manager);

        //处理新旧策略
        CustomerLevelStrategy oldStrategy = customerLevelStrategyDao.getCurrentStrategy();
        customerLevelStrategyDao.save(strategy);
        if (oldStrategy != null) {
            oldStrategy.setEndTime(new Date());
            customerLevelStrategyDao.save(oldStrategy);
        }

        //重新给所有customer设定等级
        setLevelForAllCustomers(strategy);
    }

    /**
     * 验证vo列表的合法性, 必须等级连贯从1开始, 且等级越高折扣率越高, 等级越高消费金额越高
     *
     * @param vos
     * @return 建立好的set
     */
    private Set<CustomerLevelStrategyDetail> dealWithCustomerLevelStrategyVo(List<CustomerLevelStrategyVo> vos) throws ParamErrorException {
        if (vos == null || vos.isEmpty()) {
            throw new ParamErrorException("策略条目");
        }

        Set<CustomerLevelStrategyDetail> result = new HashSet<>();
        vos.sort(Comparator.comparingInt(CustomerLevelStrategyVo::getLevel));
        for (int i = 0; i < vos.size(); i++) {
            CustomerLevelStrategyVo thisVo = vos.get(i);
            if (thisVo.getLevel() == null || thisVo.getLevel() != i + 1) {
                throw new ParamErrorException("等级");
            }

            if (thisVo.getAmount() == null || thisVo.getRate() == null) {
                throw new ParamErrorException("消费金额", "折扣率");
            }

            if (i != 0) {
                if (thisVo.getRate() < vos.get(i - 1).getRate() || thisVo.getRate() >= 1 || thisVo.getRate() < 0) {
                    throw new ParamErrorException("折扣率");
                } else if (thisVo.getAmount() < vos.get(i - 1).getAmount() || thisVo.getAmount() < 0) {
                    throw new ParamErrorException("消费金额");
                }
            }

            CustomerLevelStrategyDetail detail = new CustomerLevelStrategyDetail();
            detail.setLevel(thisVo.getLevel());
            detail.setConsumptionAmount(thisVo.getAmount());
            detail.setDiscountRate(thisVo.getRate());
            result.add(detail);
        }
        return result;
    }

    /**
     * 计算所有customer的等级
     *
     * @param strategy
     */
    private void setLevelForAllCustomers(CustomerLevelStrategy strategy) {
        List<Customer> customers = customerDao.findAll();
        customers.forEach(customer -> customer.setLevel(strategy.calculateLevel(customer.getTotalConsumptionAmount())));
        customerDao.saveAll(customers);
    }
}
