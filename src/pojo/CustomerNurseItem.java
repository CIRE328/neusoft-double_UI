package pojo;

import java.util.Date;

/**
 * 客户护理项目实体类
 * 对应数据库表 customernurseitem，记录客户购买的护理项目及剩余次数
 */

public class CustomerNurseItem {
    private Integer id;
    /** 护理项目ID，关联 nursecontent 表 */
    private Integer itemId;
    /** 客户ID，关联 customer 表 */
    private Integer customerId;
    /** 护理级别ID，关联 nurselevel 表 */
    private Integer levelId;
    /** 剩余护理次数 */
    private Integer nurseNumber;
    /** 逻辑删除标志：0-未删除，1-已删除 */
    private Integer isDeleted;
    private Date buyTime;
    private Date maturityTime;

    public CustomerNurseItem() {}

    public CustomerNurseItem(Integer id, Integer itemId, Integer customerId, Integer levelId,
                             Integer nurseNumber, Integer isDeleted, Date buyTime, Date maturityTime) {
        this.id = id;
        this.itemId = itemId;
        this.customerId = customerId;
        this.levelId = levelId;
        this.nurseNumber = nurseNumber;
        this.isDeleted = isDeleted;
        this.buyTime = buyTime;
        this.maturityTime = maturityTime;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }

    public Integer getNurseNumber() { return nurseNumber; }
    public void setNurseNumber(Integer nurseNumber) { this.nurseNumber = nurseNumber; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    public Date getBuyTime() { return buyTime; }
    public void setBuyTime(Date buyTime) { this.buyTime = buyTime; }

    public Date getMaturityTime() { return maturityTime; }
    public void setMaturityTime(Date maturityTime) { this.maturityTime = maturityTime; }
}