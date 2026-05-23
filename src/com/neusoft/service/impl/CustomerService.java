package com.neusoft.service.impl;

import com.neusoft.service.dto.CustomerDTO;
import java.util.*;

public interface CustomerService {

    /**
     * 查询客户信息列表
     * @param name 客户姓名(模糊查询)
     * @param elderType 老人类型(自理老人/护理老人)
     * @return 客户信息列表
     */
    List<CustomerDTO> getCustomerList(String name, Integer elderType);

    /**
     * 登记客户入住信息
     * @param customerDTO 入住信息
     * @return 操作结果
     */
    boolean registerCheckIn(CustomerDTO customerDTO);

    /**
     * 退住登记申请
     * @param customerId 客户ID
     * @param leaveType 退住类型(正常退住/死亡退住/保留床位)
     * @param reason 退住原因
     * @return 操作结果
     */
    boolean applyCheckOut(Long customerId, String leaveType, String reason);

    /**
     * 外出登记申请
     * @param customerId 客户ID
     * @param reason 外出事由
     * @param leaveTime 外出时间
     * @param returnTime 预计回院时间
     * @return 操作结果
     */
    boolean applyOuting(Long customerId, String reason, String leaveTime, String returnTime);

    /**
     * 逻辑删除客户信息
     * @param customerId 客户ID
     * @return 操作结果
     */
    boolean deleteCustomer(Long customerId);

    /**
     * 修改客户信息
     * @param customerDTO 客户信息
     * @return 操作结果
     */
    boolean updateCustomer(CustomerDTO customerDTO);
}
