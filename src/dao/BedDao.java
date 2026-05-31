package dao;

import pojo.Bed;
import java.util.List;

public class BedDao extends BaseDaoImpl<Bed, Integer> {

    public BedDao() {
        super("bed", "id", Bed.class, false);
    }

    //根据房间号查询床位
    public List<Bed> findByRoomNo(Integer roomNo) {
        String sql = "SELECT * FROM bed WHERE room_no = ? ";
        return executeQuery(sql, roomNo);
    }

    /*根据床位状态查询
     *@param status 1空闲 2有人 3外出
     */
    public List<Bed> findByStatus(Integer status) {
        String sql = "SELECT * FROM bed WHERE bed_status = ? ";
        return executeQuery(sql, status);
    }

    //查询空闲床位
    public List<Bed> findFreeBeds() {
        return findByStatus(1);
    }
}