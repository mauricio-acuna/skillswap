package com.skillswap.backend.security;

import com.skillswap.backend.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Custom UserDetails implementation for Spring Security
 * Represents authenticated user principal
 */
public class UserPrincipal implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String userType;
    private User.AccountStatus accountStatus;
    private boolean emailVerified;
    private boolean phoneVerified;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String email, String password, String firstName, String lastName,
                        String userType, User.AccountStatus accountStatus, boolean emailVerified,
                        boolean phoneVerified, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.accountStatus = accountStatus;
        this.emailVerified = emailVerified;
        this.phoneVerified = phoneVerified;
        this.authorities = authorities;
    }

    /**
     * Create UserPrincipal from User entity
     */
    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getUserType().toString())
        );

        return new UserPrincipal(
            user.getId(),
            user.getEmail(),
            user.getPasswordHash(),
            user.getFirstName(),
            user.getLastName(),
            user.getUserType().toString(),
            user.getAccountStatus(),
            user.getIsEmailVerified(),
            user.getIsPhoneVerified(),
            authorities
        );
    }

    // UserDetails interface methods
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !User.AccountStatus.SUSPENDED.equals(accountStatus) && 
               !User.AccountStatus.BANNED.equals(accountStatus);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return User.AccountStatus.ACTIVE.equals(accountStatus);
    }

    // Custom getters
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getUserType() {
        return userType;
    }

    public User.AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public boolean isVerified() {
        return emailVerified && phoneVerified;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(userType) || "MODERATOR".equals(userType);
    }

    public boolean canTeach() {
        return "TEACHER".equals(userType) || "BOTH".equals(userType);
    }

    public boolean canLearn() {
        return "LEARNER".equals(userType) || "BOTH".equals(userType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", userType='" + userType + '\'' +
                ", accountStatus=" + accountStatus +
                '}';
    }
}
