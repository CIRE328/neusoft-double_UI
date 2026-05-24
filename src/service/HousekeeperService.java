package service;

import dao.*;
import pojo.*;
import java.util.*;
import java.util.stream.Collectors;

public class HousekeeperService {
    private final CustomerDao customerDao = new CustomerDao();
    private final UserDao userDao = new UserDao();

    public List<User> findAllHousekeepers() {
        return userDao.findAll().stream().filter(u -> u.getRoleId() == 2).collect(Collectors.toList());
    }

    public List<Customer> findCustomersWithoutHousekeeper() {
        return customerDao.findAll().stream()
                .filter(c -> c.getUserId() == null || c.getUserId() == -1)
                .collect(Collectors.toList());
    }

    public List<Customer> findCustomersByHousekeeper(Integer housekeeperId) {
        return customerDao.findAll().stream()
                .filter(c -> c.getUserId() != null && c.getUserId().equals(housekeeperId))
                .collect(Collectors.toList());
    }

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

    public boolean removeHousekeeper(Integer customerId) {
        Optional<Customer> opt = customerDao.findById(customerId);
        if (opt.isEmpty()) return false;
        Customer c = opt.get();
        c.setUserId(-1);
        customerDao.update(c);
        return true;
    }
}