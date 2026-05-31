package service;

import dao.*;
import pojo.Bed;
import java.util.*;

/**
 * 统计服务，提供床位、客户及护理记录等汇总数据查询。
 */
public class StatisticsService {
    private final BedDao bedDao = new BedDao();
    private final CustomerDao customerDao = new CustomerDao();
    private final NurseRecordDao nurseRecordDao = new NurseRecordDao();

    /**
     * 统计床位总数及各状态（空闲、占用、外出）数量。
     *
     * @return 包含 total、free、occupied、outward 键的统计 Map
     */
    public Map<String, Integer> getBedStatistics() {
        List<Bed> beds = bedDao.findAll();
        return Map.of(
                "total", beds.size(),
                "free", (int) beds.stream().filter(b -> b.getBedStatus() == 1).count(),
                "occupied", (int) beds.stream().filter(b -> b.getBedStatus() == 2).count(),
                "outward", (int) beds.stream().filter(b -> b.getBedStatus() == 3).count()
        );
    }

    /**
     * 统计客户总数及自理、护理老人数量。
     *
     * @return 包含 total、selfCare、nursingCare 键的统计 Map
     */
    public Map<String, Integer> getCustomerStatistics() {
        List<pojo.Customer> customers = customerDao.findAll();
        int total = customers.size();
        int selfCare = (int) customers.stream().filter(c -> c.getLevelId() == null).count();
        return Map.of("total", total, "selfCare", selfCare, "nursingCare", total - selfCare);
    }

    /**
     * 统计护理记录数量。
     *
     * @param customerId 客户 ID，为 null 时统计全部记录
     * @return 护理记录条数
     */
    public long getNurseRecordCount(Integer customerId) {
        if (customerId == null) return nurseRecordDao.findAll().size();
        return nurseRecordDao.findAll().stream()
                .filter(r -> r.getCustomerId().equals(customerId))
                .count();
    }
}
