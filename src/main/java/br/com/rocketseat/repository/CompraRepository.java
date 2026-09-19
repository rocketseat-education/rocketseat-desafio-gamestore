package br.com.rocketseat.repository;

import br.com.rocketseat.entity.Compra;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {
    boolean existsByClienteIdAndJogoId(Long clienteId, Long jogoId);

    @EntityGraph(attributePaths = {"cliente", "jogo"})
    List<Compra> findByClienteIdOrderByDataCompraDesc(Long clienteId);

    @Override
    @EntityGraph(attributePaths = {"cliente", "jogo"})
    List<Compra> findAll();
}
