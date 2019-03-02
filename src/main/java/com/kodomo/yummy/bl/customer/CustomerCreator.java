package com.kodomo.yummy.bl.customer;

import com.kodomo.yummy.bl.util.MailHelper;
import com.kodomo.yummy.bl.util.ValidatingHelper;
import com.kodomo.yummy.dao.CustomerDao;
import com.kodomo.yummy.entity.customer.Customer;
import com.kodomo.yummy.entity.entity_enum.UserState;
import com.kodomo.yummy.exceptions.ParamErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Shuaiyu Yao
 * @create 2019-02-16 15:05
 */
@Service
public class CustomerCreator {

    @Value("${yummy-system.activating-link}")
    private String activatePrefix;

    private final CustomerDao customerDao;
    private final MailHelper mailHelper;
    private final ValidatingHelper validatingHelper;

    @Autowired
    public CustomerCreator(CustomerDao customerDao, MailHelper mailHelper, ValidatingHelper validatingHelper) {
        this.customerDao = customerDao;
        this.mailHelper = mailHelper;
        this.validatingHelper = validatingHelper;
    }

    /**
     * 创建新的用户类, 为存储准备, 对参数进行检查, 如果不通过抛出异常
     *
     * @param email    email
     * @param password password
     * @return customer
     * @throws ParamErrorException 参数不正确
     */
    public Customer createNewCustomerForDatabase(String email, String password) throws ParamErrorException {
        //验证不为空
        if (email == null || password == null) {
            throw new ParamErrorException("用户名或密码不能为空");
        }
        //验证账号合法
        if (!validatingHelper.isEmail(email)) {
            throw new ParamErrorException("输入的email不合法");
        }

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setState(UserState.UNACTIVATED);
        //存入数据库
        try {
            customer = customerDao.save(customer);
        } catch (Exception e) {
            throw new ParamErrorException(e.getMessage());
        }

        //发送验证邮件
        mailHelper.sendMail(email, "<html lang='zh-CN'><head><meta charset='utf-8'>"
                + "</head><body><a href='" + activatePrefix + getActivatingCode(customer) + "'>点击此处激活</a></body></html>");
        return customer;
    }

    /**
     * 计算激活码
     *
     * @param customer 激活码
     * @return 激活码
     */
    private String getActivatingCode(Customer customer) {
        return customer.getEmail().replaceAll("@", "%40").replaceAll("\\.", "%2E") + customer.getPassword();
    }

    public Customer activate(String activatingCode) throws ParamErrorException {
        if (activatingCode.length() <= 64) {
            throw new ParamErrorException("激活码格式错误");
        }

        String passwordPart = activatingCode.substring(activatingCode.length() - 64);
        String email = activatingCode.substring(0, activatingCode.length() - 64);
        Customer customer = customerDao.findById(email).orElse(null);
        if (customer == null || !customer.getPassword().equals(passwordPart)) {
            throw new ParamErrorException("未找到激活用户");
        }

        customer.setState(UserState.ACTIVATED);
        customerDao.save(customer);
        return customer;
    }
}
