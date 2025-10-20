-- =========================
-- DATA.SQL - Dati Sample per Gestione Tavoli/Ordini
-- =========================

-- =========================
-- Popola Tabella TAVOLI (20 tavoli)
-- =========================
INSERT INTO tavoli (numero_nome_tavolo, stato) VALUES
                                                   ('Tavolo 1', 'LIBERO'),
                                                   ('Tavolo 2', 'LIBERO'),
                                                   ('Tavolo 3', 'OCCUPATO'),
                                                   ('Tavolo 4', 'LIBERO'),
                                                   ('Tavolo 5', 'OCCUPATO'),
                                                   ('Tavolo 6', 'RISERVATO'),
                                                   ('Tavolo 7', 'LIBERO'),
                                                   ('Tavolo 8', 'OCCUPATO'),
                                                   ('Tavolo 9', 'LIBERO'),
                                                   ('Tavolo 10', 'LIBERO'),
                                                   ('Tavolo 11', 'OCCUPATO'),
                                                   ('Tavolo 12', 'LIBERO'),
                                                   ('Tavolo VIP 1', 'RISERVATO'),
                                                   ('Tavolo VIP 2', 'LIBERO'),
                                                   ('Terrazza 1', 'OCCUPATO'),
                                                   ('Terrazza 2', 'LIBERO'),
                                                   ('Sala Privata', 'RISERVATO'),
                                                   ('Bancone 1', 'OCCUPATO'),
                                                   ('Bancone 2', 'LIBERO'),
                                                   ('Esterno 1', 'LIBERO');

-- =========================
-- Popola Tabella PRODOTTI (30 prodotti + 2 eliminati)
-- =========================

-- PIZZE (8 prodotti)
INSERT INTO prodotti (nome_prodotto, categoria_prodotto, prezzo, deleted, deleted_at) VALUES
                                                                                          ('Pizza Margherita', 'Pizza', 8.00, false, NULL),
                                                                                          ('Pizza Diavola', 'Pizza', 9.50, false, NULL),
                                                                                          ('Pizza Capricciosa', 'Pizza', 10.00, false, NULL),
                                                                                          ('Pizza 4 Formaggi', 'Pizza', 10.50, false, NULL),
                                                                                          ('Pizza Prosciutto e Funghi', 'Pizza', 9.50, false, NULL),
                                                                                          ('Pizza Marinara', 'Pizza', 7.00, false, NULL),
                                                                                          ('Pizza Bufalina', 'Pizza', 11.00, false, NULL),
                                                                                          ('Pizza Vegetariana', 'Pizza', 9.00, false, NULL);

-- PRIMI PIATTI (6 prodotti)
INSERT INTO prodotti (nome_prodotto, categoria_prodotto, prezzo, deleted, deleted_at) VALUES
                                                                                          ('Spaghetti Carbonara', 'Primi', 12.00, false, NULL),
                                                                                          ('Penne Arrabbiata', 'Primi', 10.00, false, NULL),
                                                                                          ('Lasagne al Forno', 'Primi', 13.00, false, NULL),
                                                                                          ('Risotto ai Funghi', 'Primi', 14.00, false, NULL),
                                                                                          ('Gnocchi al Pesto', 'Primi', 11.50, false, NULL),
                                                                                          ('Tagliatelle al Ragù', 'Primi', 12.50, false, NULL);

-- SECONDI (4 prodotti)
INSERT INTO prodotti (nome_prodotto, categoria_prodotto, prezzo, deleted, deleted_at) VALUES
                                                                                          ('Bistecca alla Fiorentina', 'Secondi', 25.00, false, NULL),
                                                                                          ('Pollo alla Griglia', 'Secondi', 15.00, false, NULL),
                                                                                          ('Filetto di Salmone', 'Secondi', 18.00, false, NULL),
                                                                                          ('Tagliata di Manzo', 'Secondi', 22.00, false, NULL);

-- BEVANDE (8 prodotti)
INSERT INTO prodotti (nome_prodotto, categoria_prodotto, prezzo, deleted, deleted_at) VALUES
                                                                                          ('Coca Cola', 'Bevande', 3.00, false, NULL),
                                                                                          ('Acqua Naturale 1L', 'Bevande', 2.00, false, NULL),
                                                                                          ('Acqua Frizzante 1L', 'Bevande', 2.00, false, NULL),
                                                                                          ('Birra Moretti 66cl', 'Bevande', 5.00, false, NULL),
                                                                                          ('Vino Rosso Chianti 75cl', 'Bevande', 18.00, false, NULL),
                                                                                          ('Vino Bianco Vermentino 75cl', 'Bevande', 16.00, false, NULL),
                                                                                          ('Caffè Espresso', 'Bevande', 1.50, false, NULL),
                                                                                          ('Succo di Frutta', 'Bevande', 3.50, false, NULL);

