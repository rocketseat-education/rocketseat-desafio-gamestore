-- Massa fictícia para desenvolvimento. Preços ilustrativos, sem relação com ofertas reais.
-- Execute após a API criar as tabelas. Preserva os registros existentes.
-- Pode ser executado novamente: jogos por título, clientes por e-mail e compras por cliente/jogo.
-- Não fixa IDs nem altera as sequences. Toda a carga é uma única transação.
BEGIN;

-- Evita duas execuções simultâneas desta carga.
SELECT pg_advisory_xact_lock(20260919, 1);

INSERT INTO public.jogos (titulo, genero, preco, data_cadastro)
SELECT dados.titulo, dados.genero, dados.preco,
       (date_trunc('year', CURRENT_DATE) -
           CASE WHEN dados.ano_anterior THEN INTERVAL '1 year' ELSE INTERVAL '0 years' END)::date
FROM (VALUES
    ('Hades', 'ACAO', 73.99, true),
    ('DOOM Eternal', 'ACAO', 149.90, true),
    ('Dead Cells', 'ACAO', 59.99, false),
    ('Devil May Cry 5', 'ACAO', 99.90, false),
    ('Ori and the Blind Forest', 'AVENTURA', 39.90, true),
    ('It Takes Two', 'AVENTURA', 199.90, true),
    ('Stray', 'AVENTURA', 129.90, false),
    ('Journey', 'AVENTURA', 24.90, false),
    ('Chrono Trigger', 'RPG', 49.90, true),
    ('The Witcher 3', 'RPG', 129.90, true),
    ('Baldur''s Gate 3', 'RPG', 299.90, false),
    ('Stardew Valley', 'RPG', 29.90, false),
    ('Civilization VI', 'ESTRATEGIA', 129.90, true),
    ('Age of Empires II', 'ESTRATEGIA', 79.90, true),
    ('Into the Breach', 'ESTRATEGIA', 37.99, false),
    ('Frostpunk', 'ESTRATEGIA', 89.90, false),
    ('EA Sports FC 25', 'ESPORTE', 299.90, true),
    ('NBA 2K25', 'ESPORTE', 249.90, true),
    ('Tony Hawk''s Pro Skater 1 + 2', 'ESPORTE', 159.90, false),
    ('Golf With Your Friends', 'ESPORTE', 39.90, false),
    ('Forza Horizon 5', 'CORRIDA', 249.90, true),
    ('Need for Speed Heat', 'CORRIDA', 149.90, true),
    ('Assetto Corsa', 'CORRIDA', 49.90, false),
    ('Hot Wheels Unleashed', 'CORRIDA', 99.90, false)
) AS dados(titulo, genero, preco, ano_anterior)
WHERE NOT EXISTS (
    SELECT 1 FROM public.jogos j WHERE lower(j.titulo) = lower(dados.titulo)
);

INSERT INTO public.clientes (nome, email, data_cadastro)
SELECT dados.nome, dados.email, CURRENT_DATE - dados.dias_cadastro
FROM (VALUES
    ('Ana Silva', 'ana.silva@example.com', 800),
    ('Bruno Costa', 'bruno.costa@example.com', 730),
    ('Carla Souza', 'carla.souza@example.com', 600),
    ('Diego Santos', 'diego.santos@example.com', 450),
    ('Elisa Oliveira', 'elisa.oliveira@example.com', 365),
    ('Fabio Lima', 'fabio.lima@example.com', 240),
    ('Gabriela Alves', 'gabriela.alves@example.com', 180),
    ('Hugo Pereira', 'hugo.pereira@example.com', 120),
    ('Isabela Rocha', 'isabela.rocha@example.com', 90),
    ('Joao Martins', 'joao.martins@example.com', 60),
    ('Larissa Melo', 'larissa.melo@example.com', 30),
    ('Marcos Nunes', 'marcos.nunes@example.com', 7)
) AS dados(nome, email, dias_cadastro)
ON CONFLICT (email) DO NOTHING;

-- 54 compras distribuídas entre 10 clientes, com vendas nos seis gêneros.
-- Larissa e Marcos ficam sem compras para testar histórico vazio e total zero.
-- Parte dos valores pagos difere do preço atual para exercitar o histórico.
INSERT INTO public.compras (cliente_id, jogo_id, data_compra, valor_pago)
SELECT cliente.id, jogo.id,
       LEAST(
           GREATEST(CURRENT_DATE - dados.dias_compra, cliente.data_cadastro, jogo.data_cadastro)
               + TIME '10:30:00',
           LOCALTIMESTAMP
       ),
       dados.valor_pago
