package edu.ban7.bsd_eval.controller;

import edu.ban7.bsd_eval.dto.ClassementDto;
import edu.ban7.bsd_eval.entity.*;
import edu.ban7.bsd_eval.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/classement")
public class ClassementController {

    @Autowired private SessionRepository sessionRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private ReponseRepository reponseRepository;
    @Autowired private AppUserRepository appUserRepository;

    /**
     * Classement pour une session.
     * Score = 0 au départ, on soustrait l'écart absolu pour chaque réponse.
     * Tri décroissant (meilleur = moins d'écart).
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> classementSession(@PathVariable Integer sessionId) {
        Session session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null) return ResponseEntity.notFound().build();

        List<Question> questions = questionRepository.findBySessionId(sessionId);

        // Map userId -> score (commence à 0, on retire les écarts)
        Map<Integer, Integer> scores = new HashMap<>();

        for (Question question : questions) {
            int prixReel = question.getProduit().getPrix();
            List<Reponse> reponses = reponseRepository.findByQuestionId(question.getId());
            for (Reponse r : reponses) {
                int ecart = Math.abs(r.getPrix() - prixReel);
                scores.merge(r.getAppUser().getId(), -ecart, Integer::sum);
            }
        }

        // Construire le classement trié
        List<ClassementDto> classement = scores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .map(e -> {
                    AppUser user = appUserRepository.findById(e.getKey()).orElse(null);
                    String email = user != null ? user.getEmail() : "inconnu";
                    return new ClassementDto(email, e.getValue());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(classement);
    }
}
