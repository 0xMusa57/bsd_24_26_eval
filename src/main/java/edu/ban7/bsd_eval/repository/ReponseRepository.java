package edu.ban7.bsd_eval.repository;

import edu.ban7.bsd_eval.entity.Reponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReponseRepository extends JpaRepository<Reponse, Integer> {
    List<Reponse> findByQuestionId(Integer questionId);
    List<Reponse> findByAppUserId(Integer userId);
    Optional<Reponse> findByQuestionIdAndAppUserId(Integer questionId, Integer userId);
}
