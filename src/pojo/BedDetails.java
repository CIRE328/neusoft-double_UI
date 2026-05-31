package pojo;

import java.util.Date;

/**
 * 床位使用明细实体类
 * 对应数据库表 beddetails，记录客户与床位的入住起止时间及详情
 */

public class BedDetails {
    private Integer id;
    private Date startDate;
    private Date endDate;
    private String bedDetails;
    /** 客户ID，关联 customer 表 */
    private Integer customerId;
    /** 床位ID，关联 bed 表 */
    private Integer bedId;
    /** 逻辑删除标志：0-未删除，1-已删除 */
    private Integer isDeleted;

    public BedDetails() {}

    public BedDetails(Integer id, Date startDate, Date endDate, String bedDetails,
                      Integer customerId, Integer bedId, Integer isDeleted) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.bedDetails = bedDetails;
        this.customerId = customerId;
        this.bedId = bedId;
        this.isDeleted = isDeleted;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getBedDetails() { return bedDetails; }
    public void setBedDetails(String bedDetails) { this.bedDetails = bedDetails; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public Integer getBedId() { return bedId; }
    public void setBedId(Integer bedId) { this.bedId = bedId; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}