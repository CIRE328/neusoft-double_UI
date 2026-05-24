package service;

import dao.*;
import pojo.*;

import java.util.*;

public class StatisticsService {
    private final BedDao bedDao = new BedDao();
    private final CustomerDao customerDao = new CustomerDao();
    private final NurseRecordDao nurseRecordDao = new NurseRecordDao();

    public Map<String, Integer> getBedStatistics() {
        List<Bed> beds = bedDao.findAll();
        return Map.of(
                "total", beds.size(),
                "free", (int) beds.stream().filter(b -> b.getBedStatus() == 1).count(),
                "occupied", (int) beds.stream().filter(b -> b.getBedStatus() == 2).count(),
                "outward", (int) beds.stream().filter(b -> b.getBedStatus() == 3).count()
        );
    }

    public Map<String, Integer> getCustomerStatistics() {
        List<Customer> customers = customerDao.findAll();
        int total = customers.size();
        int selfCare = (int) customers.stream().filter(c -> c.getLevelId() == null).count();
        return Map.of("total", total, "selfCare", selfCare, "nursingCare", total - selfCare);
    }

    public long getNurseRecordCount(Integer customerId) {
        if (customerId == null) return nurseRecordDao.findAll().size();
        return nurseRecordDao.findAll().stream()
                .filter(r -> r.getCustomerId().equals(customerId))
                .count();
    }
}
