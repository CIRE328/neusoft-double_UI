package pojo;

import java.util.Date;

public class User {
    // 对应数据库字段：id
    private Integer id;
    // create_time
    private Date createTime;
    // create_by
    private Integer createBy;
    // update_time
    private Date updateTime;
    // update_by
    private Integer updateBy;
    // is_deleted
    private Integer isDeleted;
    // nickname
    private String nickname;
    // username
    private String username;
    // password
    private String password;
    // sex
    private Integer sex;
    // email
    private String email;
    // phone_number
    private String phoneNumber;
    // role_id
    private Integer roleId;

    public User() {}

    public User(Integer id, Date createTime, Integer createBy, Date updateTime, Integer updateBy,
                Integer isDeleted, String nickname, String username, String password,
                Integer sex, String email, String phoneNumber, Integer roleId) {
        this.id = id;
        this.createTime = createTime;
        this.createBy = createBy;
        this.updateTime = updateTime;
        this.updateBy = updateBy;
        this.isDeleted = isDeleted;
        this.nickname = nickname;
        this.username = username;
        this.password = password;
        this.sex = sex;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roleId = roleId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Integer getCreateBy() { return createBy; }
    public void setCreateBy(Integer createBy) { this.createBy = createBy; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getUpdateBy() { return updateBy; }
    public void setUpdateBy(Integer updateBy) { this.updateBy = updateBy; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getSex() { return sex; }
    public void setSex(Integer sex) { this.sex = sex; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
}