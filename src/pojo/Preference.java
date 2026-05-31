package pojo;

/**
 * 客户偏好实体类
 * 对应数据库表 preference，记录客户的饮食偏好、注意事项等
 */

public class Preference {
    private Integer id;
    /** 客户ID，关联 customer 表 */
    private Integer customerId;
    private String preferences;
    private String attention;
    private String remark;
    /** 逻辑删除标志：0-未删除，1-已删除 */
    private Integer isDeleted;

    public Preference() {}

    public Preference(Integer id, Integer customerId, String preferences,
                      String attention, String remark, Integer isDeleted) {
        this.id = id;
        this.customerId = customerId;
        this.preferences = preferences;
        this.attention = attention;
        this.remark = remark;
        this.isDeleted = isDeleted;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }

    public String getAttention() { return attention; }
    public void setAttention(String attention) { this.attention = attention; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}