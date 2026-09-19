package br.com.rocketseat.service;

import br.com.rocketseat.exception.DadosInvalidosException;

final class Validacao {
    private Validacao() {}

    static String texto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException(campo + " é obrigatório.");
        }
        String texto = valor.strip();
        if (texto.length() > 255) {
            throw new DadosInvalidosException(campo + " deve ter no máximo 255 caracteres.");
        }
        return texto;
    }

    static void id(Long valor, String campo) {
        if (valor == null || valor <= 0) {
            throw new DadosInvalidosException(campo + " deve ser um número positivo.");
        }
    }
}
