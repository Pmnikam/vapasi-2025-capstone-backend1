package com.tw.service;

import com.tw.dto.CustomerProfileRequestDto;
import com.tw.dto.CustomerProfileResponseDto;

public interface CustomerProfileService {
    CustomerProfileResponseDto createProfile(CustomerProfileRequestDto dto);
    CustomerProfileResponseDto getProfileById(long id);
}