package com.example.taskmanager.user.infrastructure.auth;

import com.example.taskmanager.user.domain.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class CustomUserDetailsService implements UserDetailsService {

  private final UserFacade userFacade;

  @Override
  @Cacheable(cacheNames = "user auth cache")
  public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

    return userFacade
        .getUserAuthDTO(userName)
        .map(userAuthDTO -> User.builder()
                    .username(userAuthDTO.username())
                    .password(userAuthDTO.password())
                    .authorities(userAuthDTO.role())
                    .build())
        .getOrElseThrow(() -> new UsernameNotFoundException(userName));
  }
}
