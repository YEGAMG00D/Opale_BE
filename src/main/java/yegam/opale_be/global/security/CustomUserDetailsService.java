package yegam.userservice.global.security;

import yegam.userservice.domain.user.entity.User;
import yegam.userservice.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    User user;

    // identifier가 숫자면 → id로 조회
    if (identifier.matches("\\d+")) {
      Long id = Long.parseLong(identifier);
      user = userRepository.findById(id)
          .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다 (id): " + id));
    }
    // 아니면 이메일로 조회
    else {
      user = userRepository.findByEmail(identifier)
          .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다 (email): " + identifier));
    }

    return new CustomUserDetails(user);
  }

}
