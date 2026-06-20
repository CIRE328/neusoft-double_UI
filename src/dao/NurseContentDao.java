package dao;

import pojo.NurseContent;
import java.util.List;

/**
 * 护理项目数据访问对象
 * 提供护理项目（护理内容）相关的数据库操作
 */

public class NurseContentDao extends BaseDaoImpl<NurseContent, Integer> {

    /**
     * 构造函数
     * 初始化护理项目 DAO，指定表名、主键列名、实体类类型，使用逻辑删除
     */

    public NurseContentDao() {
        super("nursecontent", "id", NurseContent.class);
    }

    /**
     * 根据状态查询护理项目
     *
     * @param status 状态（1 启用，2 停用）
     * @return 符合状态的护理项目列表
     */

    public List<NurseContent> findByStatus(Integer status) {
        String sql = "SELECT * FROM nursecontent WHERE status = ? AND is_deleted = 0";
        return executeQuery(sql, status);
    }

}
