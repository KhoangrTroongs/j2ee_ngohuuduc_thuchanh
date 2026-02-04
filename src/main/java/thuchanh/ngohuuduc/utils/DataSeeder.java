package thuchanh.ngohuuduc.utils;

import thuchanh.ngohuuduc.entities.Role;
import thuchanh.ngohuuduc.entities.User;
import thuchanh.ngohuuduc.repositories.IRoleRepository;
import thuchanh.ngohuuduc.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        loadRoles();
        assignDefaultRoleToExistingUsers();
    }

    private void loadRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Administrator role with full access");
            roleRepository.save(adminRole);

            Role userRole = new Role();
            userRole.setName("USER");
            userRole.setDescription("Standard user role");
            roleRepository.save(userRole);
        } else {
            // Ensure specific roles exist even if table is not empty
            if (roleRepository.findByName("ADMIN").isEmpty()) {
                Role adminRole = new Role();
                adminRole.setName("ADMIN");
                roleRepository.save(adminRole);
            }
            if (roleRepository.findByName("USER").isEmpty()) {
                Role userRole = new Role();
                userRole.setName("USER");
                roleRepository.save(userRole);
            }
        }
    }

    private void assignDefaultRoleToExistingUsers() {
        Role userRole = roleRepository.findByName("USER").orElse(null);
        if (userRole == null)
            return;

        List<User> usersWithoutRoles = userRepository.findAll().stream()
                .filter(user -> user.getRoles().isEmpty())
                .toList();

        for (User user : usersWithoutRoles) {
            user.getRoles().add(userRole);
            userRepository.save(user);
        }
    }
}
