package com.example.demo.classes;

import lombok.Data;

@Data
public class NewModeratorCreatedEvent {
    private Long id;
    private String email;
    private String password;
    private String userType;
    private String firstName;
    private String lastName;
    private String username;
    private String phoneNumber;
    private String photo;
    private String address;
   
}