-- DOLCI (4 prodotti)
INSERT INTO prodotti (nome_prodotto, categoria_prodotto, prezzo, deleted, deleted_at) VALUES
                                                                                          ('Tiramisù', 'Dolci', 6.00, false, NULL),
                                                                                          ('Panna Cotta', 'Dolci', 5.50, false, NULL),
                                                                                          ('Gelato Artigianale', 'Dolci', 5.00, false, NULL),
                                                                                          ('Torta al Cioccolato', 'Dolci', 6.50, false, NULL);

-- PRODOTTI ELIMINATI (soft delete - 2 prodotti)
INSERT INTO prodotti (nome_prodotto, categoria_prodotto, prezzo, deleted, deleted_at) VALUES
                                                                                          ('Pizza Hawaiana', 'Pizza', 9.00, true, '2025-10-10 15:30:00'),
                                                                                          ('Coca Cola Zero', 'Bevande', 3.00, true, '2025-10-12 10:00:00');

-- =========================
-- Popola Tabella ORDINI (15 ordini)
-- =========================
INSERT INTO ordini (id_tavolo, data_ordine, stato_ordine) VALUES
-- Ordini di oggi
(3, CURRENT_DATE, 'IN_ATTESA'),
(5, CURRENT_DATE, 'IN_PREPARAZIONE'),
(8, CURRENT_DATE, 'SERVITO'),
(11, CURRENT_DATE, 'IN_ATTESA'),
(15, CURRENT_DATE, 'IN_PREPARAZIONE'),
(18, CURRENT_DATE, 'SERVITO'),

-- Ordini chiusi di oggi
(1, CURRENT_DATE, 'CHIUSO'),
(2, CURRENT_DATE, 'CHIUSO'),

-- Ordini di ieri
(3, CURRENT_DATE - 1, 'CHIUSO'),
(5, CURRENT_DATE - 1, 'CHIUSO'),
(7, CURRENT_DATE - 1, 'CHIUSO'),
(10, CURRENT_DATE - 1, 'CHIUSO'),

-- Ordini di 2 giorni fa
(4, CURRENT_DATE - 2, 'CHIUSO'),
(6, CURRENT_DATE - 2, 'CHIUSO'),
(9, CURRENT_DATE - 2, 'CHIUSO');

-- =========================
-- Popola Tabella ORDINI_PRODOTTI (60+ righe per gli ordini)
-- =========================

-- Ordine 1 (Tavolo 3 - IN ATTESA)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (1, 1, 2, 'NON_PAGATO'),  -- 2x Pizza Margherita
                                                                                          (1, 21, 2, 'NON_PAGATO'), -- 2x Coca Cola
                                                                                          (1, 27, 1, 'NON_PAGATO'); -- 1x Caffè

-- Ordine 2 (Tavolo 5 - IN PREPARAZIONE)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (2, 3, 1, 'NON_PAGATO'),  -- 1x Pizza Capricciosa
                                                                                          (2, 9, 1, 'NON_PAGATO'),  -- 1x Spaghetti Carbonara
                                                                                          (2, 25, 1, 'NON_PAGATO'), -- 1x Vino Rosso
                                                                                          (2, 29, 2, 'NON_PAGATO'); -- 2x Tiramisù

-- Ordine 3 (Tavolo 8 - SERVITO)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (3, 4, 2, 'PAGATO'),      -- 2x Pizza 4 Formaggi
                                                                                          (3, 24, 1, 'PAGATO'),     -- 1x Birra
                                                                                          (3, 22, 2, 'NON_PAGATO'); -- 2x Acqua Naturale

-- Ordine 4 (Tavolo 11 - IN ATTESA)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (4, 17, 1, 'NON_PAGATO'), -- 1x Bistecca Fiorentina
                                                                                          (4, 11, 1, 'NON_PAGATO'), -- 1x Lasagne
                                                                                          (4, 26, 1, 'NON_PAGATO'), -- 1x Vino Bianco
                                                                                          (4, 22, 1, 'NON_PAGATO'); -- 1x Acqua

-- Ordine 5 (Tavolo 15 - IN PREPARAZIONE)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (5, 2, 3, 'NON_PAGATO'),  -- 3x Pizza Diavola
                                                                                          (5, 21, 3, 'NON_PAGATO'), -- 3x Coca Cola
                                                                                          (5, 31, 3, 'NON_PAGATO'); -- 3x Gelato

