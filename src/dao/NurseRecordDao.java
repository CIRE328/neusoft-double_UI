package dao;

import pojo.NurseRecord;
import java.util.List;

/**
 * 护理记录数据访问对象
 * 提供客户护理记录相关的数据库操作
 */

public class NurseRecordDao extends BaseDaoImpl<NurseRecord, Integer> {

    /**
     * 构造函数
     * 初始化护理记录 DAO，指定表名、主键列名、实体类类型，使用逻辑删除
     */

    public NurseRecordDao() {
        super("nurserecord", "id", NurseRecord.class);
    }

    /**
     * 根据客户ID查询护理记录
     * 按护理时间倒序排列
     *
     * @param customerId 客户ID
     * @return 该客户的护理记录列表
     */

    public List<NurseRecord> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM nurserecord WHERE customer_id = ? AND is_deleted = 0 ORDER BY nursing_time DESC";
        return executeQuery(sql, customerId);
    }

    /**
     * 根据护理人员ID查询护理记录
     *
     * @param userId 护理人员（用户）ID
     * @return 该护理人员执行的护理记录列表
     */

    public List<NurseRecord> findByUserId(Integer userId) {
        String sql = "SELECT * FROM nurserecord WHERE user_id = ? AND is_deleted = 0";
        return executeQuery(sql, userId);
    }
}
