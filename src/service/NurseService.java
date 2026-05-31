package service;

import dao.*;
import pojo.*;
import util.DateUtils;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 护理服务，负责护理项目与等级管理、客户护理套餐配置及护理执行与记录。
 */
public class NurseService {
    private final NurseContentDao contentDao = new NurseContentDao();
    private final NurseLevelDao levelDao = new NurseLevelDao();
    private final NurseLevelItemDao levelItemDao = new NurseLevelItemDao();
    private final CustomerNurseItemDao customerNurseItemDao = new CustomerNurseItemDao();
    private final NurseRecordDao nurseRecordDao = new NurseRecordDao();
    private final CustomerDao customerDao = new CustomerDao();

    /**
     * 查询所有护理项目。
     *
     * @return 护理项目列表
     */
    public List<NurseContent> findAllNurseContents() { return contentDao.findAll(); }

    /**
     * 按状态筛选护理项目。
     *
     * @param status 项目状态
     * @return 匹配状态的护理项目列表
     */
    public List<NurseContent> findNurseContentsByStatus(Integer status) {
        return contentDao.findAll().stream().filter(c -> c.getStatus().equals(status)).collect(Collectors.toList());
    }

    /**
     * 按护理项目名称关键字模糊查询。
     *
     * @param keyword 名称关键字，为空时返回全部项目
     * @return 匹配的护理项目列表
     */
    public List<NurseContent> findNurseContentsByName(String keyword) {
        if (keyword == null || keyword.isEmpty()) return findAllNurseContents();
        return contentDao.findAll().stream().filter(c -> c.getNursingName().contains(keyword)).collect(Collectors.toList());
    }

    /**
     * 新增护理项目。
     *
     * @param content 待新增的护理项目
     * @return 新增后的护理项目对象
     */
    public NurseContent addNurseContent(NurseContent content) { return contentDao.insert(content); }

    /**
     * 更新护理项目；若项目由启用变为停用，则自动从各等级中移除关联。
     *
     * @param content 待更新的护理项目
     * @return 更新成功返回 true，项目不存在时返回 false
     */
    public boolean updateNurseContent(NurseContent content) {
        if (contentDao.findById(content.getId()).isEmpty()) return false;
        NurseContent old = contentDao.findById(content.getId()).get();
        if (old.getStatus() == 1 && content.getStatus() == 2) {
            levelItemDao.findAll().stream()
                    .filter(item -> item.getItemId().equals(content.getId()))
                    .forEach(item -> levelItemDao.deleteById(item.getId()));
        }
        contentDao.update(content);
        return true;
    }

    /**
     * 删除指定护理项目。
     *
     * @param id 护理项目 ID
     * @return 删除成功返回 true，否则返回 false
     */
    public boolean deleteNurseContent(Integer id) { return contentDao.deleteById(id); }

    /**
     * 查询所有护理等级。
     *
     * @return 护理等级列表
     */
    public List<NurseLevel> findAllNurseLevels() { return levelDao.findAll(); }

    /**
     * 按状态筛选护理等级。
     *
     * @param status 等级状态
     * @return 匹配状态的护理等级列表
     */
    public List<NurseLevel> findNurseLevelsByStatus(Integer status) {
        return levelDao.findAll().stream().filter(l -> l.getLevelStatus().equals(status)).collect(Collectors.toList());
    }

    /**
     * 新增护理等级。
     *
     * @param level 待新增的护理等级
     * @return 新增后的护理等级对象
     */
    public NurseLevel addNurseLevel(NurseLevel level) { return levelDao.insert(level); }

    /**
     * 更新护理等级信息。
     *
     * @param level 待更新的护理等级
     * @return 更新成功返回 true，等级不存在时返回 false
     */
    public boolean updateNurseLevel(NurseLevel level) {
        if (levelDao.findById(level.getId()).isEmpty()) return false;
        levelDao.update(level);
        return true;
    }

    /**
     * 删除护理等级及其下所有等级-项目关联。
     *
     * @param id 护理等级 ID
     * @return 删除成功返回 true，否则返回 false
     */
    public boolean deleteNurseLevel(Integer id) {
        levelItemDao.findAll().stream().filter(item -> item.getLevelId().equals(id))
                .forEach(item -> levelItemDao.deleteById(item.getId()));
        return levelDao.deleteById(id);
    }

