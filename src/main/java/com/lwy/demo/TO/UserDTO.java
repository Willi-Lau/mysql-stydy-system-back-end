package com.lwy.demo.TO;

import com.lwy.demo.entity.User;
import lombok.*;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class UserDTO implements Serializable {
    String studentNumber;
    String name;
    String identityCardNumber;
    String password;
    String phone;
    String university;
    String className;
    Integer sqlNumber;
    Integer selectNumber;
    Integer updateNumber;
    Integer insertNumber;
    Integer deleteNumber;
    Integer otherNumber;
    Integer successNumber;
    Integer errorNumber;
    String state;

    public static UserDTO userTransformUserDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setStudentNumber(user.getStudentNumber());
        userDTO.setName(user.getName());
        userDTO.setIdentityCardNumber(user.getIdentityCardNumber());
        userDTO.setPassword(user.getPassword());
        userDTO.setUniversity(user.getUniversity());
        userDTO.setClassName(user.getClassName());
        userDTO.setPhone(user.getPhone());
        return userDTO;
    }
}
