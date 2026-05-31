package dao;

import pojo.Room;
import java.util.List;

/**
 * 房间数据访问对象
 * 提供房间相关的数据库操作，包括按楼层、按房间号查询等
 */

public class RoomDao extends BaseDaoImpl<Room, Integer> {

    /**
     * 构造函数
     * 初始化房间 DAO，指定表名、主键列名、实体类类型，不使用逻辑删除
     */

    public RoomDao() {
        super("room", "id", Room.class, false);
    }

    /**
     * 根据楼层查询房间
     *
     * @param floor 楼层
     * @return 该楼层的所有房间列表
     */

    public List<Room> findByFloor(String floor) {
        String sql = "SELECT * FROM room WHERE room_floor = ? ";
        return executeQuery(sql, floor);
    }

    /**
     * 根据房间号查询房间
     *
     * @param roomNo 房间号
     * @return 匹配的房间对象，未找到时返回 null
     */

    public Room findByRoomNo(Integer roomNo) {
        String sql = "SELECT * FROM room WHERE room_no = ?";
        List<Room> list = executeQuery(sql, roomNo);
        return list.isEmpty() ? null : list.get(0);
    }
}