    /**
     * 查询指定护理等级包含的所有护理项目。
     *
     * @param levelId 护理等级 ID
     * @return 该等级下的护理项目列表
     */
    public List<NurseContent> getItemsByLevelId(Integer levelId) {
        Set<Integer> itemIds = levelItemDao.findAll().stream()
                .filter(item -> item.getLevelId().equals(levelId))
                .map(NurseLevelItem::getItemId).collect(Collectors.toSet());
        return contentDao.findAll().stream().filter(c -> itemIds.contains(c.getId())).collect(Collectors.toList());
    }

    /**
     * 向护理等级添加护理项目。
     *
     * @param levelId 护理等级 ID
     * @param itemId  护理项目 ID
     * @return 添加成功返回 true，关联已存在时返回 false
     */
    public boolean addItemToLevel(Integer levelId, Integer itemId) {
        boolean exists = levelItemDao.findAll().stream()
                .anyMatch(item -> item.getLevelId().equals(levelId) && item.getItemId().equals(itemId));
        if (exists) return false;
        NurseLevelItem item = new NurseLevelItem();
        item.setLevelId(levelId);
        item.setItemId(itemId);
        levelItemDao.insert(item);
        return true;
    }

    /**
     * 从护理等级中移除指定护理项目。
     *
     * @param levelId 护理等级 ID
     * @param itemId  护理项目 ID
     * @return 移除成功返回 true，关联不存在时返回 false
     */
    public boolean removeItemFromLevel(Integer levelId, Integer itemId) {
        Optional<NurseLevelItem> opt = levelItemDao.findAll().stream()
                .filter(item -> item.getLevelId().equals(levelId) && item.getItemId().equals(itemId))
                .findFirst();
        return opt.map(item -> levelItemDao.deleteById(item.getId())).orElse(false);
    }

    /**
     * 为客户设置护理等级，并自动生成对应护理套餐项（有效期三个月）。
     *
     * @param customerId 客户 ID
     * @param levelId    护理等级 ID
     * @return 设置成功返回 true，客户不存在或已有护理等级时返回 false
     */
    public boolean setCustomerLevel(Integer customerId, Integer levelId) {
        Optional<Customer> opt = customerDao.findById(customerId);
        if (opt.isEmpty()) return false;
        Customer customer = opt.get();
        if (customer.getLevelId() != null) return false;
        List<NurseContent> items = getItemsByLevelId(levelId);
        Date buy = DateUtils.now();
        Date maturity = DateUtils.addMonths(buy, 3);
        for (NurseContent item : items) {
            CustomerNurseItem cni = new CustomerNurseItem();
            cni.setCustomerId(customerId);
            cni.setItemId(item.getId());
            cni.setLevelId(levelId);
            cni.setNurseNumber(1);
            cni.setBuyTime(buy);
            cni.setMaturityTime(maturity);
            cni.setIsDeleted(0);
            customerNurseItemDao.insert(cni);
        }
        customer.setLevelId(levelId);
        customerDao.update(customer);
        return true;
    }

    /**
     * 移除客户的护理等级及其关联的等级套餐项。
     *
     * @param customerId 客户 ID
     * @return 移除成功返回 true，客户不存在或未设置等级时返回 false
     */
    public boolean removeCustomerLevel(Integer customerId) {
        Optional<Customer> opt = customerDao.findById(customerId);
        if (opt.isEmpty()) return false;
        Customer customer = opt.get();
        if (customer.getLevelId() == null) return false;
        customerNurseItemDao.findAll().stream()
                .filter(cni -> cni.getCustomerId().equals(customerId) && cni.getLevelId().equals(customer.getLevelId()))
                .forEach(cni -> customerNurseItemDao.deleteById(cni.getId()));
        customer.setLevelId(null);
        customerDao.update(customer);
        return true;
    }

