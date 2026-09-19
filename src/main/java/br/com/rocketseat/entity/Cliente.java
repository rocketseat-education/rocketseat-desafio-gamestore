package br.com.rocketseat.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "clientes", uniqueConstraints = @UniqueConstraint(name = "uk_cliente_email", columnNames = "email"))
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, updatable = false)
    private LocalDate dataCadastro;

    protected Cliente() {}

    public Cliente(String nome, String email) {
        this.nome = nome;
        this.email = email;
        this.dataCadastro = LocalDate.now();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public LocalDate getDataCadastro() { return dataCadastro; }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof Cliente cliente
                && getId() != null && getId().equals(cliente.getId());
    }

    @Override
    public int hashCode() { return Cliente.class.hashCode(); }

    @Override
    public String toString() { return "Cliente[id=" + id + ", nome=" + nome + "]"; }
}
