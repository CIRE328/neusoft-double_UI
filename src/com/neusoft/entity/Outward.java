package com.neusoft.entity;

import java.util.Date;

public class Outward {
    private Integer id;
    private String remarks;
    private Integer isDeleted;
    private Integer customerId;
    private String outgoingreasons;
    private Date outgoingtime;
    private Date expectedreturntime;
    private Date actualreturntime;
    private String escorted;
    private String relation;
    private String escortedtel;
    private Integer auditstatus;
    private String auditperson;
    private Date audittime;

    public Outward() {}

    public Outward(Integer id, String remarks, Integer isDeleted, Integer customerId,
                   String outgoingreasons, Date outgoingtime, Date expectedreturntime,
                   Date actualreturntime, String escorted, String relation,
                   String escortedtel, Integer auditstatus, String auditperson, Date audittime) {
        this.id = id;
        this.remarks = remarks;
        this.isDeleted = isDeleted;
        this.customerId = customerId;
        this.outgoingreasons = outgoingreasons;
        this.outgoingtime = outgoingtime;
        this.expectedreturntime = expectedreturntime;
        this.actualreturntime = actualreturntime;
        this.escorted = escorted;
        this.relation = relation;
        this.escortedtel = escortedtel;
        this.auditstatus = auditstatus;
        this.auditperson = auditperson;
        this.audittime = audittime;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getOutgoingreasons() { return outgoingreasons; }
    public void setOutgoingreasons(String outgoingreasons) { this.outgoingreasons = outgoingreasons; }

    public Date getOutgoingtime() { return outgoingtime; }
    public void setOutgoingtime(Date outgoingtime) { this.outgoingtime = outgoingtime; }

    public Date getExpectedreturntime() { return expectedreturntime; }
    public void setExpectedreturntime(Date expectedreturntime) { this.expectedreturntime = expectedreturntime; }

    public Date getActualreturntime() { return actualreturntime; }
    public void setActualreturntime(Date actualreturntime) { this.actualreturntime = actualreturntime; }

    public String getEscorted() { return escorted; }
    public void setEscorted(String escorted) { this.escorted = escorted; }

    public String getRelation() { return relation; }
    public void setRelation(String relation) { this.relation = relation; }

    public String getEscortedtel() { return escortedtel; }
    public void setEscortedtel(String escortedtel) { this.escortedtel = escortedtel; }

    public Integer getAuditstatus() { return auditstatus; }
    public void setAuditstatus(Integer auditstatus) { this.auditstatus = auditstatus; }

    public String getAuditperson() { return auditperson; }
    public void setAuditperson(String auditperson) { this.auditperson = auditperson; }

    public Date getAudittime() { return audittime; }
    public void setAudittime(Date audittime) { this.audittime = audittime; }
}