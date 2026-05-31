package pojo;

import java.util.Date;

/**
 * 护理记录实体类
 * 对应数据库表 nurserecord，记录客户护理项目的执行明细
 */

public class NurseRecord {
    private Integer id;
    /** 逻辑删除标志：0-未删除，1-已删除 */
    private Integer isDeleted;
    /** 客户ID，关联 customer 表 */
    private Integer customerId;
    /** 护理项目ID，关联 nursecontent 表 */
    private Integer itemId;
    private Date nursingTime;
    private String nursingContent;
    private Integer nursingCount;
    /** 执行护理的用户ID，关联 user 表 */
    private Integer userId;

    public NurseRecord() {}

    public NurseRecord(Integer id, Integer isDeleted, Integer customerId, Integer itemId,
                       Date nursingTime, String nursingContent, Integer nursingCount, Integer userId) {
        this.id = id;
        this.isDeleted = isDeleted;
        this.customerId = customerId;
        this.itemId = itemId;
        this.nursingTime = nursingTime;
        this.nursingContent = nursingContent;
        this.nursingCount = nursingCount;
        this.userId = userId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }

    public Date getNursingTime() { return nursingTime; }
    public void setNursingTime(Date nursingTime) { this.nursingTime = nursingTime; }

    public String getNursingContent() { return nursingContent; }
    public void setNursingContent(String nursingContent) { this.nursingContent = nursingContent; }

    public Integer getNursingCount() { return nursingCount; }
    public void setNursingCount(Integer nursingCount) { this.nursingCount = nursingCount; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}