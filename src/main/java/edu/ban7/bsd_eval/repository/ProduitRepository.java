package edu.ban7.bsd_eval.repository;

import edu.ban7.bsd_eval.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProduitRepository extends JpaRepository<Produit, Integer> {
}
