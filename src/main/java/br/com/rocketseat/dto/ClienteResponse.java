package br.com.rocketseat.dto;

import br.com.rocketseat.entity.Cliente;
import java.time.LocalDate;

public record ClienteResponse(Long id, String nome, String email, LocalDate dataCadastro) {
    public static ClienteResponse de(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getEmail(), cliente.getDataCadastro());
    }
}
