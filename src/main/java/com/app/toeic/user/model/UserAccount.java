package com.app.toeic.user.model;


import com.app.toeic.chatai.model.ChatHistory;
import com.app.toeic.comment.model.Comment;
import com.app.toeic.course.model.Enrollment;
import com.app.toeic.message.model.Conversation;
import com.app.toeic.message.model.Message;
import com.app.toeic.message.model.Participant;
import com.app.toeic.user.enums.UType;
import com.app.toeic.userexam.model.UserExamHistory;
import com.app.toeic.user.enums.EUser;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "user_account", indexes = {
        @Index(name = "email", columnList = "email"),
        @Index(name = "status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAccount implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer userId;

    @Column(nullable = false)
    String password;
    String fullName;
    String phone;

    @Column(length = 3000)
    String address;

    @Column(unique = true, nullable = false)
    String email;

    @Column(columnDefinition = "TEXT")
    String avatar;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    UType userType = UType.STANDARD_USER;


    @Builder.Default
    @Enumerated(EnumType.STRING)
    EUser status = EUser.ACTIVE;

    String provider;

    @JsonIgnore
    @CreationTimestamp
    LocalDateTime createdAt;

    @JsonIgnore
    @UpdateTimestamp
    LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleId"))
    @Builder.Default
    Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    @Builder.Default
    Set<UserExamHistory> userExamHistories = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    @Builder.Default
    Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    @Builder.Default
    Set<ChatHistory> chatHistories = new HashSet<>();

    @ManyToMany
    @JsonBackReference
    @Builder.Default
    @JoinTable(
            name = "participants",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "conversation_id")
    )
    Set<Conversation> conversations = new HashSet<>();

    @OneToMany(mappedBy = "sender")
    @JsonBackReference
    @Builder.Default
    Set<Message> messages = new HashSet<>();

    @OneToMany(mappedBy = "user")
    @JsonBackReference
    @Builder.Default
    Set<Participant> conversationParticipants = new HashSet<>();

    @OneToMany(mappedBy = "creator")
    @JsonBackReference
    @Builder.Default
    Set<Conversation> createdConversations = new HashSet<>();

    @OneToMany(mappedBy = "userAccount")
    @JsonBackReference
    @Builder.Default
    Set<Enrollment> enrollments = new HashSet<>();

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        this.roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getRoleName())));
        return authorities;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return this.email;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return this.password;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
