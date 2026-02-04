package thuchanh.ngohuuduc.viewmodels;

import lombok.Builder;
import thuchanh.ngohuuduc.entities.User;

@Builder
public record UserGetVm(Long id, String username, String email, String phone, String provider) {
    public static UserGetVm from(User user) {
        return UserGetVm.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .provider(user.getProvider())
                .build();
    }
}
