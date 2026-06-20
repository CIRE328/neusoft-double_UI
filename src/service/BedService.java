package service;

import dao.*;
import pojo.*;
import util.DateUtils;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 床位服务类
 * 提供床位管理相关的业务逻辑功能
 * 包括床位统计、房间床位查询、床位使用记录管理、换床等功能
 */

public class BedService {
    private final BedDao bedDao = new BedDao();
    private final RoomDao roomDao = new RoomDao();
    private final BedDetailsDao bedDetailsDao = new BedDetailsDao();
    private final CustomerDao customerDao = new CustomerDao();

    /**
     * 获取床位统计信息
     * 统计总床位数、空闲床位数、占用床位数、外出床位数
     *
     * @return 包含统计信息的Map，key为统计类型，value为数量
     */

    public Map<String, Integer> getBedStatistics() {
        List<Bed> all = bedDao.findAll();
        return Map.of(
                "total", all.size(),
                "free", (int) all.stream().filter(b -> b.getBedStatus() == 1).count(),
                "occupied", (int) all.stream().filter(b -> b.getBedStatus() == 2).count(),
                "outward", (int) all.stream().filter(b -> b.getBedStatus() == 3).count()
        );
    }

    /**
     * 根据楼层获取房间及其床位信息
     *
     * @param floor 楼层号
     * @return 房间及床位信息列表，每个元素包含房间号和床位列表
     */

    public List<Map<String, Object>> getRoomsWithBedsByFloor(String floor) {
        List<Room> rooms = roomDao.findAll().stream()
                .filter(r -> r.getRoomFloor().equals(floor))
                .collect(Collectors.toList());
        List<Map<String, Object>> result = new ArrayList<>();
        for (Room room : rooms) {
            Map<String, Object> info = new HashMap<>();
            info.put("roomNo", room.getRoomNo());
            List<Bed> beds = bedDao.findAll().stream()
                    .filter(b -> b.getRoomNo().equals(room.getRoomNo()))
                    .collect(Collectors.toList());
            info.put("beds", beds);
            result.add(info);
        }
        return result;
    }

    /**
     * 获取客户的床位使用记录
     * 可根据使用状态筛选当前使用或历史使用记录
     *
     * @param customerId 客户ID
     * @param usageStatus 使用状态（"当前使用"、"历史使用"或其他）
     * @return 床位使用记录列表
     */

    public List<BedDetails> getBedUsageDetails(Integer customerId, String usageStatus) {
        List<BedDetails> all = bedDetailsDao.findAll().stream()
                .filter(d -> d.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
        if ("当前使用".equals(usageStatus)) {
            return all.stream().filter(d -> d.getEndDate() == null).collect(Collectors.toList());
        } else if ("历史使用".equals(usageStatus)) {
            return all.stream().filter(d -> d.getEndDate() != null).collect(Collectors.toList());
        }
        return all;
    }

    /**
     * 为客户更换床位
     * 更新旧床位使用记录的结束日期，创建新的床位使用记录，更新床位状态和客户信息
     *
     * @param customerId 客户ID
     * @param newBedId 新床位ID
     * @return 是否更换成功
     */

    public boolean changeBed(Integer customerId, Integer newBedId) {
        Optional<Customer> optCustomer = customerDao.findById(customerId);
        if (optCustomer.isEmpty()) return false;
        Customer customer = optCustomer.get();
        Integer oldBedId = customer.getBedId();
        Optional<Bed> optNewBed = bedDao.findById(newBedId);
        if (optNewBed.isEmpty() || optNewBed.get().getBedStatus() != 1) return false;
        Bed newBed = optNewBed.get();

        bedDetailsDao.findAll().stream()
                .filter(d -> d.getCustomerId() != null
                        && d.getCustomerId().equals(customerId)
                        && d.getBedId().equals(oldBedId)
                        && d.getEndDate() == null)
                .forEach(d -> {
                    d.setEndDate(DateUtils.now());
                    bedDetailsDao.update(d);
                });

        bedDao.findById(oldBedId).ifPresent(oldBed -> {
            oldBed.setBedStatus(1);
            bedDao.update(oldBed);
        });

        newBed.setBedStatus(2);
        bedDao.update(newBed);

        BedDetails newDetails = new BedDetails();
        newDetails.setStartDate(DateUtils.now());
        newDetails.setEndDate(null);
        newDetails.setCustomerId(customerId);
        newDetails.setBedId(newBedId);
        newDetails.setIsDeleted(0);
        bedDetailsDao.insert(newDetails);

        customer.setBedId(newBedId);
        customer.setRoomNo(String.valueOf(newBed.getRoomNo()));
        customerDao.update(customer);
        return true;
    }

    /**
     * 查询床位使用记录
     * 支持按客户姓名、入住日期、使用状态进行筛选
     *
     * @param customerName 客户姓名（支持模糊查询，可为null）
     * @param checkinDate 入住日期（可为null）
     * @param usageStatus 使用状态（"当前使用"、"历史使用"或其他）
     * @return 床位使用记录列表
     */

    public List<BedDetails> queryBedDetails(String customerName, Date checkinDate, String usageStatus) {
        Set<Integer> ids = customerDao.findAll().stream()
                .filter(c -> customerName == null || c.getCustomerName().contains(customerName))
                .map(Customer::getId)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) return List.of();

        List<BedDetails> all = bedDetailsDao.findAll().stream()
                .filter(d -> ids.contains(d.getCustomerId()))
                .collect(Collectors.toList());
        if (checkinDate != null) {
            all = all.stream().filter(d -> d.getStartDate() != null && d.getStartDate().equals(checkinDate)).collect(Collectors.toList());
        }
        if ("当前使用".equals(usageStatus)) {
            all = all.stream().filter(d -> d.getEndDate() == null).collect(Collectors.toList());
        } else if ("历史使用".equals(usageStatus)) {
            all = all.stream().filter(d -> d.getEndDate() != null).collect(Collectors.toList());
        }
        return all;
    }

    /**
     * 获取指定房间的空闲床位
     *
     * @param roomNo 房间号
     * @return 空闲床位列表
     */

    public List<Bed> getFreeBedsByRoom(Integer roomNo) {
        return bedDao.findByRoomNo(roomNo).stream()
                .filter(b -> b.getBedStatus() == 1)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有房间号
     *
     * @return 房间号列表
     */

    public List<Integer> getAllRoomNumbers() {
        return roomDao.findAll().stream().map(Room::getRoomNo).collect(Collectors.toList());
    }

    /**
     * 获取所有床位信息
     *
     * @return 床位列表
     */

    public List<Bed> getAllBeds() {
        return bedDao.findAll();
    }

    /**
     * 根据床位ID获取当前使用该床位的客户ID
     *
     * @param bedId 床位ID
     * @return 客户ID，如果床位未被占用则返回null
     */

    public Integer getCustomerIdByBedId(Integer bedId) {
        return bedDetailsDao.findAll().stream()
                .filter(d -> d.getBedId().equals(bedId) && d.getEndDate() == null)
                .map(BedDetails::getCustomerId)
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取所有楼层号
     *
     * @return 楼层号列表（已排序且去重）
     */

    public List<String> getAllFloors() {
        return roomDao.findAll().stream()
                .map(Room::getRoomFloor)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}