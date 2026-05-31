package service;

import dao.*;
import pojo.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管家服务，负责管家人员查询及客户与管家的分配与解除。
 */
public class HousekeeperService {
    private final CustomerDao customerDao = new CustomerDao();
    private final UserDao userDao = new UserDao();

    /**
     * 查询所有角色为管家的用户。
     *
     * @return 管家用户列表
     */
    public List<User> findAllHousekeepers() {
        return userDao.findAll().stream().filter(u -> u.getRoleId() == 2).collect(Collectors.toList());
    }

    /**
     * 查询尚未分配管家的客户。
     *
     * @return 未分配管家的客户列表
     */
    public List<Customer> findCustomersWithoutHousekeeper() {
        return customerDao.findAll().stream()
                .filter(c -> c.getUserId() == null || c.getUserId() == -1)
                .collect(Collectors.toList());
    }

    /**
     * 查询指定管家负责的客户。
     *
     * @param housekeeperId 管家用户 ID
     * @return 该管家负责的客户列表
     */
    public List<Customer> findCustomersByHousekeeper(Integer housekeeperId) {
        return customerDao.findAll().stream()
                .filter(c -> c.getUserId() != null && c.getUserId().equals(housekeeperId))
                .collect(Collectors.toList());
    }

    /**
     * 将客户分配给指定管家。
     *
     * @param customerId    客户 ID
     * @param housekeeperId 管家用户 ID
     * @return 分配成功返回 true，客户或管家不存在、管家角色不符时返回 false
     */
    public boolean assignHousekeeper(Integer customerId, Integer housekeeperId) {
        Optional<Customer> optC = customerDao.findById(customerId);
        if (optC.isEmpty()) return false;
        Optional<User> optH = userDao.findById(housekeeperId);
        if (optH.isEmpty() || optH.get().getRoleId() != 2) return false;
        Customer c = optC.get();
        c.setUserId(housekeeperId);
        customerDao.update(c);
        return true;
    }

    /**
     * 解除客户与管家的绑定关系。
     *
     * @param customerId 客户 ID
     * @return 解除成功返回 true，客户不存在时返回 false
     */
    public boolean removeHousekeeper(Integer customerId) {
        Optional<Customer> opt = customerDao.findById(customerId);
        if (opt.isEmpty()) return false;
        Customer c = opt.get();
        c.setUserId(-1);
        customerDao.update(c);
        return true;
    }
}
