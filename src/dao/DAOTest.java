package dao;

import com.neusoft.dao.*;
import com.neusoft.pojo.*;
import pojo.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DAOTest {

    public static void main(String[] args) {
        testUserDao();
        testBedDao();
        testBedDetailsDao();
        testRoomDao();
        testRoleDao();
        testMenuDao();
        testRoleMenuDao();
        testNurseContentDao();
        testNurseLevelDao();
        testNurseLevelItemDao();
        testNurseRecordDao();
        testCustomerNurseItemDao();
        testOutwardDao();
        testBackdownDao();
        testFoodDao();
        testCustomerPreferenceDao();
        testMealDao();
        testCustomerDao();

        System.out.println("\n=== 所有 DAO 测试完成，请检查数据库变化 ===");
    }

    private static void testUserDao() {
        UserDao dao = new UserDao();
        System.out.println("=== UserDao ===");
        List<User> all = dao.findAll();
        System.out.println("用户总数: " + all.size());
        // 插入测试
        User u = new User();
        u.setNickname("测试员");
        u.setUsername("testuser");
        u.setPassword("123");
        u.setSex(1);
        u.setPhoneNumber("13800138000");
        u.setRoleId(2);
        u.setCreateTime(new Date());
        u.setUpdateTime(new Date());
        u.setIsDeleted(0);
        u.setCreateBy(1);
        u.setUpdateBy(1);
        User inserted = dao.insert(u);
        System.out.println("插入后ID: " + inserted.getId());
        // 按ID查询
        Optional<User> found = dao.findById(inserted.getId());
        System.out.println("查询结果: " + (found.isPresent() ? found.get().getUsername() : "无"));
        // 更新
        inserted.setNickname("更新测试");
        dao.update(inserted);
        // 逻辑删除
        dao.deleteById(inserted.getId());
        System.out.println("删除后是否存在: " + dao.findById(inserted.getId()).isPresent());
    }

    private static void testBedDao() {
        BedDao dao = new BedDao();
        System.out.println("\n=== BedDao ===");
        List<Bed> all = dao.findAll();
        System.out.println("床位总数: " + all.size());
        if (!all.isEmpty()) {
            Bed b = all.get(0);
            System.out.println("示例床位: 房间号=" + b.getRoomNo() + ", 状态=" + b.getBedStatus());
        }
        List<Bed> free = dao.findFreeBeds();
        System.out.println("空闲床位: " + free.size());
        List<Bed> roomBeds = dao.findByRoomNo(101);
        System.out.println("房间101的床位: " + roomBeds.size());
    }

    private static void testBedDetailsDao() {
        BedDetailsDao dao = new BedDetailsDao();
        System.out.println("\n=== BedDetailsDao ===");
        List<BedDetails> all = dao.findAll();
        System.out.println("床位使用记录总数: " + all.size());
        if (!all.isEmpty()) {
            BedDetails d = all.get(0);
            System.out.println("示例记录: 客户ID=" + d.getCustomerId() + ", 床位ID=" + d.getBedId());
        }
    }

    private static void testRoomDao() {
        RoomDao dao = new RoomDao();
        System.out.println("\n=== RoomDao ===");
        List<Room> all = dao.findAll();
        System.out.println("房间总数: " + all.size());
        if (!all.isEmpty()) {
            Room r = all.get(0);
            System.out.println("示例房间: 楼层=" + r.getRoomFloor() + ", 编号=" + r.getRoomNo());
        }
        List<Room> floorRooms = dao.findByFloor("1F");
        System.out.println("1F房间数: " + floorRooms.size());
    }

    private static void testRoleDao() {
        RoleDao dao = new RoleDao();
        System.out.println("\n=== RoleDao ===");
        List<Role> all = dao.findAll();
        System.out.println("角色总数: " + all.size());
        all.forEach(r -> System.out.println(r.getId() + " " + r.getName()));
    }

    private static void testMenuDao() {
        MenuDao dao = new MenuDao();
        System.out.println("\n=== MenuDao ===");
        List<Menu> all = dao.findAll();
        System.out.println("菜单总数: " + all.size());
        if (!all.isEmpty()) {
            Menu m = all.get(0);
            List<Menu> children = dao.findByParentId(m.getId());
            System.out.println(m.getTitle() + " 的子菜单数: " + children.size());
        }
    }

    private static void testRoleMenuDao() {
        RoleMenuDao dao = new RoleMenuDao();
        System.out.println("\n=== RoleMenuDao ===");
        List<RoleMenu> all = dao.findAll();
        System.out.println("角色菜单关联总数: " + all.size());
        if (!all.isEmpty()) {
            RoleMenu rm = all.get(0);
            List<RoleMenu> byRole = dao.findByRoleId(rm.getRoleId());
            System.out.println("角色ID " + rm.getRoleId() + " 的菜单权限数: " + byRole.size());
        }
    }

    private static void testNurseContentDao() {
        NurseContentDao dao = new NurseContentDao();
        System.out.println("\n=== NurseContentDao ===");
        List<NurseContent> all = dao.findAll();
        System.out.println("护理项目总数: " + all.size());
        List<NurseContent> enabled = dao.findByStatus(1);
        System.out.println("启用项目数: " + enabled.size());
        // 插入测试
        NurseContent nc = new NurseContent();
        nc.setSerialNumber("TEST");
        nc.setNursingName("测试项目");
        nc.setServicePrice("10");
        nc.setStatus(1);
        nc.setIsDeleted(0);
        NurseContent inserted = dao.insert(nc);
        System.out.println("插入项目ID: " + inserted.getId());
        dao.deleteById(inserted.getId());
    }

    private static void testNurseLevelDao() {
        NurseLevelDao dao = new NurseLevelDao();
        System.out.println("\n=== NurseLevelDao ===");
        List<NurseLevel> all = dao.findAll();
        System.out.println("护理级别总数: " + all.size());
        List<NurseLevel> enabled = dao.findByStatus(1);
        System.out.println("启用级别数: " + enabled.size());
    }

    private static void testNurseLevelItemDao() {
        NurseLevelItemDao dao = new NurseLevelItemDao();
        System.out.println("\n=== NurseLevelItemDao ===");
        List<NurseLevelItem> all = dao.findAll();
        System.out.println("级别项目关联总数: " + all.size());
        if (!all.isEmpty()) {
            NurseLevelItem item = all.get(0);
            List<NurseLevelItem> byLevel = dao.findByLevelId(item.getLevelId());
            System.out.println("级别ID " + item.getLevelId() + " 关联项目数: " + byLevel.size());
        }
    }

    private static void testNurseRecordDao() {
        NurseRecordDao dao = new NurseRecordDao();
        System.out.println("\n=== NurseRecordDao ===");
        List<NurseRecord> all = dao.findAll();
        System.out.println("护理记录总数: " + all.size());
        if (!all.isEmpty()) {
            NurseRecord r = all.get(0);
            List<NurseRecord> byCustomer = dao.findByCustomerId(r.getCustomerId());
            System.out.println("客户ID " + r.getCustomerId() + " 的护理记录数: " + byCustomer.size());
        }
    }

    private static void testCustomerNurseItemDao() {
        CustomerNurseItemDao dao = new CustomerNurseItemDao();
        System.out.println("\n=== CustomerNurseItemDao ===");
        List<CustomerNurseItem> all = dao.findAll();
        System.out.println("客户护理条目总数: " + all.size());
        if (!all.isEmpty()) {
            CustomerNurseItem cni = all.get(0);
            List<CustomerNurseItem> byCustomer = dao.findByCustomerId(cni.getCustomerId());
            System.out.println("客户ID " + cni.getCustomerId() + " 的护理条目数: " + byCustomer.size());
        }
    }

    private static void testOutwardDao() {
        OutwardDao dao = new OutwardDao();
        System.out.println("\n=== OutwardDao ===");
        List<Outward> all = dao.findAll();
        System.out.println("外出申请总数: " + all.size());
        if (!all.isEmpty()) {
            Outward o = all.get(0);
            List<Outward> byCustomer = dao.findByCustomerId(o.getCustomerId());
            System.out.println("客户ID " + o.getCustomerId() + " 外出申请数: " + byCustomer.size());
            List<Outward> pending = dao.findByAuditStatus(0);
            System.out.println("待审核申请数: " + pending.size());
        }
    }

    private static void testBackdownDao() {
        BackdownDao dao = new BackdownDao();
        System.out.println("\n=== BackdownDao ===");
        List<BackDown> all = dao.findAll();
        System.out.println("退住申请总数: " + all.size());
        if (!all.isEmpty()) {
            BackDown b = all.get(0);
            List<BackDown> byCustomer = dao.findByCustomerId(b.getCustomerId());
            System.out.println("客户ID " + b.getCustomerId() + " 退住申请数: " + byCustomer.size());
        }
    }

    private static void testFoodDao() {
        FoodDao dao = new FoodDao();
        System.out.println("\n=== FoodDao ===");
        List<Food> all = dao.findAll();
        System.out.println("食品总数: " + all.size());
        if (!all.isEmpty()) {
            Food f = all.get(0);
            List<Food> byType = dao.findByType(f.getFoodType());
            System.out.println("类型 " + f.getFoodType() + " 的食品数: " + byType.size());
        }
    }

    private static void testCustomerPreferenceDao() {
        PreferenceDao dao = new PreferenceDao();
        System.out.println("\n=== CustomerPreferenceDao ===");
        List<Preference> all = dao.findAll();
        System.out.println("客户喜好总数: " + all.size());
        if (!all.isEmpty()) {
            Preference p = all.get(0);
            Optional<Preference> byCustomer = dao.findByCustomerId(p.getCustomerId());
            System.out.println("客户ID " + p.getCustomerId() + " 喜好存在: " + byCustomer.isPresent());
        }
    }

    private static void testMealDao() {
        MealDao dao = new MealDao();
        System.out.println("\n=== MealDao ===");
        List<Meal> all = dao.findAll();
        System.out.println("膳食日历总数: " + all.size());
        if (!all.isEmpty()) {
            Meal m = all.get(0);
            List<Meal> byDay = dao.findByWeekDay(m.getWeekDay());
            System.out.println(m.getWeekDay() + " 的餐次安排数: " + byDay.size());
        }
    }

    private static void testCustomerDao() {
        CustomerDao dao = new CustomerDao();
        System.out.println("\n=== CustomerDao ===");
        List<Customer> all = dao.findAll();
        System.out.println("客户总数: " + all.size());
        if (!all.isEmpty()) {
            Customer c = all.get(0);
            List<Customer> byName = dao.findByNameLike(c.getCustomerName().substring(0, 1));
            System.out.println("姓名模糊查询结果数: " + byName.size());
            List<Customer> byLevel = dao.findByLevelId(c.getLevelId());
            System.out.println("护理级别 " + c.getLevelId() + " 的客户数: " + byLevel.size());
            List<Customer> byHousekeeper = dao.findByUserId(c.getUserId());
            System.out.println("管家ID " + c.getUserId() + " 的客户数: " + byHousekeeper.size());
            List<Customer> without = dao.findWithoutHousekeeper();
            System.out.println("无管家客户数: " + without.size());
        }
        // 插入临时客户测试
        Customer temp = new Customer();
        temp.setCustomerName("测试客户");
        temp.setCustomerAge(20);
        temp.setCustomerSex(1);
        temp.setIdcard("123456789012345678");
        temp.setRoomNo("999");
        temp.setBuildingNo("606");
        temp.setCheckinDate(new Date());
        temp.setExpirationDate(new Date());
        temp.setContactTel("13900000000");
        temp.setBedId(1);
        temp.setIsDeleted(0);
        Customer inserted = dao.insert(temp);
        System.out.println("插入临时客户ID: " + inserted.getId());
        // 按身份证查询
        Optional<Customer> byIdCard = dao.findByIdCard(inserted.getIdcard());
        System.out.println("按身份证查询: " + (byIdCard.isPresent() ? "找到" : "未找到"));
        // 清理
        dao.deleteById(inserted.getId());
        System.out.println("临时客户已删除");
    }
}