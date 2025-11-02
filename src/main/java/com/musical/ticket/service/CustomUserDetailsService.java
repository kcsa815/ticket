package com.musical.ticket.service;
//Spring Security가 DB의 User를 조회할 때 사용할 서비스
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.musical.ticket.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional // DB 조회이므로
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Spring Security는 'username'으로 받지만, 우리는 'email'을 사용
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 찾을 수 없습니다: " + email));
        
        // .orElseThrow()를 사용하면, userRepository.findByEmail(email)이
        // Optional<User>를 반환하므로, User 객체가 바로 UserDetails로 반환됨
        // (User 엔티티가 UserDetails를 구현했기 때문)
    }
}
