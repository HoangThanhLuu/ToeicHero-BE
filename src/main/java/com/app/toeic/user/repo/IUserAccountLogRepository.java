package com.app.toeic.user.repo;

import com.app.toeic.user.model.UserAccountLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserAccountLogRepository extends JpaRepository<UserAccountLog, Integer> {
}
