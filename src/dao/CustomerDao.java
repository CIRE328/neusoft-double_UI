package dao;

import pojo.Customer;
import java.util.List;
import java.util.Optional;

public class CustomerDao extends BaseDaoImpl<Customer, Integer> {

    public CustomerDao() {
        super("customer", "id", Customer.class);
    }

    //根据姓名模糊查询
    public List<Customer> findByNameLike(String keyword) {
        String sql = "SELECT * FROM customer WHERE customer_name LIKE ? AND is_deleted = 0";
        return executeQuery(sql, "%" + keyword + "%");
    }

    //根据护理级别ID查询客户
    public List<Customer> findByLevelId(Integer levelId) {
        String sql = "SELECT * FROM customer WHERE level_id = ? AND is_deleted = 0";
        return executeQuery(sql, levelId);
    }

    //根据健康管家ID查询客户
    public List<Customer> findByUserId(Integer userId) {
        String sql = "SELECT * FROM customer WHERE user_id = ? AND is_deleted = 0";
        return executeQuery(sql, userId);
    }

    //询无管家的客户 (user_id IS NULL OR user_id = -1)
    public List<Customer> findWithoutHousekeeper() {
        String sql = "SELECT * FROM customer WHERE (user_id IS NULL OR user_id = -1) AND is_deleted = 0";
        return executeQuery(sql);
    }

    //根据身份证号查询（唯一）
    public Optional<Customer> findByIdCard(String idCard) {
        String sql = "SELECT * FROM customer WHERE idcard = ? AND is_deleted = 0";
        List<Customer> list = executeQuery(sql, idCard);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}