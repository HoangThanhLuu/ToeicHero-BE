package com.app.toeic.repository;

import com.app.toeic.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ITopicRepository extends JpaRepository<Topic, Integer> {
}
