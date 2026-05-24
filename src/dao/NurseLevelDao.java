package dao;

import pojo.NurseLevel;
import java.util.List;

public class NurseLevelDao extends BaseDaoImpl<NurseLevel, Integer> {

    public NurseLevelDao() {
        super("nurse_level", "id", NurseLevel.class);
    }

    //根据级别状态查询（1启用 2停用）
    public List<NurseLevel> findByStatus(Integer levelStatus) {
        String sql = "SELECT * FROM nurse_level WHERE level_status = ? AND is_deleted = 0";
        return executeQuery(sql, levelStatus);
    }
}
