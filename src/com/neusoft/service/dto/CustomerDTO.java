package com.neusoft.service.dto;

import java.time.LocalDate;

public class CustomerDTO {
    private Long id;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private Integer age;
    private String idNumber;
    private String bloodType;
    private String familyContact;
    private String phone;
    private String building; // 楼栋(固定606)
    private Long roomId;
    private Long bedId;
    private String roomNumber;
    private String bedNumber;
    private LocalDate checkInDate;
    private LocalDate contractExpiryDate;
    private Long nursingLevelId;
    private Long healthManagerId;

    // getters and setters
}