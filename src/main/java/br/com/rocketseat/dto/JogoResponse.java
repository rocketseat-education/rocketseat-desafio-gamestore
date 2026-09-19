package br.com.rocketseat.dto;

import br.com.rocketseat.entity.Jogo;
import br.com.rocketseat.enums.Genero;
import java.math.BigDecimal;
import java.time.LocalDate;

public record JogoResponse(Long id, String titulo, Genero genero, BigDecimal preco, LocalDate dataCadastro) {
    public static JogoResponse de(Jogo jogo) {
        return new JogoResponse(jogo.getId(), jogo.getTitulo(), jogo.getGenero(),
                jogo.getPreco(), jogo.getDataCadastro());
    }
}
