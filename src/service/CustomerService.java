package service;

import dao.*;
import pojo.*;
import util.DateUtils;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 客户服务类
 * 提供客户管理相关的业务逻辑功能
 * 包括客户查询、入住办理、信息更新、退住处理、外出管理、退院管理等功能
 */

public class CustomerService {
    private final CustomerDao customerDao = new CustomerDao();
    private final BedDao bedDao = new BedDao();
    private final BedDetailsDao bedDetailsDao = new BedDetailsDao();
    private final OutwardDao outwardDao = new OutwardDao();
    private final BackdownDao backdownDao = new BackdownDao();

    /**
     * 查询所有客户信息
     *
     * @return 客户列表
     */

    public List<Customer> findAllCustomers() { return customerDao.findAll(); }

    /**
     * 根据客户姓名模糊查询客户信息
     *
     * @param keyword 客户姓名关键词
     * @return 客户列表
     */

    public List<Customer> findCustomersByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return findAllCustomers();
        return customerDao.findAll().stream()
                .filter(c -> c.getCustomerName() != null && c.getCustomerName().contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * 根据客户类型查询客户信息
     *
     * @param type 客户类型（"自理老人"、"护理老人"或其他）
     * @return 客户列表
     */

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

    /**
     * 根据ID查询客户信息
     *
     * @param id 客户ID
     * @return 包含客户的Optional对象
     */

    public Optional<Customer> findCustomerById(Integer id) { return customerDao.findById(id); }

    /**
     * 办理客户入住
     * 验证入住日期和床位状态，创建客户记录和床位使用记录，更新床位状态
     *
     * @param customer 客户信息
     * @param bedId 床位ID
     * @return 是否入住成功
     */

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
        customer.setBuildingNo("606");          // 楼栋固定606
        customer.setBedId(bedId);
        customer.setRoomNo(String.valueOf(bed.getRoomNo()));  // 设置房间号
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

    /**
     * 更新客户信息
     * 如果合同到期时间发生变化，同步更新床位使用记录的结束日期
     *
     * @param customer 客户信息
     * @return 是否更新成功
     */

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

    /**
     * 删除客户信息
     * 释放床位，更新床位使用记录的结束日期，逻辑删除客户
     *
     * @param customerId 客户ID
     * @return 是否删除成功
     */

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

    /**
     * 提交外出申请
     * 验证客户存在性，设置审核状态为待审核
     *
     * @param outward 外出申请信息
     * @return 是否提交成功
     */

    public boolean submitOutward(Outward outward) {
        if (customerDao.findById(outward.getCustomerId()).isEmpty()) return false;
        outward.setAuditstatus(0);
        outward.setIsDeleted(0);
        outwardDao.insert(outward);
        return true;
    }

    /**
     * 查询所有外出申请记录
     *
     * @return 外出申请列表
     */

    public List<Outward> findAllOutwards() { return outwardDao.findAll(); }

    /**
     * 根据客户姓名查询外出申请记录
     *
     * @param keyword 客户姓名关键词
     * @return 外出申请列表
     */

    public List<Outward> findOutwardsByCustomerName(String keyword) {
        List<Customer> customers = findCustomersByName(keyword);
        if (customers.isEmpty()) return List.of();
        Set<Integer> ids = customers.stream().map(Customer::getId).collect(Collectors.toSet());
        return outwardDao.findAll().stream()
                .filter(o -> ids.contains(o.getCustomerId()))
                .collect(Collectors.toList());
    }

    /**
     * 审核外出申请
     * 审核通过则更新床位状态为外出，审核不通过则标记为拒绝
     *
     * @param outwardId 外出申请ID
     * @param approved 是否通过审核
     * @param auditorName 审核人姓名
     * @return 是否审核成功
     */

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

    /**
     * 外出归来登记
     * 更新实际归来时间，恢复床位状态为占用
     *
     * @param outwardId 外出申请ID
     * @param actualReturnTime 实际归来时间
     * @return 是否登记成功
     */

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

    /**
     * 提交退住申请
     * 验证客户存在性，设置审核状态为待审核
     *
     * @param backdown 退住申请信息
     * @return 是否提交成功
     */

    public boolean submitBackdown(BackDown backdown) {
        if (customerDao.findById(backdown.getCustomerId()).isEmpty()) return false;
        backdown.setAuditstatus(0);
        backdown.setIsDeleted(0);
        backdownDao.insert(backdown);
        return true;
    }

    /**
     * 查询所有退院申请记录
     *
     * @return 退住申请列表
     */

    public List<BackDown> findAllBackdowns() { return backdownDao.findAll(); }

    /**
     * 根据客户姓名查询退院申请记录
     *
     * @param keyword 客户姓名关键词
     * @return 退院申请列表
     */

    public List<BackDown> findBackdownsByCustomerName(String keyword) {
        List<Customer> customers = findCustomersByName(keyword);
        if (customers.isEmpty()) return List.of();
        Set<Integer> ids = customers.stream().map(Customer::getId).collect(Collectors.toSet());
        return backdownDao.findAll().stream()
                .filter(b -> ids.contains(b.getCustomerId()))
                .collect(Collectors.toList());
    }

    /**
     * 审核退住申请
     * 审核通过则释放床位、删除客户、更新床位使用记录，审核不通过则标记为拒绝
     *
     * @param backdownId 退住申请ID
     * @param approved 是否通过审核
     * @param auditorName 审核人姓名
     * @return 是否审核成功
     */

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