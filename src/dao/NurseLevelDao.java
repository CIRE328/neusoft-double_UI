package dao;

import pojo.NurseLevel;
import java.util.List;

/**
 * 护理级别数据访问对象
 * 提供护理级别相关的数据库操作，包括按状态查询等
 */

public class NurseLevelDao extends BaseDaoImpl<NurseLevel, Integer> {

    /**
     * 构造函数
     * 初始化护理级别 DAO，指定表名、主键列名、实体类类型，使用逻辑删除
     */

    public NurseLevelDao() {
        super("nurselevel", "id", NurseLevel.class);
    }

    /**
     * 根据级别状态查询护理级别
     *
     * @param levelStatus 级别状态（1 启用，2 停用）
     * @return 符合状态的护理级别列表
     */

    public List<NurseLevel> findByStatus(Integer levelStatus) {
        String sql = "SELECT * FROM nurselevel WHERE level_status = ? AND is_deleted = 0";
        return executeQuery(sql, levelStatus);
    }
}
