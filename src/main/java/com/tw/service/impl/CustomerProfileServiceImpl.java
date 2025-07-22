package com.tw.service.impl;

import com.tw.dto.CustomerProfileRequestDto;
import com.tw.dto.CustomerProfileResponseDto;
import com.tw.entity.CustomerProfile;
import com.tw.entity.Users;
import com.tw.repository.CustomerProfileRepository;
import com.tw.repository.UserRepository;
import com.tw.service.CustomerProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerProfileServiceImpl implements CustomerProfileService {
    @Autowired
    private CustomerProfileRepository customerProfileRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public CustomerProfileResponseDto createProfile(CustomerProfileRequestDto dto) {
        Users user=userRepository.findById(dto.getUserId()).orElseThrow(()->new EntityNotFoundException("User not found"));
        CustomerProfile profile=CustomerProfile.builder()
                .dob(dto.getDob())
                .mobile(dto.getMobile())
                .address(dto.getAddress())
                .aadharNo(dto.getAadharNo())
                .panNo(dto.getPanNo())
                .user(user)
                .build();
        return mapToResponse(customerProfileRepository.save(profile));
    }

    @Override
    public CustomerProfileResponseDto getProfileById(long id) {
        CustomerProfile profile=customerProfileRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Profile not found"));
        return mapToResponse(profile);
    }

    private CustomerProfileResponseDto mapToResponse(CustomerProfile profile) {
        return CustomerProfileResponseDto.builder()
                .pid(profile.getPid())
                .dob(profile.getDob())
                .mobile(profile.getMobile())
                .address(profile.getAddress())
                .aadharNo(profile.getAadharNo())
                .panNo(profile.getPanNo())
                .userId(profile.getUser().getUserId())
                .build();
    }
}
