package pojo;

/**
 * 床位实体类
 * 对应数据库表 bed，存储房间内的床位编号及占用状态
 */

public class Bed {
    private Integer id;
    private Integer roomNo;      // room_no
    /** 床位状态：1-空闲，2-有人，3-外出 */
    private Integer bedStatus;   // bed_status
    private String remarks;
    private String bedNo;        // bed_no

    public Bed() {}

    public Bed(Integer id, Integer roomNo, Integer bedStatus, String remarks, String bedNo) {
        this.id = id;
        this.roomNo = roomNo;
        this.bedStatus = bedStatus;
        this.remarks = remarks;
        this.bedNo = bedNo;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getRoomNo() { return roomNo; }
    public void setRoomNo(Integer roomNo) { this.roomNo = roomNo; }

    public Integer getBedStatus() { return bedStatus; }
    public void setBedStatus(Integer bedStatus) { this.bedStatus = bedStatus; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getBedNo() { return bedNo; }
    public void setBedNo(String bedNo) { this.bedNo = bedNo; }
}