    /**
     * 查询客户拥有的所有护理套餐项。
     *
     * @param customerId 客户 ID
     * @return 客户护理套餐项列表
     */
    public List<CustomerNurseItem> getCustomerNurseItems(Integer customerId) {
        return customerNurseItemDao.findAll().stream()
                .filter(cni -> cni.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    /**
     * 为客户单独购买护理项目（非等级套餐）。
     *
     * @param customerId   客户 ID
     * @param itemId       护理项目 ID
     * @param quantity     购买次数
     * @param maturityTime 到期时间
     * @return 购买成功返回 true，客户已拥有该项目时返回 false
     */
    public boolean purchaseNurseItem(Integer customerId, Integer itemId, Integer quantity, Date maturityTime) {
        boolean exists = customerNurseItemDao.findAll().stream()
                .anyMatch(cni -> cni.getCustomerId().equals(customerId) && cni.getItemId().equals(itemId));
        if (exists) return false;
        CustomerNurseItem cni = new CustomerNurseItem();
        cni.setCustomerId(customerId);
        cni.setItemId(itemId);
        cni.setLevelId(null);
        cni.setNurseNumber(quantity);
        cni.setBuyTime(DateUtils.now());
        cni.setMaturityTime(maturityTime);
        cni.setIsDeleted(0);
        customerNurseItemDao.insert(cni);
        return true;
    }

    /**
     * 续费客户护理套餐项，增加可用次数并可延长到期时间。
     *
     * @param customerNurseItemId  客户护理套餐项 ID
     * @param additionalQuantity   追加次数
     * @param newMaturityTime      新的到期时间，可为 null 表示不修改
     * @return 续费成功返回 true，套餐项不存在时返回 false
     */
    public boolean renewNurseItem(Integer customerNurseItemId, Integer additionalQuantity, Date newMaturityTime) {
        Optional<CustomerNurseItem> opt = customerNurseItemDao.findById(customerNurseItemId);
        if (opt.isEmpty()) return false;
        CustomerNurseItem cni = opt.get();
        cni.setNurseNumber(cni.getNurseNumber() + additionalQuantity);
        if (newMaturityTime != null) cni.setMaturityTime(newMaturityTime);
        customerNurseItemDao.update(cni);
        return true;
    }

    /**
     * 删除客户护理套餐项。
     *
     * @param customerNurseItemId 客户护理套餐项 ID
     * @return 删除成功返回 true，否则返回 false
     */
    public boolean removeCustomerNurseItem(Integer customerNurseItemId) {
        return customerNurseItemDao.deleteById(customerNurseItemId);
    }

    /**
     * 执行护理操作，扣减可用次数并生成护理记录。
     *
     * @param customerId   客户 ID
     * @param itemId       护理项目 ID
     * @param nursingCount 本次护理次数
     * @param userId       执行护理的用户 ID
     * @return 执行成功返回 true，套餐项不存在、次数不足或已过期时返回 false
     */
    public boolean performNursing(Integer customerId, Integer itemId, Integer nursingCount, Integer userId) {
        Optional<CustomerNurseItem> optCni = customerNurseItemDao.findAll().stream()
                .filter(cni -> cni.getCustomerId().equals(customerId) && cni.getItemId().equals(itemId))
                .findFirst();
        if (optCni.isEmpty()) return false;
        CustomerNurseItem cni = optCni.get();
        if (cni.getNurseNumber() < nursingCount) return false;
        if (cni.getMaturityTime().before(DateUtils.now())) return false;
        cni.setNurseNumber(cni.getNurseNumber() - nursingCount);
        customerNurseItemDao.update(cni);

        NurseRecord record = new NurseRecord();
        record.setCustomerId(customerId);
        record.setItemId(itemId);
        record.setNursingTime(DateUtils.now());
        record.setNursingCount(nursingCount);
        record.setUserId(userId);
        record.setIsDeleted(0);
        contentDao.findById(itemId).ifPresent(c -> record.setNursingContent(c.getNursingName()));
        nurseRecordDao.insert(record);
        return true;
    }

    /**
     * 查询护理记录，可按客户筛选。
     *
     * @param customerId 客户 ID，为 null 时返回全部记录
     * @return 护理记录列表
     */
    public List<NurseRecord> getNurseRecordsByCustomer(Integer customerId) {
        if (customerId == null) {
            return nurseRecordDao.findAll(); // 返回所有未删除的记录
        }
        return nurseRecordDao.findAll().stream()
                .filter(r -> r.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    /**
     * 删除指定护理记录。
     *
     * @param recordId 护理记录 ID
     * @return 删除成功返回 true，否则返回 false
     */
    public boolean deleteNurseRecord(Integer recordId) {
        return nurseRecordDao.deleteById(recordId);
    }

    /**
     * 查询指定管家执行的所有护理记录。
     *
     * @param housekeeperId 管家用户 ID
     * @return 该管家的护理记录列表
     */
    public List<NurseRecord> getNurseRecordsByHousekeeper(Integer housekeeperId) {
        return nurseRecordDao.findByUserId(housekeeperId); // 假设 NurseRecordDao 有 findByUserId 方法
    }
}
