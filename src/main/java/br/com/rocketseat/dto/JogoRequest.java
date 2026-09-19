package br.com.rocketseat.dto;

import br.com.rocketseat.enums.Genero;
import java.math.BigDecimal;

public record JogoRequest(String titulo, Genero genero, BigDecimal preco) {}
