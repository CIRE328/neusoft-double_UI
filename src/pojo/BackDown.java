package pojo;

import java.util.Date;

/**
 * 退住申请实体类
 * 对应数据库表 backdown，记录客户退住事由、类型及审批信息
 */

public class BackDown {
    private Integer id;
    private String remarks;
    /** 逻辑删除标志：0-未删除，1-已删除 */
    private Integer isDeleted;
    /** 客户ID，关联 customer 表 */
    private Integer customerId;
    private Date retreatment;
    /** 退住类型：0-正常退住，1-死亡退住，2-保留床位 */
    private Integer retreattype;
    private String retreatmentreason;
    /** 审批状态：0-待审核，1-已通过，2-已拒绝 */
    private Integer auditstatus;
    private String auditperson;
    private Date audittime;

    public BackDown() {}

    public BackDown(Integer id, String remarks, Integer isDeleted, Integer customerId,
                    Date retreatment, Integer retreattype, String retreatmentreason,
                    Integer auditstatus, String auditperson, Date audittime) {
        this.id = id;
        this.remarks = remarks;
        this.isDeleted = isDeleted;
        this.customerId = customerId;
        this.retreatment = retreatment;
        this.retreattype = retreattype;
        this.retreatmentreason = retreatmentreason;
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

    public Date getRetreatment() { return retreatment; }
    public void setRetreatment(Date retreatment) { this.retreatment = retreatment; }

    public Integer getRetreattype() { return retreattype; }
    public void setRetreattype(Integer retreattype) { this.retreattype = retreattype; }

    public String getRetreatmentreason() { return retreatmentreason; }
    public void setRetreatmentreason(String retreatmentreason) { this.retreatmentreason = retreatmentreason; }

    public Integer getAuditstatus() { return auditstatus; }
    public void setAuditstatus(Integer auditstatus) { this.auditstatus = auditstatus; }

    public String getAuditperson() { return auditperson; }
    public void setAuditperson(String auditperson) { this.auditperson = auditperson; }

    public Date getAudittime() { return audittime; }
    public void setAudittime(Date audittime) { this.audittime = audittime; }
}