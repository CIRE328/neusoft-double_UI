package com.neusoft.dao;

import com.neusoft.pojo.NurseLevel;
import java.util.List;

public class NurseLevelDao extends BaseDaoImpl<NurseLevel, Integer> {

    public NurseLevelDao() {
        super("nurselevel", "id", NurseLevel.class);
    }

    //根据级别状态查询（1启用 2停用）
    public List<NurseLevel> findByStatus(Integer levelStatus) {
        String sql = "SELECT * FROM nurselevel WHERE level_status = ? AND is_deleted = 0";
        return executeQuery(sql, levelStatus);
    }
}
