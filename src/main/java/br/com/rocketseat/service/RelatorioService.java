package br.com.rocketseat.service;

import br.com.rocketseat.entity.Jogo;
import br.com.rocketseat.enums.Genero;
import br.com.rocketseat.repository.CompraRepository;
import br.com.rocketseat.repository.JogoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RelatorioService {
    private final JogoRepository jogos;
    private final CompraRepository compras;

    public RelatorioService(JogoRepository jogos, CompraRepository compras) {
        this.jogos = jogos;
        this.compras = compras;
    }

    public Set<Genero> generos() {
        return jogos.findAll().stream().map(Jogo::getGenero).collect(Collectors.toSet());
    }

    public Map<Genero, Long> vendasPorGenero() {
        return compras.findAll().stream().collect(
                Collectors.groupingBy(compra -> compra.getJogo().getGenero(), Collectors.counting()));
    }
}
