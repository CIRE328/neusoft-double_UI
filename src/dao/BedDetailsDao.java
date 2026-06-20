package dao;

import pojo.BedDetails;

/**
 * 床位详情数据访问对象
 * 提供床位使用记录的数据库操作功能
 * 继承自BaseDaoImpl，支持基本的CRUD操作
 */

public class BedDetailsDao extends BaseDaoImpl<BedDetails, Integer> {

    /**
     * 构造函数，初始化床位详情DAO
     * 指定表名为beddetails，主键列为id，实体类为BedDetails
     */

    public BedDetailsDao() {
        super("beddetails", "id", BedDetails.class);
    }

}