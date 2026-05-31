package service;

import dao.*;
import pojo.*;
import util.DateUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 床位管理服务，负责床位统计、房间查询、换床及床位使用明细等业务。
 */
public class BedService {
    private final BedDao bedDao = new BedDao();
    private final RoomDao roomDao = new RoomDao();
    private final BedDetailsDao bedDetailsDao = new BedDetailsDao();
    private final CustomerDao customerDao = new CustomerDao();

    /**
     * 统计床位总数及各状态（空闲、占用、外出）数量。
     *
     * @return 包含 total、free、occupied、outward 键的统计 Map
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
     * 按楼层查询房间及其下属床位信息。
     *
     * @param floor 楼层标识
     * @return 每个房间包含 roomNo 与 beds 列表的 Map 集合
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
     * 查询指定客户的床位使用明细，可按当前或历史使用状态筛选。
     *
     * @param customerId  客户 ID
     * @param usageStatus 使用状态（"当前使用"、"历史使用"），为空时返回全部
     * @return 床位使用明细列表
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
     * 为客户办理换床，结束旧床位使用记录并占用新床位。
     *
     * @param customerId 客户 ID
     * @param newBedId   新床位 ID
     * @return 换床成功返回 true，客户或新床位不存在、新床位非空闲时返回 false
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
                .filter(d -> d.getCustomerId().equals(customerId) && d.getBedId().equals(oldBedId) && d.getEndDate() == null)
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
     * 按客户姓名、入住日期及使用状态组合查询床位使用明细。
     *
     * @param customerName 客户姓名关键字，可为 null 表示不限
     * @param checkinDate  入住日期，可为 null 表示不限
     * @param usageStatus  使用状态（"当前使用"、"历史使用"），可为 null 表示不限
     * @return 匹配的床位使用明细列表
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
     * 查询指定房间内的所有空闲床位。
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
     * 获取所有房间号。
     *
     * @return 房间号列表
     */
    public List<Integer> getAllRoomNumbers() {
        return roomDao.findAll().stream().map(Room::getRoomNo).collect(Collectors.toList());
    }

    /**
     * 查询所有床位。
     *
     * @return 床位列表
     */
    public List<Bed> getAllBeds() {
        return bedDao.findAll();
    }

    /**
     * 根据床位 ID 查询当前占用该床位的客户 ID。
     *
     * @param bedId 床位 ID
     * @return 客户 ID，床位当前无人占用时返回 null
     */
    public Integer getCustomerIdByBedId(Integer bedId) {
        return bedDetailsDao.findAll().stream()
                .filter(d -> d.getBedId().equals(bedId) && d.getEndDate() == null)
                .map(BedDetails::getCustomerId)
                .findFirst()
                .orElse(null);
    }
}
