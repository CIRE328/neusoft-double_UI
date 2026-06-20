package dao;

import pojo.CustomerNurseItem;
import java.util.List;

public class CustomerNurseItemDao extends BaseDaoImpl<CustomerNurseItem, Integer> {

    public CustomerNurseItemDao() {
        super("customernurseitem", "id", CustomerNurseItem.class);
    }

    //根据客户ID查询其所有护理项目
    public List<CustomerNurseItem> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM customernurseitem WHERE customer_id = ? AND is_deleted = 0";
        return executeQuery(sql, customerId);
    }

}
