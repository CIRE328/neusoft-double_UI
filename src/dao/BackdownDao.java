package dao;

import pojo.BackDown;
import java.util.List;

/**
 * 退住申请数据访问对象
 * 健康管家可以为自己服务的客户提供退住申请
 * 继承自BaseDaoImpl，提供退住申请相关的数据库操作方法1
 */

public class BackdownDao extends BaseDaoImpl<BackDown, Integer> {

    /**
     * 构造函数
     * 初始化数据表名称、主键字段和实体类类型
     */

    public BackdownDao() {
        super("backdown", "id", BackDown.class);
    }

    /**
     * 根据客户ID查询退住申请
     * @param customerId 客户ID
     * @return 退住申请列表
     */

    public List<BackDown> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM backdown WHERE customer_id = ? AND is_deleted = 0";
        return executeQuery(sql, customerId);
    }

    /**
     * 根据审批状态查询退住申请
     * @param auditStatus 审批状态
     * @return 退住申请列表
     */

    public List<BackDown> findByAuditStatus(Integer auditStatus) {
        String sql = "SELECT * FROM backdown WHERE audit_status = ? AND is_deleted = 0";
        return executeQuery(sql, auditStatus);
    }
}