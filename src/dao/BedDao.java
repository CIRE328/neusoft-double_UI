package dao;

import pojo.Bed;
import java.util.List;

/**
 * 床位数据访问对象
 * 提供床位相关的数据库操作，包括根据房间号查询、根据状态查询等
 */

public class BedDao extends BaseDaoImpl<Bed, Integer> {

    public BedDao() {
        super("bed", "id", Bed.class, false);
    }

    /**
     * 根据房间号查询床位
     *
     * @param roomNo 房间号
     * @return 该房间下的所有床位列表
     */

    public List<Bed> findByRoomNo(Integer roomNo) {
        String sql = "SELECT * FROM bed WHERE room_no = ? ";
        return executeQuery(sql, roomNo);
    }

    /**
     * 根据床位状态查询
     *
     * @param status 1空闲 2有人 3外出
     * @return 符合状态的床位列表
     */

    public List<Bed> findByStatus(Integer status) {
        String sql = "SELECT * FROM bed WHERE bed_status = ? ";
        return executeQuery(sql, status);
    }

    /**
     * 查询所有空闲床位
     *
     * @return 所有空闲状态的床位列表
     */

    public List<Bed> findFreeBeds() {
        return findByStatus(1);
    }
}