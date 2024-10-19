package com.app.toeic.question.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_image")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer questionImageId;
    private String questionImage;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @org.hibernate.annotations.Index(name = "question_id_index")
    private Question question;
}
