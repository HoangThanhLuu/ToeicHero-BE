package com.app.toeic.email.model;


import com.app.toeic.util.Constant;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "email_template", indexes = {
        @Index(name = "email_template_code_index", columnList = "templateCode"),
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String name;

    private String subject;

    @Column(unique = true)
    private String templateCode;

    @Column(columnDefinition = "TEXT")
    private String templateContent;
    @Builder.Default
    private String status = Constant.STATUS_INACTIVE;
}
