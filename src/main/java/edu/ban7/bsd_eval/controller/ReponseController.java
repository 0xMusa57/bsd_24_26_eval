package edu.ban7.bsd_eval.controller;

import edu.ban7.bsd_eval.entity.*;
import edu.ban7.bsd_eval.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reponses")
public class ReponseController {

    @Autowired private ReponseRepository reponseRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private AppUserRepository appUserRepository;

    /**
     * Soumettre une réponse pour une question donnée
     */
    @PostMapping("/question/{questionId}")
    public ResponseEntity<?> submitReponse(@PathVariable Integer questionId,
                                           @RequestBody Reponse reponse,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null) return ResponseEntity.notFound().build();

        AppUser user = appUserRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // Vérifier si l'utilisateur a déjà répondu à cette question
        reponseRepository.findByQuestionIdAndAppUserId(questionId, user.getId())
                .ifPresent(r -> reponseRepository.delete(r));

        reponse.setQuestion(question);
        reponse.setAppUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponseRepository.save(reponse));
    }

    @GetMapping("/question/{questionId}")
    public List<Reponse> getByQuestion(@PathVariable Integer questionId) {
        return reponseRepository.findByQuestionId(questionId);
    }
}
