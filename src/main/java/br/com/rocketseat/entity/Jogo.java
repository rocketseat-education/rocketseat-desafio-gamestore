package br.com.rocketseat.entity;

import br.com.rocketseat.enums.Genero;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "jogos")
public class Jogo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genero genero;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false, updatable = false)
    private LocalDate dataCadastro;

    protected Jogo() {}

    public Jogo(String titulo, Genero genero, BigDecimal preco) {
        this.titulo = titulo;
        this.genero = genero;
        this.preco = preco;
        this.dataCadastro = LocalDate.now();
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public Genero getGenero() { return genero; }
    public BigDecimal getPreco() { return preco; }
    public LocalDate getDataCadastro() { return dataCadastro; }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Jogo jogo
                && getId() != null && getId().equals(jogo.getId());
    }

    @Override
    public int hashCode() { return Jogo.class.hashCode(); }

    @Override
    public String toString() { return "Jogo[id=" + id + ", titulo=" + titulo + "]"; }
}
