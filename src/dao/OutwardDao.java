package dao;

import pojo.Outward;
import java.util.List;

/**
 * 外出申请数据访问对象
 * 提供客户外出申请相关的数据库操作
 */

public class OutwardDao extends BaseDaoImpl<Outward, Integer> {

    /**
     * 构造函数
     * 初始化外出申请 DAO，指定表名、主键列名、实体类类型，使用逻辑删除
     */

    public OutwardDao() {
        super("outward", "id", Outward.class);
    }

    /**
     * 根据客户ID查询外出申请
     *
     * @param customerId 客户ID
     * @return 该客户的所有外出申请列表
     */

    public List<Outward> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM outward WHERE customer_id = ? AND is_deleted = 0";
        return executeQuery(sql, customerId);
    }

    /**
     * 根据审批状态查询外出申请
     *
     * @param auditStatus 审批状态（0 已提交，1 同意，2 拒绝）
     * @return 符合审批状态的外出申请列表
     */

    public List<Outward> findByAuditStatus(Integer auditStatus) {
        String sql = "SELECT * FROM outward WHERE audit_status = ? AND is_deleted = 0";
        return executeQuery(sql, auditStatus);
    }
}
