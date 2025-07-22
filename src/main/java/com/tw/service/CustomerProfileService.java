package com.tw.service;

import com.tw.dto.CustomerProfileDto;
import com.tw.entity.CustomerProfile;

public interface CustomerProfileService {
    CustomerProfile createProfile(CustomerProfileDto dto);
}
