    package com.example.security.user;

    import com.example.security.role.Role;
    import com.fasterxml.jackson.annotation.JsonIdentityInfo;
    import com.fasterxml.jackson.annotation.ObjectIdGenerators;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import lombok.experimental.SuperBuilder;
    import org.springframework.data.annotation.CreatedDate;
    import org.springframework.data.annotation.LastModifiedDate;
    import org.springframework.data.jpa.domain.support.AuditingEntityListener;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;

    import java.security.Principal;
    import java.time.LocalDateTime;
    import java.util.Collection;
    import java.util.List;
    import java.util.stream.Collectors;


    @Entity
    @Inheritance(strategy = InheritanceType.JOINED)  // ← JOINED strategy creates 3 tables
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    @Table(name = "_user")
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id"
    )
    public class User implements UserDetails, Principal {

        @Id
        @GeneratedValue(
                strategy = GenerationType.SEQUENCE,
                generator = "user_seq"
        )
        @SequenceGenerator(
                name = "user_seq",
                sequenceName = "user_seq",
                allocationSize = 1,
                initialValue = 0
        )
        private Long id;
        private String firstname;
        private String lastname;
        @Column(unique = true)
        private String email;
        private String password;
        private boolean accountLocked;
        private boolean enabled;
        @CreatedDate
        @Column(updatable = false)
        private LocalDateTime createdAt;
        @LastModifiedDate
        @Column(insertable = false)
        private LocalDateTime lastModifiedAt;

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(
                name = "users_roles",
                joinColumns = @JoinColumn(name = "_user_id"),
                inverseJoinColumns =@JoinColumn(name =  "role_id")
        )
        private List<Role> roles;

        @OneToMany
        private List<Token> tokens;

        public User(String firstname, String lastname, String email, String password) {
            this.firstname = firstname;
            this.lastname = lastname;
            this.email = email;
            this.password = password;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return this.roles.stream().map(r->new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public String getName() {
            return email;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return !accountLocked;
        }

        @Override
        public boolean isCredentialsNonExpired() {
              return true;
         }

        @Override
        public boolean isEnabled() {
            return enabled;
        }


         public String getFullName(){
            return firstname +" "+ lastname;
         }
    }
