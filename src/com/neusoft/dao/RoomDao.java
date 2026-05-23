package com.neusoft.dao;

import com.neusoft.pojo.Room;
import java.util.List;

public class RoomDao extends BaseDaoImpl<Room, Integer> {

    public RoomDao() {
        super("room", "id", Room.class);
    }

    //根据楼层查询房间
    public List<Room> findByFloor(String floor) {
        String sql = "SELECT * FROM room WHERE room_floor = ? AND is_deleted = 0";
        return executeQuery(sql, floor);
    }

    //根据房间号查询
    public Room findByRoomNo(Integer roomNo) {
        String sql = "SELECT * FROM room WHERE room_no = ? AND is_deleted = 0";
        List<Room> list = executeQuery(sql, roomNo);
        return list.isEmpty() ? null : list.get(0);
    }
}