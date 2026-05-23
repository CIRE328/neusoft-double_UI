package com.neusoft.dao;

import com.neusoft.pojo.BedDetails;
import java.util.List;

public class BedDetailsDao extends BaseDaoImpl<BedDetails, Integer> {

    public BedDetailsDao() {
        super("bed_details", "id", BedDetails.class);
    }

    //根据客户ID查询床位使用记录
    public List<BedDetails> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM bed_details WHERE customer_id = ? AND is_deleted = 0";
        return executeQuery(sql, customerId);
    }

    //查询客户当前正在使用的床位记录（end_date IS NULL）
    public List<BedDetails> findCurrentByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM bed_details WHERE customer_id = ? AND end_date IS NULL AND is_deleted = 0";
        return executeQuery(sql, customerId);
    }
}