package com.neusoft.service;

import com.neusoft.dao.*;
import com.neusoft.pojo.*;
import com.neusoft.util.DateUtils;
import java.util.*;
import java.util.stream.Collectors;

public class CustomerService {
    private final CustomerDao customerDao = new CustomerDao();
    private final BedDao bedDao = new BedDao();
    private final BedDetailsDao bedDetailsDao = new BedDetailsDao();
    private final OutwardDao outwardDao = new OutwardDao();
    private final BackdownDao backdownDao = new BackdownDao();

    public List<Customer> findAllCustomers() { return customerDao.findAll(); }

    public List<Customer> findCustomersByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return findAllCustomers();
        return customerDao.findAll().stream()
                .filter(c -> c.getCustomerName() != null && c.getCustomerName().contains(keyword))
                .collect(Collectors.toList());
    }

    public List<Customer> findCustomersByType(String type) {
        if ("自理老人".equals(type)) {
            return customerDao.findAll().stream()
                    .filter(c -> c.getLevelId() == null)
                    .collect(Collectors.toList());
        } else if ("护理老人".equals(type)) {
            return customerDao.findAll().stream()
                    .filter(c -> c.getLevelId() != null)
                    .collect(Collectors.toList());
        }
        return findAllCustomers();
    }

    public Optional<Customer> findCustomerById(Integer id) { return customerDao.findById(id); }

    public boolean checkin(Customer customer, Integer bedId) {
        if (DateUtils.isAfter(customer.getCheckinDate(), customer.getExpirationDate())) {
            System.err.println("合同到期时间不能小于入住时间");
            return false;
        }
        Optional<Bed> optBed = bedDao.findById(bedId);
        if (optBed.isEmpty() || optBed.get().getBedStatus() != 1) {
            System.err.println("床位不存在或不是空闲状态");
            return false;
        }
        Bed bed = optBed.get();
        if (customer.getBirthday() != null) {
            customer.setCustomerAge(DateUtils.calculateAge(customer.getBirthday()));
        }
        customer.setBuildingNo("001");
        customer.setBedId(bedId);
        if (customer.getUserId() == null) customer.setUserId(-1);
        customer.setIsDeleted(0);
        Customer saved = customerDao.insert(customer);
        bed.setBedStatus(2);
        bedDao.update(bed);
        BedDetails details = new BedDetails();
        details.setStartDate(customer.getCheckinDate());
        details.setEndDate(null);
        details.setCustomerId(saved.getId());
        details.setBedId(bedId);
        details.setIsDeleted(0);
        bedDetailsDao.insert(details);
        return true;
    }

    public boolean updateCustomer(Customer customer) {
        if (customerDao.findById(customer.getId()).isEmpty()) return false;
        Customer old = customerDao.findById(customer.getId()).get();
        if (!old.getExpirationDate().equals(customer.getExpirationDate())) {
            List<BedDetails> detailsList = bedDetailsDao.findAll().stream()
                    .filter(d -> d.getCustomerId().equals(customer.getId()) && d.getEndDate() == null && d.getIsDeleted() == 0)
                    .collect(Collectors.toList());
            for (BedDetails d : detailsList) {
                d.setEndDate(customer.getExpirationDate());
                bedDetailsDao.update(d);
            }
        }
        customerDao.update(customer);
        return true;
    }

    public boolean deleteCustomer(Integer customerId) {
        Optional<Customer> opt = customerDao.findById(customerId);
        if (opt.isEmpty()) return false;
        Customer customer = opt.get();
        if (customer.getBedId() != null) {
            bedDao.findById(customer.getBedId()).ifPresent(bed -> {
                bed.setBedStatus(1);
                bedDao.update(bed);
            });
        }
        List<BedDetails> detailsList = bedDetailsDao.findAll().stream()
                .filter(d -> d.getCustomerId().equals(customerId) && d.getEndDate() == null && d.getIsDeleted() == 0)
                .collect(Collectors.toList());
        for (BedDetails d : detailsList) {
            d.setEndDate(DateUtils.now());
            bedDetailsDao.update(d);
        }
        return customerDao.deleteById(customerId);
    }

    public boolean submitOutward(Outward outward) {
        if (customerDao.findById(outward.getCustomerId()).isEmpty()) return false;
        outward.setAuditstatus(0);
        outward.setIsDeleted(0);
        outwardDao.insert(outward);
        return true;
    }

    public List<Outward> findAllOutwards() { return outwardDao.findAll(); }

    public List<Outward> findOutwardsByCustomerName(String keyword) {
        List<Customer> customers = findCustomersByName(keyword);
        if (customers.isEmpty()) return List.of();
        Set<Integer> ids = customers.stream().map(Customer::getId).collect(Collectors.toSet());
        return outwardDao.findAll().stream()
                .filter(o -> ids.contains(o.getCustomerId()))
                .collect(Collectors.toList());
    }

    public boolean auditOutward(Integer outwardId, boolean approved, String auditorName) {
        Optional<Outward> opt = outwardDao.findById(outwardId);
        if (opt.isEmpty()) return false;
        Outward outward = opt.get();
        if (outward.getAuditstatus() != 0) return false;
        if (approved) {
            outward.setAuditstatus(1);
            customerDao.findById(outward.getCustomerId()).ifPresent(customer ->
                    bedDao.findById(customer.getBedId()).ifPresent(bed -> {
                        bed.setBedStatus(3);
                        bedDao.update(bed);
                    })
            );
        } else {
            outward.setAuditstatus(2);
        }
        outward.setAuditperson(auditorName);
        outward.setAudittime(DateUtils.now());
        outwardDao.update(outward);
        return true;
    }

    public boolean returnFromOutward(Integer outwardId, Date actualReturnTime) {
        Optional<Outward> opt = outwardDao.findById(outwardId);
        if (opt.isEmpty()) return false;
        Outward outward = opt.get();
        if (outward.getAuditstatus() != 1) return false;
        outward.setActualreturntime(actualReturnTime);
        outwardDao.update(outward);
        customerDao.findById(outward.getCustomerId()).ifPresent(customer ->
                bedDao.findById(customer.getBedId()).ifPresent(bed -> {
                    bed.setBedStatus(2);
                    bedDao.update(bed);
                })
        );
        return true;
    }

    public boolean submitBackdown(BackDown backdown) {
        if (customerDao.findById(backdown.getCustomerId()).isEmpty()) return false;
        backdown.setAuditstatus(0);
        backdown.setIsDeleted(0);
        backdownDao.insert(backdown);
        return true;
    }

    public List<BackDown> findAllBackdowns() { return backdownDao.findAll(); }

    public List<BackDown> findBackdownsByCustomerName(String keyword) {
        List<Customer> customers = findCustomersByName(keyword);
        if (customers.isEmpty()) return List.of();
        Set<Integer> ids = customers.stream().map(Customer::getId).collect(Collectors.toSet());
        return backdownDao.findAll().stream()
                .filter(b -> ids.contains(b.getCustomerId()))
                .collect(Collectors.toList());
    }

    public boolean auditBackdown(Integer backdownId, boolean approved, String auditorName) {
        Optional<BackDown> opt = backdownDao.findById(backdownId);
        if (opt.isEmpty()) return false;
        BackDown backdown = opt.get();
        if (backdown.getAuditstatus() != 0) return false;
        if (approved) {
            backdown.setAuditstatus(1);
            customerDao.findById(backdown.getCustomerId()).ifPresent(customer -> {
                if (customer.getBedId() != null) {
                    bedDao.findById(customer.getBedId()).ifPresent(bed -> {
                        bed.setBedStatus(1);
                        bedDao.update(bed);
                    });
                }
                customerDao.deleteById(customer.getId());
                List<BedDetails> details = bedDetailsDao.findAll().stream()
                        .filter(d -> d.getCustomerId().equals(customer.getId()) && d.getEndDate() == null && d.getIsDeleted() == 0)
                        .collect(Collectors.toList());
                for (BedDetails d : details) {
                    d.setEndDate(DateUtils.now());
                    bedDetailsDao.update(d);
                }
            });
        } else {
            backdown.setAuditstatus(2);
        }
        backdown.setAuditperson(auditorName);
        backdown.setAudittime(DateUtils.now());
        backdownDao.update(backdown);
        return true;
    }
}