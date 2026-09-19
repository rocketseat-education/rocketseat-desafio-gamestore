package br.com.rocketseat.repository;

import br.com.rocketseat.entity.Jogo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JogoRepository extends JpaRepository<Jogo, Long> {
    List<Jogo> findByTituloContainingIgnoreCaseOrderByTituloAsc(String titulo);
}