-- Ordine 6 (Tavolo 18 - SERVITO)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (6, 10, 2, 'PAGATO'),     -- 2x Penne Arrabbiata
                                                                                          (6, 18, 1, 'PAGATO'),     -- 1x Pollo Griglia
                                                                                          (6, 23, 2, 'PAGATO');     -- 2x Acqua Frizzante

-- Ordine 7 (Tavolo 1 - CHIUSO oggi)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (7, 1, 2, 'PAGATO'),      -- 2x Margherita
                                                                                          (7, 21, 2, 'PAGATO'),     -- 2x Coca Cola
                                                                                          (7, 27, 2, 'PAGATO');     -- 2x Caffè

-- Ordine 8 (Tavolo 2 - CHIUSO oggi)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (8, 9, 1, 'PAGATO'),      -- 1x Carbonara
                                                                                          (8, 12, 1, 'PAGATO'),     -- 1x Risotto
                                                                                          (8, 25, 1, 'PAGATO'),     -- 1x Vino Rosso
                                                                                          (8, 29, 1, 'PAGATO');     -- 1x Tiramisù

-- Ordine 9 (Tavolo 3 - CHIUSO ieri)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (9, 7, 2, 'PAGATO'),      -- 2x Pizza Bufalina
                                                                                          (9, 24, 2, 'PAGATO'),     -- 2x Birra
                                                                                          (9, 30, 2, 'PAGATO');     -- 2x Panna Cotta

-- Ordine 10 (Tavolo 5 - CHIUSO ieri)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (10, 17, 1, 'PAGATO'),    -- 1x Bistecca
                                                                                          (10, 20, 1, 'PAGATO'),    -- 1x Tagliata
                                                                                          (10, 25, 1, 'PAGATO'),    -- 1x Vino Rosso
                                                                                          (10, 22, 2, 'PAGATO');    -- 2x Acqua

-- Ordine 11 (Tavolo 7 - CHIUSO ieri)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (11, 5, 4, 'PAGATO'),     -- 4x Pizza Prosciutto Funghi
                                                                                          (11, 21, 4, 'PAGATO'),    -- 4x Coca Cola
                                                                                          (11, 31, 2, 'PAGATO');    -- 2x Gelato

-- Ordine 12 (Tavolo 10 - CHIUSO ieri)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (12, 14, 1, 'PAGATO'),    -- 1x Tagliatelle
                                                                                          (12, 19, 1, 'PAGATO'),    -- 1x Salmone
                                                                                          (12, 26, 1, 'PAGATO'),    -- 1x Vino Bianco
                                                                                          (12, 32, 1, 'PAGATO');    -- 1x Torta Cioccolato

-- Ordine 13 (Tavolo 4 - CHIUSO 2 giorni fa)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (13, 3, 3, 'PAGATO'),     -- 3x Capricciosa
                                                                                          (13, 24, 3, 'PAGATO'),    -- 3x Birra
                                                                                          (13, 29, 3, 'PAGATO');    -- 3x Tiramisù

-- Ordine 14 (Tavolo 6 - CHIUSO 2 giorni fa)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (14, 11, 2, 'PAGATO'),    -- 2x Lasagne
                                                                                          (14, 18, 1, 'PAGATO'),    -- 1x Pollo
                                                                                          (14, 22, 2, 'PAGATO'),    -- 2x Acqua
                                                                                          (14, 27, 2, 'PAGATO');    -- 2x Caffè

-- Ordine 15 (Tavolo 9 - CHIUSO 2 giorni fa)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (15, 1, 5, 'PAGATO'),     -- 5x Margherita
                                                                                          (15, 21, 5, 'PAGATO'),    -- 5x Coca Cola
                                                                                          (15, 28, 3, 'PAGATO'),    -- 3x Succo Frutta
                                                                                          (15, 31, 5, 'PAGATO');    -- 5x Gelato

-- =========================
-- RIEPILOGO DATI INSERITI
-- =========================
-- Tavoli: 20 tavoli (mix di stati: LIBERO, OCCUPATO, RISERVATO)
-- Prodotti: 32 prodotti (30 attivi + 2 eliminati con soft delete)
--   - 8 Pizze
--   - 6 Primi
--   - 4 Secondi
--   - 8 Bevande
--   - 4 Dolci
--   - 2 Eliminati (Pizza Hawaiana, Coca Cola Zero)
-- Ordini: 15 ordini (6 aperti oggi, 2 chiusi oggi, 7 chiusi giorni precedenti)
-- Ordini_Prodotti: 64 righe (prodotti ordinati con diverse quantità e stati pagamento)
-- =========================
