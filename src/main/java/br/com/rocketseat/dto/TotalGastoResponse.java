package br.com.rocketseat.dto;

import java.math.BigDecimal;

public record TotalGastoResponse(Long clienteId, BigDecimal totalGasto) {}
