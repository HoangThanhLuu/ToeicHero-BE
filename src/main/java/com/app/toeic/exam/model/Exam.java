package com.app.toeic.exam.model;


import com.app.toeic.part.model.Part;
import com.app.toeic.topic.model.Topic;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "exam")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer examId;
    private String examName;
    private String examImage;
    
    @Column(columnDefinition = "TEXT")
    private String examAudio;
    @Column(columnDefinition = "TEXT")
    private String audioPart1;
    @Column(columnDefinition = "TEXT")
    private String audioPart2;
    @Column(columnDefinition = "TEXT")
    private String audioPart3;
    @Column(columnDefinition = "TEXT")
    private String audioPart4;
    private String status = "ACTIVE";
    private Integer numberOfUserDoExam = 0;
    private Double price = 0.0;

    @JsonIgnore
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonIgnore
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    @Index(name = "topic_id_index")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Topic topic;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    @JsonBackReference
    @OrderBy("partId ASC")
    private Set<Part> parts = new HashSet<>();
}
