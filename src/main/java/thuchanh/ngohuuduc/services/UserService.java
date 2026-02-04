package thuchanh.ngohuuduc.services;

import thuchanh.ngohuuduc.constants.Provider;
import thuchanh.ngohuuduc.entities.User;
import thuchanh.ngohuuduc.repositories.IRoleRepository;
import thuchanh.ngohuuduc.repositories.IUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = { Exception.class, Throwable.class })
    public void save(@NotNull User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = { Exception.class, Throwable.class })
    public void setDefaultRole(String username) {
        var userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            roleRepository.findByName("USER").ifPresent(role -> {
                user.getRoles().add(role);
                userRepository.save(user);
            });
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void saveOauthUser(String email, @NotNull String username) {
        if (userRepository.findByUsername(username).isPresent())
            return;
        var user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(username));
        user.setProvider(Provider.GOOGLE.value);

        // Add default USER role if exists
        roleRepository.findByName("USER").ifPresent(role -> user.getRoles().add(role));

        userRepository.save(user);
    }

}
