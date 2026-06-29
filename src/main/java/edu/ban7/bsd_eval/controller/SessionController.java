package edu.ban7.bsd_eval.controller;

import edu.ban7.bsd_eval.entity.*;
import edu.ban7.bsd_eval.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    @Autowired private SessionRepository sessionRepository;
    @Autowired private AppUserRepository appUserRepository;
    @Autowired private ProduitRepository produitRepository;
    @Autowired private QuestionRepository questionRepository;

    @GetMapping
    public List<Session> getAll() {
        return sessionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Session> getById(@PathVariable Integer id) {
        return sessionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Créer une session : sélectionne 10 produits aléatoires et crée les questions
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody Session session) {
        List<Produit> allProduits = produitRepository.findAll();
        if (allProduits.size() < 10) {
            return ResponseEntity.badRequest().body("Il faut au moins 10 produits en base");
        }
        Collections.shuffle(allProduits);
        List<Produit> selected = allProduits.subList(0, 10);

        Session saved = sessionRepository.save(session);

        for (Produit p : selected) {
            Question q = new Question();
            q.setSession(saved);
            q.setProduit(p);
            questionRepository.save(q);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(sessionRepository.findById(saved.getId()).get());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Session session) {
        if (!sessionRepository.existsById(id)) return ResponseEntity.notFound().build();
        session.setId(id);
        return ResponseEntity.ok(sessionRepository.save(session));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!sessionRepository.existsById(id)) return ResponseEntity.notFound().build();
        sessionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Un utilisateur rejoint une session
     */
    @PostMapping("/{id}/rejoindre")
    public ResponseEntity<?> rejoindre(@PathVariable Integer id,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        Session session = sessionRepository.findById(id).orElse(null);
        if (session == null) return ResponseEntity.notFound().build();

        AppUser user = appUserRepository.findByEmail(userDetails.getUsername()).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!session.getParticipants().contains(user)) {
            session.getParticipants().add(user);
            sessionRepository.save(session);
        }
        return ResponseEntity.ok(session);
    }

    /**
     * Récupérer les questions d'une session (avec produits masqués si besoin)
     */
    @GetMapping("/{id}/questions")
    public ResponseEntity<?> getQuestions(@PathVariable Integer id) {
        if (!sessionRepository.existsById(id)) return ResponseEntity.notFound().build();
        List<Question> questions = questionRepository.findBySessionId(id);
        return ResponseEntity.ok(questions);
    }
}
