package br.com.rocketseat.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "compras", uniqueConstraints =
        @UniqueConstraint(name = "uk_compra_cliente_jogo", columnNames = {"cliente_id", "jogo_id"}))
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false, updatable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jogo_id", nullable = false, updatable = false)
    private Jogo jogo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCompra;

    @Column(nullable = false, updatable = false, precision = 12, scale = 2)
    private BigDecimal valorPago;

    protected Compra() {}

    public Compra(Cliente cliente, Jogo jogo) {
        this.cliente = cliente;
        this.jogo = jogo;
        this.dataCompra = LocalDateTime.now();
        this.valorPago = jogo.getPreco();
    }

    public Long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Jogo getJogo() { return jogo; }
    public LocalDateTime getDataCompra() { return dataCompra; }
    public BigDecimal getValorPago() { return valorPago; }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Compra compra
                && getId() != null && getId().equals(compra.getId());
    }

    @Override
    public int hashCode() { return Compra.class.hashCode(); }

    @Override
    public String toString() { return "Compra[id=" + id + ", valorPago=" + valorPago + "]"; }
}
