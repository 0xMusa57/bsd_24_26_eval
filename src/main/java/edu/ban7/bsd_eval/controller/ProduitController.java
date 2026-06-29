package edu.ban7.bsd_eval.controller;

import edu.ban7.bsd_eval.entity.Produit;
import edu.ban7.bsd_eval.repository.ProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitController {

    @Autowired
    private ProduitRepository produitRepository;

    @GetMapping
    public List<Produit> getAll() {
        return produitRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produit> getById(@PathVariable Integer id) {
        return produitRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Produit> create(@RequestBody Produit produit) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produitRepository.save(produit));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Produit> update(@PathVariable Integer id, @RequestBody Produit produit) {
        if (!produitRepository.existsById(id)) return ResponseEntity.notFound().build();
        produit.setId(id);
        return ResponseEntity.ok(produitRepository.save(produit));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!produitRepository.existsById(id)) return ResponseEntity.notFound().build();
        produitRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
