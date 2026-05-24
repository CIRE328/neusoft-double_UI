package dao;

import pojo.Room;
import java.util.List;

public class RoomDao extends BaseDaoImpl<Room, Integer> {

    public RoomDao() {
        super("room", "id", Room.class, false);
    }

    //根据楼层查询房间
    public List<Room> findByFloor(String floor) {
        String sql = "SELECT * FROM room WHERE room_floor = ? ";
        return executeQuery(sql, floor);
    }

    //根据房间号查询
    public Room findByRoomNo(Integer roomNo) {
        String sql = "SELECT * FROM room WHERE room_no = ?";
        List<Room> list = executeQuery(sql, roomNo);
        return list.isEmpty() ? null : list.get(0);
    }
}