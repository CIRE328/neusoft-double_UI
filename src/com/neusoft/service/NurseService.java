package com.neusoft.service;

import com.neusoft.dao.*;
import com.neusoft.pojo.*;
import com.neusoft.util.DateUtils;

import java.util.*;
import java.util.stream.Collectors;

public class NurseService {
    private final NurseContentDao contentDao = new NurseContentDao();
    private final NurseLevelDao levelDao = new NurseLevelDao();
    private final NurseLevelItemDao levelItemDao = new NurseLevelItemDao();
    private final CustomerNurseItemDao customerNurseItemDao = new CustomerNurseItemDao();
    private final NurseRecordDao nurseRecordDao = new NurseRecordDao();
    private final CustomerDao customerDao = new CustomerDao();

    public List<NurseContent> findAllNurseContents() { return contentDao.findAll(); }
    public List<NurseContent> findNurseContentsByStatus(Integer status) {
        return contentDao.findAll().stream().filter(c -> c.getStatus().equals(status)).collect(Collectors.toList());
    }
    public List<NurseContent> findNurseContentsByName(String keyword) {
        if (keyword == null || keyword.isEmpty()) return findAllNurseContents();
        return contentDao.findAll().stream().filter(c -> c.getNursingName().contains(keyword)).collect(Collectors.toList());
    }
    public NurseContent addNurseContent(NurseContent content) { return contentDao.insert(content); }
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
    public boolean deleteNurseContent(Integer id) { return contentDao.deleteById(id); }

    public List<NurseLevel> findAllNurseLevels() { return levelDao.findAll(); }
    public List<NurseLevel> findNurseLevelsByStatus(Integer status) {
        return levelDao.findAll().stream().filter(l -> l.getLevelStatus().equals(status)).collect(Collectors.toList());
    }
    public NurseLevel addNurseLevel(NurseLevel level) { return levelDao.insert(level); }
    public boolean updateNurseLevel(NurseLevel level) {
        if (levelDao.findById(level.getId()).isEmpty()) return false;
        levelDao.update(level);
        return true;
    }
    public boolean deleteNurseLevel(Integer id) {
        levelItemDao.findAll().stream().filter(item -> item.getLevelId().equals(id))
                .forEach(item -> levelItemDao.deleteById(item.getId()));
        return levelDao.deleteById(id);
    }

    public List<NurseContent> getItemsByLevelId(Integer levelId) {
        Set<Integer> itemIds = levelItemDao.findAll().stream()
                .filter(item -> item.getLevelId().equals(levelId))
                .map(NurseLevelItem::getItemId).collect(Collectors.toSet());
        return contentDao.findAll().stream().filter(c -> itemIds.contains(c.getId())).collect(Collectors.toList());
    }
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
    public boolean removeItemFromLevel(Integer levelId, Integer itemId) {
        Optional<NurseLevelItem> opt = levelItemDao.findAll().stream()
                .filter(item -> item.getLevelId().equals(levelId) && item.getItemId().equals(itemId))
                .findFirst();
        return opt.map(item -> levelItemDao.deleteById(item.getId())).orElse(false);
    }

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

    public List<CustomerNurseItem> getCustomerNurseItems(Integer customerId) {
        return customerNurseItemDao.findAll().stream()
                .filter(cni -> cni.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }
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
    public boolean renewNurseItem(Integer customerNurseItemId, Integer additionalQuantity, Date newMaturityTime) {
        Optional<CustomerNurseItem> opt = customerNurseItemDao.findById(customerNurseItemId);
        if (opt.isEmpty()) return false;
        CustomerNurseItem cni = opt.get();
        cni.setNurseNumber(cni.getNurseNumber() + additionalQuantity);
        if (newMaturityTime != null) cni.setMaturityTime(newMaturityTime);
        customerNurseItemDao.update(cni);
        return true;
    }
    public boolean removeCustomerNurseItem(Integer customerNurseItemId) {
        return customerNurseItemDao.deleteById(customerNurseItemId);
    }

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

    public List<NurseRecord> getNurseRecordsByCustomer(Integer customerId) {
        return nurseRecordDao.findAll().stream()
                .filter(r -> r.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }
    public boolean deleteNurseRecord(Integer recordId) {
        return nurseRecordDao.deleteById(recordId);
    }
}