package dao;

import pojo.Customer;
import java.util.List;
import java.util.Optional;

/**
 * 客户数据访问对象
 * 提供客户相关的数据库操作，包括根据姓名模糊查询、根据护理级别查询、根据健康管家查询等
 */

public class CustomerDao extends BaseDaoImpl<Customer, Integer> {

    /**
     * 构造函数
     * 初始化客户 DAO，指定表名、主键列名、实体类类型，使用逻辑删除
     */

    public CustomerDao() {
        super("customer", "id", Customer.class);
    }

    /**
     * 根据姓名模糊查询客户
     *
     * @param keyword 查询关键字
     * @return 姓名包含关键字的客户列表
     */

    public List<Customer> findByNameLike(String keyword) {
        String sql = "SELECT * FROM customer WHERE customer_name LIKE ? AND is_deleted = 0";
        return executeQuery(sql, "%" + keyword + "%");
    }

    /**
     * 根据护理级别ID查询客户
     *
     * @param levelId 护理级别ID
     * @return 该护理级别的所有客户列表
     */

    public List<Customer> findByLevelId(Integer levelId) {
        String sql = "SELECT * FROM customer WHERE level_id = ? AND is_deleted = 0";
        return executeQuery(sql, levelId);
    }

    /**
     * 根据健康管家ID查询客户
     *
     * @param userId 健康管家ID
     * @return 该健康管家负责的所有客户列表
     */

    public List<Customer> findByUserId(Integer userId) {
        String sql = "SELECT * FROM customer WHERE user_id = ? AND is_deleted = 0";
        return executeQuery(sql, userId);
    }

    /**
     * 查询无管家的客户
     * 返回 user_id 为 NULL 或 -1 的客户记录
     *
     * @return 无健康管家负责的客户列表
     */
    public List<Customer> findWithoutHousekeeper() {
        String sql = "SELECT * FROM customer WHERE (user_id IS NULL OR user_id = -1) AND is_deleted = 0";
        return executeQuery(sql);
    }

    /**
     * 根据身份证号查询客户
     * 身份证号是唯一的，最多返回一个客户
     *
     * @param idCard 身份证号
     * @return 包含查询结果的 Optional 对象
     */

    public Optional<Customer> findByIdCard(String idCard) {
        String sql = "SELECT * FROM customer WHERE idcard = ? AND is_deleted = 0";
        List<Customer> list = executeQuery(sql, idCard);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}