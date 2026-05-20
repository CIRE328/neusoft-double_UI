package com.neusoft.pojo;

public class Bed {
    private Integer id;
    private Integer roomNo;      // room_no
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
