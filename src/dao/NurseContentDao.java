package dao;

import pojo.NurseContent;
import java.util.List;

public class NurseContentDao extends BaseDaoImpl<NurseContent, Integer> {

    public NurseContentDao() {
        super("nursecontent", "id", NurseContent.class);
    }

    //根据状态查询（1启用 2停用）
    public List<NurseContent> findByStatus(Integer status) {
        String sql = "SELECT * FROM nursecontent WHERE status = ? AND is_deleted = 0";
        return executeQuery(sql, status);
    }

    //根据名称模糊查询
    public List<NurseContent> findByNameLike(String keyword) {
        String sql = "SELECT * FROM nursecontent WHERE nursing_name LIKE ? AND is_deleted = 0";
        return executeQuery(sql, "%" + keyword + "%");
    }
}