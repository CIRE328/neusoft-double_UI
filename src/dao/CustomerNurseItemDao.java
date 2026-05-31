package dao;

import pojo.CustomerNurseItem;
import java.util.List;

/**
 * 客户护理项目数据访问对象
 * 提供客户护理项目相关的数据库操作，包括根据客户ID查询护理项目等
 */

public class CustomerNurseItemDao extends BaseDaoImpl<CustomerNurseItem, Integer> {

    /**
     * 构造函数
     * 初始化客户护理项目 DAO，指定表名、主键列名、实体类类型，使用逻辑删除
     */

    public CustomerNurseItemDao() {
        super("customernurseitem", "id", CustomerNurseItem.class);
    }

    /**
     * 根据客户ID查询其所有护理项目
     *
     * @param customerId 客户ID
     * @return 该客户的所有护理项目列表
     */

    public List<CustomerNurseItem> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM customernurseitem WHERE customer_id = ? AND is_deleted = 0";
        return executeQuery(sql, customerId);
    }

    /**
     * 根据客户ID和项目ID查询护理项目
     *
     * @param customerId 客户ID
     * @param itemId 护理项目ID
     * @return 符合条件的护理项目列表
     */

    public List<CustomerNurseItem> findByCustomerAndItem(Integer customerId, Integer itemId) {
        String sql = "SELECT * FROM customernurseitem WHERE customer_id = ? AND item_id = ? AND is_deleted = 0";
        return executeQuery(sql, customerId, itemId);
    }
}
