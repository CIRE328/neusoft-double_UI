package service;

import dao.*;
import pojo.*;
import util.DateUtils;

import java.util.*;
import java.util.stream.Collectors;

public class BedService {
    private final BedDao bedDao = new BedDao();
    private final RoomDao roomDao = new RoomDao();
    private final BedDetailsDao bedDetailsDao = new BedDetailsDao();
    private final CustomerDao customerDao = new CustomerDao();

    public Map<String, Integer> getBedStatistics() {
        List<Bed> all = bedDao.findAll();
        return Map.of(
                "total", all.size(),
                "free", (int) all.stream().filter(b -> b.getBedStatus() == 1).count(),
                "occupied", (int) all.stream().filter(b -> b.getBedStatus() == 2).count(),
                "outward", (int) all.stream().filter(b -> b.getBedStatus() == 3).count()
        );
    }

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
}