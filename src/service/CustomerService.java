package service;

import dao.*;
import pojo.*;
import util.DateUtils;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 客户管理服务，负责客户入住、信息维护、外出申请与退住申请及其审核流程。
 */
public class CustomerService {
    private final CustomerDao customerDao = new CustomerDao();
    private final BedDao bedDao = new BedDao();
    private final BedDetailsDao bedDetailsDao = new BedDetailsDao();
    private final OutwardDao outwardDao = new OutwardDao();
    private final BackdownDao backdownDao = new BackdownDao();

    /**
     * 查询所有客户。
     *
     * @return 客户列表
     */
    public List<Customer> findAllCustomers() { return customerDao.findAll(); }

    /**
     * 按客户姓名关键字模糊查询。
     *
     * @param keyword 姓名关键字，为空时返回全部客户
     * @return 匹配的客户列表
     */
    public List<Customer> findCustomersByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return findAllCustomers();
        return customerDao.findAll().stream()
                .filter(c -> c.getCustomerName() != null && c.getCustomerName().contains(keyword))
                .collect(Collectors.toList());
    }

    /**
     * 按老人类型筛选客户（自理老人或护理老人）。
     *
     * @param type 老人类型（"自理老人"、"护理老人"），其他值返回全部
     * @return 匹配类型的客户列表
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
     * 根据 ID 查询客户。
     *
     * @param id 客户 ID
     * @return 存在则返回客户，否则为空
     */
    public Optional<Customer> findCustomerById(Integer id) { return customerDao.findById(id); }

    /**
     * 办理客户入住，支持新客户登记与已有客户更新入住信息。
     *
     * @param customer 客户信息（含入住日期、合同到期日等）
     * @param bedId    分配的床位 ID
     * @return 入住成功返回 true，校验失败或床位不可用时返回 false
     */
    public boolean checkin(Customer customer, Integer bedId) {
        // 1. 校验合同时间
        if (DateUtils.isAfter(customer.getCheckinDate(), customer.getExpirationDate())) {
            System.err.println("合同到期时间不能小于入住时间");
            return false;
        }
        // 2. 校验床位
        Optional<Bed> optBed = bedDao.findById(bedId);
        if (optBed.isEmpty() || optBed.get().getBedStatus() != 1) {
            System.err.println("床位不存在或不是空闲状态");
            return false;
        }
        Bed bed = optBed.get();

        // 3. 计算年龄
        if (customer.getBirthday() != null) {
            customer.setCustomerAge(DateUtils.calculateAge(customer.getBirthday()));
        }
        customer.setBuildingNo("606");   // 楼栋固定

        Customer savedCustomer;
        // 4. 判断是新增客户还是已有客户办理入住
        if (customer.getId() == null || customer.getId() <= 0) {
            // 新客户：插入客户记录
            if (customer.getUserId() == null) customer.setUserId(-1);
            customer.setIsDeleted(0);
            savedCustomer = customerDao.insert(customer);
        } else {
            // 已有客户：更新入住信息，不新增记录
            Optional<Customer> existingOpt = customerDao.findById(customer.getId());
            if (existingOpt.isEmpty()) {
                System.err.println("客户不存在");
                return false;
            }
            Customer existing = existingOpt.get();
            existing.setCheckinDate(customer.getCheckinDate());
            existing.setExpirationDate(customer.getExpirationDate());
            existing.setBedId(bedId);
            existing.setRoomNo(String.valueOf(bed.getRoomNo()));
            existing.setBuildingNo("606");
            existing.setCustomerAge(customer.getCustomerAge());
            existing.setBloodType(customer.getBloodType());
            existing.setFamilyMember(customer.getFamilyMember());
            existing.setContactTel(customer.getContactTel());
            // 注意：不要覆盖原有的 levelId, userId 等其他字段
            customerDao.update(existing);
            savedCustomer = existing;
        }

        // 5. 更新床位状态为有人
        bed.setBedStatus(2);
        bedDao.update(bed);

        // 6. 插入床位使用记录（新记录，结束时间为空）
        BedDetails details = new BedDetails();
        details.setStartDate(customer.getCheckinDate());
        details.setEndDate(null);
        details.setCustomerId(savedCustomer.getId());
        details.setBedId(bedId);
        details.setIsDeleted(0);
        bedDetailsDao.insert(details);

        return true;
    }

    /**
     * 更新客户信息；若合同到期日变更，同步结束当前床位使用记录。
     *
     * @param customer 待更新的客户对象
     * @return 更新成功返回 true，客户不存在时返回 false
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
     * 删除客户，释放床位并结束当前床位使用记录。
     *
     * @param customerId 客户 ID
     * @return 删除成功返回 true，客户不存在时返回 false
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
     * 提交客户外出申请。
     *
     * @param outward 外出申请信息
     * @return 提交成功返回 true，客户不存在时返回 false
     */
    public boolean submitOutward(Outward outward) {
        if (customerDao.findById(outward.getCustomerId()).isEmpty()) return false;
        outward.setAuditstatus(0);
        outward.setIsDeleted(0);
        outwardDao.insert(outward);
        return true;
    }

    /**
     * 查询所有外出申请。
     *
     * @return 外出申请列表
     */
    public List<Outward> findAllOutwards() { return outwardDao.findAll(); }

    /**
     * 按客户姓名关键字查询外出申请。
     *
     * @param keyword 客户姓名关键字
     * @return 匹配的外出申请列表，无匹配客户时返回空列表
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
     * 审核外出申请；通过后床位状态变为外出。
     *
     * @param outwardId   外出申请 ID
     * @param approved    是否通过
     * @param auditorName 审核人姓名
     * @return 审核成功返回 true，申请不存在或已审核时返回 false
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
     * 登记客户外出归来，恢复床位为占用状态。
     *
     * @param outwardId        外出申请 ID
     * @param actualReturnTime 实际归来时间
     * @return 登记成功返回 true，申请不存在或未通过审核时返回 false
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
     * 提交客户退住申请。
     *
     * @param backdown 退住申请信息
     * @return 提交成功返回 true，客户不存在时返回 false
     */
    public boolean submitBackdown(BackDown backdown) {
        if (customerDao.findById(backdown.getCustomerId()).isEmpty()) return false;
        backdown.setAuditstatus(0);
        backdown.setIsDeleted(0);
        backdownDao.insert(backdown);
        return true;
    }

    /**
     * 查询所有退住申请。
     *
     * @return 退住申请列表
     */
    public List<BackDown> findAllBackdowns() { return backdownDao.findAll(); }

    /**
     * 按客户姓名关键字查询退住申请。
     *
     * @param keyword 客户姓名关键字
     * @return 匹配的退住申请列表，无匹配客户时返回空列表
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
     * 审核退住申请；通过后释放床位、结束使用记录并删除客户。
     *
     * @param backdownId  退住申请 ID
     * @param approved    是否通过
     * @param auditorName 审核人姓名
     * @return 审核成功返回 true，申请不存在或已审核时返回 false
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
