package com.neusoft.dao;

import com.neusoft.pojo.NurseRecord;
import java.util.List;

public class NurseRecordDao extends BaseDaoImpl<NurseRecord, Integer> {

    public NurseRecordDao() {
        super("nurserecord", "id", NurseRecord.class);
    }

    //根据客户ID查询护理记录
    public List<NurseRecord> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM nurserecord WHERE customer_id = ? AND is_deleted = 0 ORDER BY nursing_time DESC";
        return executeQuery(sql, customerId);
    }

    //根据护理人员ID查询
    public List<NurseRecord> findByUserId(Integer userId) {
        String sql = "SELECT * FROM nurserecord WHERE user_id = ? AND is_deleted = 0";
        return executeQuery(sql, userId);
    }
}
