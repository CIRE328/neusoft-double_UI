package com.neusoft.dao;

import com.neusoft.pojo.Outward;
import java.util.List;

public class OutwardDao extends BaseDaoImpl<Outward, Integer> {

    public OutwardDao() {
        super("outward", "id", Outward.class);
    }

    //根据客户ID查询外出申请
    public List<Outward> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM outward WHERE customer_id = ? AND is_deleted = 0";
        return executeQuery(sql, customerId);
    }

    //根据审批状态查询（0已提交 1同意 2拒绝）
    public List<Outward> findByAuditStatus(Integer auditStatus) {
        String sql = "SELECT * FROM outward WHERE audit_status = ? AND is_deleted = 0";
        return executeQuery(sql, auditStatus);
    }
}
