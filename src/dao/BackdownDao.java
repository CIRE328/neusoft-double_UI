package dao;

import pojo.BackDown;
import java.util.List;

public class BackdownDao extends BaseDaoImpl<BackDown, Integer> {

    public BackdownDao() {
        super("backdown", "id", BackDown.class);
    }

    //根据客户ID查询退住申请
    public List<BackDown> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM backdown WHERE customer_id = ? AND is_deleted = 0";
        return executeQuery(sql, customerId);
    }

    //根据审批状态查询
    public List<BackDown> findByAuditStatus(Integer auditStatus) {
        String sql = "SELECT * FROM backdown WHERE audit_status = ? AND is_deleted = 0";
        return executeQuery(sql, auditStatus);
    }
}