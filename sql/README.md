# Massa de dados

`seed.sql` adiciona **24 jogos, 12 clientes e 54 compras** em um banco vazio,
depois que a API tiver criado as tabelas. Os dados e preços são fictícios.

- Quatro jogos por gênero, com preços de R$ 24,90 a R$ 299,90.
- Doze jogos cadastrados no ano atual e doze no ano anterior.
- Compras nos seis gêneros, com quantidades diferentes para comparar relatórios.
- Dez clientes com compras e dois sem compras: Larissa e Marcos.
- Cinco jogos sem vendas.
- Compras em datas variadas, incluindo ano anterior, e valores pagos diferentes do preço atual.

## Carregar

Na raiz do projeto, com as tabelas já criadas:

```powershell
docker compose up -d --wait
docker cp sql/seed.sql gamestore-local-postgres:/tmp/gamestore-seed.sql
docker exec gamestore-local-postgres psql -v ON_ERROR_STOP=1 -U gamestore -d gamestore -f /tmp/gamestore-seed.sql
```

Também pode abrir `seed.sql` no cliente SQL e executá-lo no banco `gamestore`.

A carga é transacional e pode ser repetida sem duplicar registros. Preserva dados
existentes, identifica jogos pelo título e clientes pelo e-mail, e ignora compras
já registradas. Não depende de IDs fixos e não atualiza preços ou datas existentes.
Se houver jogos com o mesmo título, utiliza o de menor ID. Os totais podem variar
quando parte da massa já existir no banco.

Na primeira execução neste projeto, os registros existentes foram preservados:
o banco ficou com **25 jogos, 13 clientes e 55 compras**.

Consulte os resultados no Swagger pelas rotas `/relatorios/vendas-por-genero`,
`/jogos/ordenados-por-preco`, `/jogos/cadastrados-no-ano-atual`,
`/clientes/{id}/compras` e `/clientes/{id}/total-gasto`.
