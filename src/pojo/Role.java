package pojo;

import java.util.Date;

/**
 * 角色实体类
 * 对应数据库表 role，定义系统用户的权限角色
 */

public class Role {
    private Integer id;
    private Date createTime;
    private Date updateTime;
    private Integer updateBy;
    /** 逻辑删除标志：0-未删除，1-已删除 */
    private Integer isDeleted;
    private String name;

    public Role() {}

    public Role(Integer id, Date createTime, Date updateTime, Integer updateBy,
                Integer isDeleted, String name) {
        this.id = id;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
        this.isDeleted = isDeleted;
        this.name = name;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getUpdateBy() { return updateBy; }
    public void setUpdateBy(Integer updateBy) { this.updateBy = updateBy; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}