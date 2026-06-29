package edu.ban7.bsd_eval.repository;

import edu.ban7.bsd_eval.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Integer> {
}
