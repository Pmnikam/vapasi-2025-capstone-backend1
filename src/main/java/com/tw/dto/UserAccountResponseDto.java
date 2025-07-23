package com.tw.dto;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class UserAccountResponseDto {
        private Long id;
        private String name;
        private String email;
        private String role;

        public UserAccountResponseDto(Long id, String name, String email, String role) {
                this.id = id;
                this.name = name;
                this.email = email;
                this.role = role;
        }

}
