# Regra de execução — GameStore

## Como trabalhar

- Usar `plan.md` como fonte dos requisitos obrigatórios.
- Implementar uma etapa por vez, na ordem abaixo. Avançar quando seu critério de conclusão for atendido.
- Entregar funcionalidades completas: persistência, regra de negócio, endpoint e validação correspondente.
- Manter controllers pequenos, regras nos services e acesso ao banco nos repositories.
- Usar services concretos, injeção por construtor e DTOs com records quando necessários. Não criar interfaces, classes-base ou mapeadores genéricos sem necessidade.
- Usar PostgreSQL e JPA desde o início, BigDecimal para valores e relacionamentos unidirecionais de Compra para Cliente e Jogo.
- Criar testes das regras de negócio junto da funcionalidade; não deixar todos para o final.
- Não incluir extras antes de concluir o obrigatório. Não adicionar autenticação ou arquitetura avançada. Por solicitação posterior do usuário, usar Docker Compose somente para o PostgreSQL local.
- Ao terminar cada etapa, informar o que foi feito, como foi validado e o próximo passo. Não declarar uma verificação concluída se não foi executada.
- Este documento define a sequência; sua criação não inicia a implementação.

## Ordem de execução

### 1. Base executável — primeiro passo

- Verificar JDK e Maven disponíveis e a compatibilidade das versões exigidas: Java 25, Spring Boot 4.1.1 e springdoc 3.x. Não trocar versões silenciosamente.
- Configurar o pom.xml apenas com as dependências necessárias.
- Substituir o exemplo Main pela aplicação Spring Boot.
- Configurar PostgreSQL por variáveis de ambiente, com os padrões locais `gamestore` solicitados pelo usuário e definidos também no Compose.
- Para o desafio local, usar geração de schema pelo Hibernate, sem introduzir ferramenta de migrations.
- Habilitar Swagger com configuração mínima.

**Concluído quando:** o projeto compilar, a aplicação iniciar conectada ao PostgreSQL e o Swagger abrir. Se o banco não estiver disponível, registrar a pendência sem declarar esta etapa concluída.

### 2. Jogos

- Criar Genero, Jogo, repository, service, DTOs e controller.
- Implementar cadastro e todas as consultas de jogos previstas no plan.md.
- Validar título, gênero e preço; definir data automaticamente e persistir enum como texto.
- Iniciar o tratamento centralizado de erros: 400 para entrada inválida, 404 para recurso inexistente e 409 para conflito.
- Testar rejeição de preço inválido.

**Concluído quando:** for possível cadastrar e consultar jogos persistidos, incluindo filtros, ordenação, mais caro e ano atual; teste passando.

### 3. Clientes

- Criar entidade, repository, service, DTOs e controller para cadastro, listagem e busca por ID.
- Validar nome e e-mail, gerar data automaticamente e proteger e-mail único no service e no banco.
- Testar rejeição de e-mail duplicado.

**Concluído quando:** cadastro e consultas funcionarem e duplicidades forem rejeitadas; teste passando.

### 4. Compras e histórico

- Criar Compra com cliente, jogo, dataCompra e valorPago.
- Registrar compra em transação, validando existência e impedindo recompra do mesmo jogo pelo cliente.
- Garantir unicidade de cliente/jogo também no banco.
- Copiar o preço atual do jogo para valorPago, sem recalcular compras antigas.
- Expor registro/listagem de compras, histórico do cliente e total gasto.
- Testar cliente inexistente, jogo inexistente, compra repetida, soma dos valores pagos e preservação do preço histórico.

**Concluído quando:** o fluxo cliente → jogo → compra → histórico funcionar e os testes das regras passarem.

### 5. Relatórios

- Retornar gêneros com jogos cadastrados usando Set.
- Retornar quantidade de compras por gênero usando Map.
- Usar Streams e lambdas nas consultas em que tornarem o código mais claro.
- Tratar coleções vazias e total gasto sem compras como zero.

**Concluído quando:** os dois relatórios refletirem os dados cadastrados, inclusive com banco vazio.

### 6. Validação final e documentação

- Executar a suíte de testes, com pelo menos cinco testes de regras de negócio.
- Conferir todos os endpoints exigidos e seus erros pelo Swagger.
- Validar persistência após reiniciar a aplicação.
- Documentar pré-requisitos, banco, variáveis de ambiente e comandos de execução/testes em README curto.

**Concluído quando:** todos os requisitos obrigatórios estiverem implementados e verificados, com instruções suficientes para executar o projeto.

## Situação da execução

- Etapas 1 a 5 implementadas e verificadas: base, jogos, clientes, compras e relatórios.
- PostgreSQL 17 executando pelo `compose.yaml`, com banco, usuário e senha local `gamestore`.
- Maven Wrapper incluído; Java 25 e versões exigidas mantidos.
- 26 testes unitários e 9 testes de integração passaram, sem falhas ou testes ignorados.
- Todas as 17 operações HTTP verificadas, incluindo os erros e tentativas simultâneas de cadastro/compra duplicados.
- Preservação do valor pago e persistência após reiniciar a aplicação verificadas no PostgreSQL.
- Schema isolado de integração removido ao terminar os testes.
- README com configuração, Compose, Swagger, endpoints e comandos de testes.
- Swagger UI e OpenAPI validados por HTTP; conferência visual não executada porque nenhum navegador estava disponível na sessão.
