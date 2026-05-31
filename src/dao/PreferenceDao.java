package dao;

import pojo.Preference;
import java.util.List;
import java.util.Optional;

/**
 * 饮食喜好数据访问对象
 * 提供客户饮食偏好相关的数据库操作
 */

public class PreferenceDao extends BaseDaoImpl<Preference, Integer> {

    /**
     * 构造函数
     * 初始化饮食喜好 DAO，指定表名、主键列名、实体类类型，使用逻辑删除
     */

    public PreferenceDao() {
        super("preference", "id", Preference.class);
    }

    /**
     * 根据客户ID查询饮食喜好
     * 每个客户最多对应一条喜好记录
     *
     * @param customerId 客户ID
     * @return 包含查询结果的 Optional 对象
     */

    public Optional<Preference> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM preference WHERE customer_id = ? AND is_deleted = 0";
        List<Preference> list = executeQuery(sql, customerId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