FROM (VALUES
    ('ana.silva@example.com', 'Hades', 400, 55.49),
    ('ana.silva@example.com', 'Ori and the Blind Forest', 141, 39.90),
    ('ana.silva@example.com', 'Chrono Trigger', 132, 49.90),
    ('ana.silva@example.com', 'The Witcher 3', 123, 97.43),
    ('ana.silva@example.com', 'Baldur''s Gate 3', 114, 299.90),
    ('ana.silva@example.com', 'Civilization VI', 105, 129.90),
    ('ana.silva@example.com', 'EA Sports FC 25', 96, 224.92),
    ('ana.silva@example.com', 'Forza Horizon 5', 87, 249.90),
    ('ana.silva@example.com', 'Need for Speed Heat', 78, 149.90),
    ('ana.silva@example.com', 'Stardew Valley', 69, 22.42),
    ('bruno.costa@example.com', 'Hades', 143, 73.99),
    ('bruno.costa@example.com', 'DOOM Eternal', 134, 149.90),
    ('bruno.costa@example.com', 'It Takes Two', 125, 149.93),
    ('bruno.costa@example.com', 'Chrono Trigger', 116, 49.90),
    ('bruno.costa@example.com', 'Age of Empires II', 107, 79.90),
    ('bruno.costa@example.com', 'NBA 2K25', 98, 187.43),
    ('bruno.costa@example.com', 'Forza Horizon 5', 89, 249.90),
    ('bruno.costa@example.com', 'Assetto Corsa', 80, 49.90),
    ('carla.souza@example.com', 'Hades', 136, 73.99),
    ('carla.souza@example.com', 'Dead Cells', 127, 44.99),
    ('carla.souza@example.com', 'Ori and the Blind Forest', 118, 39.90),
    ('carla.souza@example.com', 'The Witcher 3', 109, 129.90),
    ('carla.souza@example.com', 'Baldur''s Gate 3', 100, 224.92),
    ('carla.souza@example.com', 'Civilization VI', 91, 129.90),
    ('carla.souza@example.com', 'Tony Hawk''s Pro Skater 1 + 2', 82, 159.90),
    ('diego.santos@example.com', 'DOOM Eternal', 129, 112.43),
    ('diego.santos@example.com', 'It Takes Two', 120, 199.90),
    ('diego.santos@example.com', 'Chrono Trigger', 111, 49.90),
    ('diego.santos@example.com', 'Into the Breach', 102, 28.49),
    ('diego.santos@example.com', 'EA Sports FC 25', 93, 299.90),
    ('diego.santos@example.com', 'Need for Speed Heat', 84, 149.90),
    ('elisa.oliveira@example.com', 'Hades', 122, 73.99),
    ('elisa.oliveira@example.com', 'Stray', 113, 129.90),
    ('elisa.oliveira@example.com', 'Baldur''s Gate 3', 104, 224.92),
    ('elisa.oliveira@example.com', 'Stardew Valley', 95, 29.90),
    ('elisa.oliveira@example.com', 'Age of Empires II', 86, 79.90),
    ('elisa.oliveira@example.com', 'Forza Horizon 5', 77, 187.43),
    ('fabio.lima@example.com', 'Dead Cells', 115, 59.99),
    ('fabio.lima@example.com', 'Ori and the Blind Forest', 106, 29.92),
    ('fabio.lima@example.com', 'The Witcher 3', 97, 129.90),
    ('fabio.lima@example.com', 'NBA 2K25', 88, 249.90),
    ('fabio.lima@example.com', 'Assetto Corsa', 79, 37.42),
    ('gabriela.alves@example.com', 'Hades', 108, 55.49),
    ('gabriela.alves@example.com', 'It Takes Two', 99, 199.90),
    ('gabriela.alves@example.com', 'Civilization VI', 90, 129.90),
    ('gabriela.alves@example.com', 'Forza Horizon 5', 81, 187.43),
    ('hugo.pereira@example.com', 'Stray', 101, 129.90),
    ('hugo.pereira@example.com', 'Baldur''s Gate 3', 92, 299.90),
    ('hugo.pereira@example.com', 'Tony Hawk''s Pro Skater 1 + 2', 83, 119.93),
    ('isabela.rocha@example.com', 'Chrono Trigger', 80, 49.90),
    ('isabela.rocha@example.com', 'Into the Breach', 80, 28.49),
    ('isabela.rocha@example.com', 'Need for Speed Heat', 76, 149.90),
    ('joao.martins@example.com', 'Stardew Valley', 50, 22.42),
    ('joao.martins@example.com', 'EA Sports FC 25', 50, 299.90)
) AS dados(email, titulo, dias_compra, valor_pago)
JOIN public.clientes cliente ON cliente.email = dados.email
-- O desafio permite títulos repetidos: usa o menor ID, sem multiplicar as compras.
JOIN LATERAL (
    SELECT j.id, j.data_cadastro FROM public.jogos j
    WHERE lower(j.titulo) = lower(dados.titulo)
    ORDER BY j.id LIMIT 1
) jogo ON true
ON CONFLICT (cliente_id, jogo_id) DO NOTHING;

COMMIT;

-- Resumo do banco após a carga (inclui dados que já existiam).
SELECT
    (SELECT count(*) FROM public.jogos) AS jogos,
    (SELECT count(*) FROM public.clientes) AS clientes,
    (SELECT count(*) FROM public.compras) AS compras;

SELECT j.genero, count(*) AS vendas, sum(c.valor_pago) AS faturamento
FROM public.compras c
JOIN public.jogos j ON j.id = c.jogo_id
GROUP BY j.genero
ORDER BY vendas DESC, j.genero;
