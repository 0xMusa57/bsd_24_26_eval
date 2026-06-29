package edu.ban7.bsd_eval.repository;

import edu.ban7.bsd_eval.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findBySessionId(Integer sessionId);
}
