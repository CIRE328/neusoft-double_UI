package pojo;

/**
 * 护理级别项目关联实体类
 * 对应数据库表 nurselevelitem，维护护理级别与护理项目的对应关系
 */

public class NurseLevelItem {
    private Integer id;
    /** 护理级别ID，关联 nurselevel 表 */
    private Integer levelId;
    /** 护理项目ID，关联 nursecontent 表 */
    private Integer itemId;

    public NurseLevelItem() {}

    public NurseLevelItem(Integer id, Integer levelId, Integer itemId) {
        this.id = id;
        this.levelId = levelId;
        this.itemId = itemId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }

    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }
}