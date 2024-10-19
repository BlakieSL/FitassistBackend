package source.code.helper;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import source.code.dto.other.UserCredentialsDto;

public class UserDetailsHelper {
  public static UserDetails buildUserDetails(UserCredentialsDto dto) {
    return User.builder()
            .username(dto.getEmail())
            .password(dto.getPassword())
            .roles(dto.getRoles().toArray(String[]::new))
            .build();
  }
}
