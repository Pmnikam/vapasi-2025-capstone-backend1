package com.tw.service.impl;

import com.tw.dto.CustomerProfileDto;
import com.tw.entity.CustomerProfile;
import com.tw.entity.Users;
import com.tw.repository.CustomerProfileRepository;
import com.tw.repository.UserRepository;
import com.tw.service.CustomerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerProfileServiceImpl implements CustomerProfileService {
    @Autowired
    private CustomerProfileRepository customerProfileRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public CustomerProfile createProfile(CustomerProfileDto dto) {
        Users user = new Users();
        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setDob(dto.getDob());
        customerProfile.setMobile(dto.getMobile());
        customerProfile.setAddress(dto.getAddress());
        customerProfile.setAadharNo(dto.getAadharNo());
        customerProfile.setPanNo(dto.getPanNo());
        customerProfile.setUser(user);
        return customerProfileRepository.save(customerProfile);
    }
}
