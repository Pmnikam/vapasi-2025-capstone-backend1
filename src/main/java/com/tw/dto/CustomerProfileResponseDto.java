package com.tw.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerProfileResponseDto {
    private long pid;
    private String dob;
    private String mobile;
    private String address;
    private String aadharNo;
    private String panNo;
    private long userId;
}
