package pojo;

import java.util.Date;

/**
 * 客户实体类
 * 对应数据库表 customer，存储养老院入住客户的基本信息、健康资料及护理分配
 */

public class Customer {
    private Integer id;
    /** 逻辑删除标志：0-未删除，1-已删除 */
    private Integer isDeleted;
    private String customerName;
    private Integer customerAge;
    /** 性别：0-男，1-女 */
    private Integer customerSex;
    private String idcard;
    private String roomNo;
    private String buildingNo;
    private Date checkinDate;
    private Date expirationDate;
    private String contactTel;
    private Integer bedId;
    private String psychosomaticState;
    private String attention;
    private Date birthday;
    private String height;
    private String weight;
    private String bloodType;
    private String filepath;
    /** 健康管家（用户）ID */
    private Integer userId;
    /** 护理级别ID */
    private Integer levelId;
    private String familyMember;

    public Customer() {}

    public Customer(Integer id, Integer isDeleted, String customerName, Integer customerAge,
                    Integer customerSex, String idcard, String roomNo, String buildingNo,
                    Date checkinDate, Date expirationDate, String contactTel, Integer bedId,
                    String psychosomaticState, String attention, Date birthday, String height,
                    String weight, String bloodType, String filepath, Integer userId,
                    Integer levelId, String familyMember) {
        this.id = id;
        this.isDeleted = isDeleted;
        this.customerName = customerName;
        this.customerAge = customerAge;
        this.customerSex = customerSex;
        this.idcard = idcard;
        this.roomNo = roomNo;
        this.buildingNo = buildingNo;
        this.checkinDate = checkinDate;
        this.expirationDate = expirationDate;
        this.contactTel = contactTel;
        this.bedId = bedId;
        this.psychosomaticState = psychosomaticState;
        this.attention = attention;
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        this.filepath = filepath;
        this.userId = userId;
        this.levelId = levelId;
        this.familyMember = familyMember;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Integer getCustomerAge() { return customerAge; }
    public void setCustomerAge(Integer customerAge) { this.customerAge = customerAge; }

    public Integer getCustomerSex() { return customerSex; }
    public void setCustomerSex(Integer customerSex) { this.customerSex = customerSex; }

    public String getIdcard() { return idcard; }
    public void setIdcard(String idcard) { this.idcard = idcard; }

    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }

    public String getBuildingNo() { return buildingNo; }
    public void setBuildingNo(String buildingNo) { this.buildingNo = buildingNo; }

    public Date getCheckinDate() { return checkinDate; }
    public void setCheckinDate(Date checkinDate) { this.checkinDate = checkinDate; }

    public Date getExpirationDate() { return expirationDate; }
    public void setExpirationDate(Date expirationDate) { this.expirationDate = expirationDate; }

    public String getContactTel() { return contactTel; }
    public void setContactTel(String contactTel) { this.contactTel = contactTel; }

    public Integer getBedId() { return bedId; }
    public void setBedId(Integer bedId) { this.bedId = bedId; }

    public String getPsychosomaticState() { return psychosomaticState; }
    public void setPsychosomaticState(String psychosomaticState) { this.psychosomaticState = psychosomaticState; }

    public String getAttention() { return attention; }
    public void setAttention(String attention) { this.attention = attention; }

    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }

    public String getHeight() { return height; }
    public void setHeight(String height) { this.height = height; }

    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getFilepath() { return filepath; }
    public void setFilepath(String filepath) { this.filepath = filepath; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getLevelId() { return levelId; }
    public void setLevelId(Integer levelId) { this.levelId = levelId; }

    public String getFamilyMember() { return familyMember; }
    public void setFamilyMember(String familyMember) { this.familyMember = familyMember; }
}