package pojo;

/**
 * 护理级别实体类
 * 对应数据库表 nurselevel，定义客户的护理等级及启用状态
 */

public class NurseLevel {
    private Integer id;
    private String levelName;
    /** 级别状态：1-启用，2-停用 */
    private Integer levelStatus;
    /** 逻辑删除标志：0-未删除，1-已删除 */
    private Integer isDeleted;

    public NurseLevel() {}

    public NurseLevel(Integer id, String levelName, Integer levelStatus, Integer isDeleted) {
        this.id = id;
        this.levelName = levelName;
        this.levelStatus = levelStatus;
        this.isDeleted = isDeleted;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }

    public Integer getLevelStatus() { return levelStatus; }
    public void setLevelStatus(Integer levelStatus) { this.levelStatus = levelStatus; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}