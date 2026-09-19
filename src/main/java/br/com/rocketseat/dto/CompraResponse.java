package br.com.rocketseat.dto;

import br.com.rocketseat.entity.Compra;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompraResponse(Long id, Long clienteId, String nomeCliente,
                             Long jogoId, String tituloJogo, BigDecimal valorPago, LocalDateTime dataCompra) {
    public static CompraResponse de(Compra compra) {
        return new CompraResponse(compra.getId(), compra.getCliente().getId(), compra.getCliente().getNome(),
                compra.getJogo().getId(), compra.getJogo().getTitulo(), compra.getValorPago(), compra.getDataCompra());
    }
}
