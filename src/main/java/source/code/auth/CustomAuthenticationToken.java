package source.code.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {
  private final Integer userId;

  public CustomAuthenticationToken(Object principal, Integer userId, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
    this.userId = userId;
  }

  public Integer getUserId() {
    return userId;
  }
}
