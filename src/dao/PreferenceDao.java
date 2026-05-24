package dao;

import pojo.Preference;
import java.util.List;
import java.util.Optional;

public class PreferenceDao extends BaseDaoImpl<Preference, Integer> {

    public PreferenceDao() {
        super("preference", "id", Preference.class);
    }

    /**
     * 根据客户ID查询饮食喜好
     */
    public Optional<Preference> findByCustomerId(Integer customerId) {
        String sql = "SELECT * FROM preference WHERE customer_id = ? AND is_deleted = 0";
        List<Preference> list = executeQuery(sql, customerId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
