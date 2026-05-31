package dao;

import pojo.BedDetails;
import java.util.List;

/**
 * 床位详情数据访问对象
 * 提供床位使用记录相关的数据库操作，包括根据客户ID查询使用记录等
 */

public class BedDetailsDao extends BaseDaoImpl<BedDetails, Integer> {

    /**
     * 构造函数
     * 初始化床位详情 DAO，指定表名、主键列名、实体类类型，使用逻辑删除
     */

    public BedDetailsDao() {
        super("beddetails", "id", BedDetails.class);
    }

    /**
     * 根据客户ID查询床位使用记录
     *
     * @param customerId 客户ID
     * @return 该客户的所有床位使用记录列表（不包括已删除的记录）
     */

    public List<BedDetails> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM beddetails WHERE customer_id = ? AND is_deleted = 0";
        return executeQuery(sql, customerId);
    }

    /**
     * 查询客户当前正在使用的床位记录
     * 返回结束日期为空的记录，表示客户当前正在使用的床位
     *
     * @param customerId 客户ID
     * @return 该客户当前正在使用的床位记录列表
     */

    public List<BedDetails> findCurrentByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM beddetails WHERE customer_id = ? AND end_date IS NULL AND is_deleted = 0";
        return executeQuery(sql, customerId);
    }
}