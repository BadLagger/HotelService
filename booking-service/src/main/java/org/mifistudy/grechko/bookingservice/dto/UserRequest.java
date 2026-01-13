package org.mifistudy.grechko.bookingservice.dto;

import lombok.Data;
import org.mifistudy.grechko.bookingservice.entity.User;

@Data
public class UserRequest {
    private String username;
    private String password;
    private String email;
    private User.Role role;
}
