package com.neusoft.dao;

import com.neusoft.pojo.CustomerNurseItem;
import java.util.List;

public class CustomerNurseItemDao extends BaseDaoImpl<CustomerNurseItem, Integer> {

    public CustomerNurseItemDao() {
        super("customer_nurse_item", "id", CustomerNurseItem.class);
    }

    //根据客户ID查询其所有护理项目
    public List<CustomerNurseItem> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM customer_nurse_item WHERE customer_id = ? AND is_deleted = 0";
        return executeQuery(sql, customerId);
    }

    //根据客户和项目ID查询
    public List<CustomerNurseItem> findByCustomerAndItem(Integer customerId, Integer itemId) {
        String sql = "SELECT * FROM customer_nurse_item WHERE customer_id = ? AND item_id = ? AND is_deleted = 0";
        return executeQuery(sql, customerId, itemId);
    }
}
