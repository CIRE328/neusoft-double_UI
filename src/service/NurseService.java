package service;

import dao.*;
import pojo.*;
import util.DateUtils;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 护理服务类
 * 提供护理管理相关的业务逻辑功能
 * 包括护理内容管理、护理等级管理、客户护理项目设置、护理记录等功能
 */

public class NurseService {
    private final NurseContentDao contentDao = new NurseContentDao();
    private final NurseLevelDao levelDao = new NurseLevelDao();
    private final NurseLevelItemDao levelItemDao = new NurseLevelItemDao();
    private final CustomerNurseItemDao customerNurseItemDao = new CustomerNurseItemDao();
    private final NurseRecordDao nurseRecordDao = new NurseRecordDao();
    private final CustomerDao customerDao = new CustomerDao();

    /**
     * 查询所有护理内容
     *
     * @return 护理内容列表
     */

    public List<NurseContent> findAllNurseContents() { return contentDao.findAll(); }

    /**
     * 根据状态查询护理内容
     *
     * @param status 状态（1-启用，2-禁用）
     * @return 护理内容列表
     */

    public List<NurseContent> findNurseContentsByStatus(Integer status) {
        return contentDao.findAll().stream().filter(c -> c.getStatus().equals(status)).collect(Collectors.toList());
    }

    /**
     * 根据护理名称模糊查询护理内容
     *
     * @param keyword 护理名称关键词
     * @return 护理内容列表
     */

    public List<NurseContent> findNurseContentsByName(String keyword) {
        if (keyword == null || keyword.isEmpty()) return findAllNurseContents();
        return contentDao.findAll().stream().filter(c -> c.getNursingName().contains(keyword)).collect(Collectors.toList());
    }

    /**
     * 添加护理内容
     *
     * @param content 护理内容信息
     * @return 添加后的护理内容
     */

    public NurseContent addNurseContent(NurseContent content) { return contentDao.insert(content); }

    /**
     * 更新护理内容
     * 如果从启用状态改为禁用状态，则删除所有护理等级中包含该项目的关联
     *
     * @param content 护理内容信息
     * @return 是否更新成功
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
     * 删除护理内容
     *
     * @param id 护理内容ID
     * @return 是否删除成功
     */

    public boolean deleteNurseContent(Integer id) { return contentDao.deleteById(id); }

    /**
     * 查询所有护理等级
     *
     * @return 护理等级列表
     */

    public List<NurseLevel> findAllNurseLevels() { return levelDao.findAll(); }

    /**
     * 根据状态查询护理等级
     *
     * @param status 状态（1-启用，2-禁用）
     * @return 护理等级列表
     */

    public List<NurseLevel> findNurseLevelsByStatus(Integer status) {
        return levelDao.findAll().stream().filter(l -> l.getLevelStatus().equals(status)).collect(Collectors.toList());
    }

    /**
     * 添加护理等级
     *
     * @param level 护理等级信息
     * @return 添加后的护理等级
     */

    public NurseLevel addNurseLevel(NurseLevel level) { return levelDao.insert(level); }

    /**
     * 更新护理等级
     *
     * @param level 护理等级信息
     * @return 是否更新成功
     */

    public boolean updateNurseLevel(NurseLevel level) {
        if (levelDao.findById(level.getId()).isEmpty()) return false;
        levelDao.update(level);
        return true;
    }

    /**
     * 删除护理等级
     * 同时删除该等级下所有护理项目的关联
     *
     * @param id 护理等级ID
     * @return 是否删除成功
     */

    public boolean deleteNurseLevel(Integer id) {
        levelItemDao.findAll().stream().filter(item -> item.getLevelId().equals(id))
                .forEach(item -> levelItemDao.deleteById(item.getId()));
        return levelDao.deleteById(id);
    }

    /**
     * 根据护理等级ID获取该等级下的所有护理项目
     *
     * @param levelId 护理等级ID
     * @return 护理项目列表
     */

    public List<NurseContent> getItemsByLevelId(Integer levelId) {
        Set<Integer> itemIds = levelItemDao.findAll().stream()
                .filter(item -> item.getLevelId().equals(levelId))
                .map(NurseLevelItem::getItemId).collect(Collectors.toSet());
        return contentDao.findAll().stream().filter(c -> itemIds.contains(c.getId())).collect(Collectors.toList());
    }

    /**
     * 向护理等级添加护理项目
     *
     * @param levelId 护理等级ID
     * @param itemId 护理项目ID
     * @return 是否添加成功
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
     * 从护理等级中移除护理项目
     *
     * @param levelId 护理等级ID
     * @param itemId 护理项目ID
     * @return 是否移除成功
     */

    public boolean removeItemFromLevel(Integer levelId, Integer itemId) {
        Optional<NurseLevelItem> opt = levelItemDao.findAll().stream()
                .filter(item -> item.getLevelId().equals(levelId) && item.getItemId().equals(itemId))
                .findFirst();
        return opt.map(item -> levelItemDao.deleteById(item.getId())).orElse(false);
    }

    /**
     * 为客户设置护理等级
     * 为客户添加该等级下的所有护理项目，默认数量为1，有效期为3个月
     *
     * @param customerId 客户ID
     * @param levelId 护理等级ID
     * @return 是否设置成功
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
     * 移除客户的护理等级
     * 删除该客户在该等级下的所有护理项目
     *
     * @param customerId 客户ID
     * @return 是否移除成功
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
     * 获取客户的护理项目列表
     *
     * @param customerId 客户ID
     * @return 客户护理项目列表
     */

    public List<CustomerNurseItem> getCustomerNurseItems(Integer customerId) {
        return customerNurseItemDao.findAll().stream()
                .filter(cni -> cni.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    /**
     * 为客户购买护理项目
     * 客户不能重复购买相同的护理项目
     *
     * @param customerId 客户ID
     * @param itemId 护理项目ID
     * @param quantity 购买数量
     * @param maturityTime 到期时间
     * @return 是否购买成功
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
     * 续费护理项目
     * 增加护理次数或延长到期时间
     *
     * @param customerNurseItemId 客户护理项目ID
     * @param additionalQuantity 增加的护理次数
     * @param newMaturityTime 新的到期时间（可为null）
     * @return 是否续费成功
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
     * 删除客户的护理项目
     *
     * @param customerNurseItemId 客户护理项目ID
     * @return 是否删除成功
     */

    public boolean removeCustomerNurseItem(Integer customerNurseItemId) {
        return customerNurseItemDao.deleteById(customerNurseItemId);
    }

    /**
     * 执行护理服务
     * 扣减护理次数，记录护理日志
     *
     * @param customerId 客户ID
     * @param itemId 护理项目ID
     * @param nursingCount 护理次数
     * @param userId 护理员ID
     * @return 是否执行成功
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
     * 获取护理记录
     * 如果客户ID为null，则返回所有记录
     *
     * @param customerId 客户ID（可为null）
     * @return 护理记录列表
     */

    public List<NurseRecord> getNurseRecordsByCustomer(Integer customerId) {
        if (customerId == null) {
            return nurseRecordDao.findAll();
        }
        return nurseRecordDao.findAll().stream()
                .filter(r -> r.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    /**
     * 删除护理记录
     *
     * @param recordId 护理记录ID
     * @return 是否删除成功
     */

    public boolean deleteNurseRecord(Integer recordId) {
        return nurseRecordDao.deleteById(recordId);
    }

    public List<NurseRecord> getNurseRecordsByHousekeeper(Integer housekeeperId) {
        return nurseRecordDao.findByUserId(housekeeperId);
    }
}