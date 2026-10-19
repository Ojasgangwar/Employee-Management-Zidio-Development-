package com.EMPMANAGE.Security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.EMPMANAGE.Entity.UserAuthentication;
import com.EMPMANAGE.Enum.Permission;
import com.EMPMANAGE.Repository.UserAuthRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAuthRepository userRepo;

    public CustomUserDetailsService(UserAuthRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String userOfficialEmail)
            throws UsernameNotFoundException {
        UserAuthentication user = userRepo.findByUserOfficialEmail(userOfficialEmail)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + userOfficialEmail));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        Set<Permission> permissions =
                RoleBasedPermission.getRoleBasePermission().get(user.getRole());
        if (permissions != null) {
            permissions.stream()
                    .map(permission -> new SimpleGrantedAuthority(permission.name()))
                    .forEach(authorities::add);
        }

        return User.withUsername(user.getUserOfficialEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
