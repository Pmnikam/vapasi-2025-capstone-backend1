//package com.tw.controller;
//
//import com.tw.entity.CustomerProfile;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/customers")
//public class CustomerProfileController {
//    @Autowired
//    private CustomerProfileService profileService;
//
//    @PostMapping("/profile")
//    public ResponseEntity<CustomerProfile> createProfile(@RequestBody CustomerProfileDto dto) {
//        return ResponseEntity.ok(profileService.createProfile(dto));
//    }
//}
