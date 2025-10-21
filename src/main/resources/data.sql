-- =============================================================================
-- DATA.SQL - Dati Sample Aggiornati per Gestionale Ordini
-- =============================================================================

-- =============================================================================
-- POPOLAMENTO TAVOLI (20 tavoli con mix realistico di stati)
-- =============================================================================

INSERT INTO tavoli (numero_nome_tavolo, stato, deleted, deleted_at) VALUES
-- Sala principale (12 tavoli attivi)
('Tavolo 1', 'LIBERO', false, NULL),
('Tavolo 2', 'LIBERO', false, NULL),
('Tavolo 3', 'OCCUPATO', false, NULL),
('Tavolo 4', 'LIBERO', false, NULL),
('Tavolo 5', 'OCCUPATO', false, NULL),
('Tavolo 6', 'RISERVATO', false, NULL),
('Tavolo 7', 'LIBERO', false, NULL),
('Tavolo 8', 'OCCUPATO', false, NULL),
('Tavolo 9', 'LIBERO', false, NULL),
('Tavolo 10', 'LIBERO', false, NULL),
('Tavolo 11', 'OCCUPATO', false, NULL),
('Tavolo 12', 'LIBERO', false, NULL),

-- Area VIP (2 tavoli attivi)
('Tavolo VIP 1', 'RISERVATO', false, NULL),
('Tavolo VIP 2', 'LIBERO', false, NULL),

-- Terrazza (2 tavoli attivi)
('Terrazza 1', 'OCCUPATO', false, NULL),
('Terrazza 2', 'LIBERO', false, NULL),

-- Aree speciali (4 tavoli attivi)
('Sala Privata', 'RISERVATO', false, NULL),
('Bancone 1', 'OCCUPATO', false, NULL),
('Bancone 2', 'LIBERO', false, NULL),
('Esterno 1', 'LIBERO', false, NULL),

-- TAVOLI RIMOSSI (esempi soft delete)
('Tavolo 13', 'LIBERO', true, '2025-10-15 14:30:00'),  -- Rimosso per ristrutturazione
('Sala Meeting', 'LIBERO', true, '2025-10-18 09:00:00'); -- Convertita in ufficio

-- =============================================================================
-- POPOLAMENTO PRODOTTI (32 prodotti + esempi soft delete)
-- =============================================================================

-- PIZZE (8 prodotti)
INSERT INTO prodotti (nome_prodotto, categoria_prodotto, prezzo, deleted, deleted_at) VALUES
                                                                                          ('Pizza Margherita', 'PIZZE', 8.00, false, NULL),
                                                                                          ('Pizza Diavola', 'PIZZE', 9.50, false, NULL),
                                                                                          ('Pizza Capricciosa', 'PIZZE', 10.00, false, NULL),
                                                                                          ('Pizza 4 Formaggi', 'PIZZE', 10.50, false, NULL),
                                                                                          ('Pizza Prosciutto e Funghi', 'PIZZE', 9.50, false, NULL),
                                                                                          ('Pizza Marinara', 'PIZZE', 7.00, false, NULL),
                                                                                          ('Pizza Bufalina', 'PIZZE', 11.00, false, NULL),
                                                                                          ('Pizza Vegetariana', 'PIZZE', 9.00, false, NULL);

-- PRIMI PIATTI (6 prodotti)
INSERT INTO prodotti (nome_prodotto, categoria_prodotto, prezzo, deleted, deleted_at) VALUES
                                                                                          ('Spaghetti Carbonara', 'PRIMI', 12.00, false, NULL),
                                                                                          ('Penne Arrabbiata', 'PRIMI', 10.00, false, NULL),
                                                                                          ('Lasagne al Forno', 'PRIMI', 13.00, false, NULL),
                                                                                          ('Risotto ai Funghi', 'PRIMI', 14.00, false, NULL),
                                                                                          ('Gnocchi al Pesto', 'PRIMI', 11.50, false, NULL),
                                                                                          ('Tagliatelle al Ragù', 'PRIMI', 12.50, false, NULL);

-- SECONDI PIATTI (4 prodotti)
INSERT INTO prodotti (nome_prodotto, categoria_prodotto, prezzo, deleted, deleted_at) VALUES
                                                                                          ('Bistecca alla Fiorentina', 'SECONDI', 25.00, false, NULL),
                                                                                          ('Pollo alla Griglia', 'SECONDI', 15.00, false, NULL),
                                                                                          ('Filetto di Salmone', 'SECONDI', 18.00, false, NULL),
                                                                                          ('Tagliata di Manzo', 'SECONDI', 22.00, false, NULL);

-- BEVANDE (8 prodotti)
INSERT INTO prodotti (nome_prodotto, categoria_prodotto, prezzo, deleted, deleted_at) VALUES
                                                                                          ('Coca Cola 33cl', 'BEVANDE', 3.00, false, NULL),
                                                                                          ('Acqua Naturale 1L', 'BEVANDE', 2.00, false, NULL),
                                                                                          ('Acqua Frizzante 1L', 'BEVANDE', 2.00, false, NULL),
                                                                                          ('Birra Moretti 66cl', 'BEVANDE', 5.00, false, NULL),
                                                                                          ('Vino Rosso Chianti 75cl', 'BEVANDE', 18.00, false, NULL),
                                                                                          ('Vino Bianco Vermentino 75cl', 'BEVANDE', 16.00, false, NULL),
                                                                                          ('Caffè Espresso', 'BEVANDE', 1.50, false, NULL),
                                                                                          ('Succo di Frutta', 'BEVANDE', 3.50, false, NULL);

-- DOLCI (4 prodotti)
INSERT INTO prodotti (nome_prodotto, categoria_prodotto, prezzo, deleted, deleted_at) VALUES
                                                                                          ('Tiramisù della Casa', 'DOLCI', 6.00, false, NULL),
                                                                                          ('Panna Cotta ai Frutti di Bosco', 'DOLCI', 5.50, false, NULL),
                                                                                          ('Gelato Artigianale (3 gusti)', 'DOLCI', 5.00, false, NULL),
                                                                                          ('Torta al Cioccolato Fondente', 'DOLCI', 6.50, false, NULL);

-- PRODOTTI RIMOSSI DAL MENU (esempi soft delete)
INSERT INTO prodotti (nome_prodotto, categoria_prodotto, prezzo, deleted, deleted_at) VALUES
                                                                                          ('Pizza Hawaiana', 'PIZZE', 9.00, true, '2025-10-10 15:30:00'),
                                                                                          ('Coca Cola Zero', 'BEVANDE', 3.00, true, '2025-10-12 10:00:00');

-- =============================================================================
-- POPOLAMENTO ORDINI (18 ordini con nuovi stati enum)
-- Stati: IN_ATTESA, IN_PREPARAZIONE, SERVITO, CHIUSO
-- =============================================================================

INSERT INTO ordini (id_tavolo, data_ordine, stato_ordine, deleted, deleted_at) VALUES
-- ORDINI ATTIVI DI OGGI (6 ordini)
(3, CURRENT_DATE, 'IN_ATTESA', false, NULL),        -- Appena arrivato
(5, CURRENT_DATE, 'IN_PREPARAZIONE', false, NULL),  -- In cucina
(8, CURRENT_DATE, 'SERVITO', false, NULL),          -- Servito, da pagare
(11, CURRENT_DATE, 'IN_ATTESA', false, NULL),       -- In attesa
(15, CURRENT_DATE, 'IN_PREPARAZIONE', false, NULL), -- In preparazione
(18, CURRENT_DATE, 'SERVITO', false, NULL),         -- Servito

-- ORDINI COMPLETATI DI OGGI (3 ordini)
(1, CURRENT_DATE, 'CHIUSO', false, NULL),           -- Già pagato e chiuso
(2, CURRENT_DATE, 'CHIUSO', false, NULL),           -- Già pagato e chiuso
(4, CURRENT_DATE, 'CHIUSO', false, NULL),           -- Già pagato e chiuso

-- ORDINI DI IERI (tutti chiusi - 5 ordini)
(6, CURRENT_DATE - 1, 'CHIUSO', false, NULL),
(7, CURRENT_DATE - 1, 'CHIUSO', false, NULL),
(9, CURRENT_DATE - 1, 'CHIUSO', false, NULL),
(10, CURRENT_DATE - 1, 'CHIUSO', false, NULL),
(12, CURRENT_DATE - 1, 'CHIUSO', false, NULL),

-- ORDINI DI 2 GIORNI FA (tutti chiusi - 3 ordini)
(13, CURRENT_DATE - 2, 'CHIUSO', false, NULL),
(14, CURRENT_DATE - 2, 'CHIUSO', false, NULL),
(16, CURRENT_DATE - 2, 'CHIUSO', false, NULL),

-- ORDINE CANCELLATO (esempio soft delete)
(17, CURRENT_DATE, 'IN_ATTESA', true, CURRENT_TIMESTAMP); -- Ordine cancellato

-- =============================================================================
-- POPOLAMENTO ORDINI_PRODOTTI (dettagli realistici degli ordini)
-- =============================================================================

-- ORDINE 1: Tavolo 3 - IN_ATTESA (famiglia con bambini)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (1, 1, 2, 'NON_PAGATO'), -- 2x Pizza Margherita
                                                                                          (1, 8, 1, 'NON_PAGATO'), -- 1x Pizza Vegetariana
                                                                                          (1, 21, 3, 'NON_PAGATO'), -- 3x Coca Cola
                                                                                          (1, 27, 2, 'NON_PAGATO'); -- 2x Caffè

-- ORDINE 2: Tavolo 5 - IN_PREPARAZIONE (cena romantica)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (2, 15, 2, 'NON_PAGATO'), -- 2x Lasagne al Forno
                                                                                          (2, 17, 1, 'NON_PAGATO'), -- 1x Bistecca Fiorentina
                                                                                          (2, 25, 1, 'NON_PAGATO'), -- 1x Vino Rosso Chianti
                                                                                          (2, 29, 2, 'NON_PAGATO'); -- 2x Tiramisù

-- ORDINE 3: Tavolo 8 - SERVITO (pranzo business)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (3, 9, 1, 'PAGATO'),     -- 1x Spaghetti Carbonara
                                                                                          (3, 18, 1, 'PAGATO'),    -- 1x Pollo alla Griglia
                                                                                          (3, 22, 2, 'NON_PAGATO'), -- 2x Acqua Naturale
                                                                                          (3, 27, 2, 'NON_PAGATO'); -- 2x Caffè

-- ORDINE 4: Tavolo 11 - IN_ATTESA (gruppo amici)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (4, 3, 3, 'NON_PAGATO'), -- 3x Pizza Capricciosa
                                                                                          (4, 2, 2, 'NON_PAGATO'), -- 2x Pizza Diavola
                                                                                          (4, 24, 4, 'NON_PAGATO'), -- 4x Birra Moretti
                                                                                          (4, 31, 3, 'NON_PAGATO'); -- 3x Gelato

-- ORDINE 5: Tavolo 15 - IN_PREPARAZIONE (famiglia numerosa)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (5, 1, 3, 'NON_PAGATO'), -- 3x Pizza Margherita
                                                                                          (5, 5, 2, 'NON_PAGATO'), -- 2x Pizza Prosciutto Funghi
                                                                                          (5, 21, 5, 'NON_PAGATO'), -- 5x Coca Cola
                                                                                          (5, 28, 2, 'NON_PAGATO'); -- 2x Succo di Frutta

-- ORDINE 6: Tavolo 18 - SERVITO (coppia)
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
                                                                                          (6, 12, 1, 'PAGATO'),    -- 1x Risotto ai Funghi
                                                                                          (6, 19, 1, 'PAGATO'),    -- 1x Filetto di Salmone
                                                                                          (6, 26, 1, 'PAGATO'),    -- 1x Vino Bianco
                                                                                          (6, 30, 2, 'PAGATO');    -- 2x Panna Cotta

-- ORDINI CHIUSI DI OGGI (7, 8, 9) - tutti pagati
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
-- Ordine 7: Pranzo veloce
(7, 1, 1, 'PAGATO'),     -- 1x Margherita
(7, 27, 1, 'PAGATO'),    -- 1x Caffè
(7, 22, 1, 'PAGATO'),    -- 1x Acqua

-- Ordine 8: Pranzo completo
(8, 10, 1, 'PAGATO'),    -- 1x Penne Arrabbiata
(8, 18, 1, 'PAGATO'),    -- 1x Pollo Griglia
(8, 24, 1, 'PAGATO'),    -- 1x Birra
(8, 32, 1, 'PAGATO'),    -- 1x Torta Cioccolato

-- Ordine 9: Aperitivo
(9, 24, 2, 'PAGATO'),    -- 2x Birra
(9, 4, 1, 'PAGATO'),     -- 1x Pizza 4 Formaggi
(9, 31, 2, 'PAGATO');    -- 2x Gelato

-- ORDINI STORICI (10-17) - esempi per reportistica
INSERT INTO ordini_prodotti (id_ordine, id_prodotto, quantita_prodotto, stato_pagato) VALUES
-- Ordini di ieri (tutti pagati)
(10, 17, 1, 'PAGATO'), (10, 25, 1, 'PAGATO'), (10, 29, 1, 'PAGATO'),
(11, 7, 2, 'PAGATO'), (11, 24, 2, 'PAGATO'),
(12, 13, 1, 'PAGATO'), (12, 14, 1, 'PAGATO'), (12, 26, 1, 'PAGATO'),
(13, 5, 4, 'PAGATO'), (13, 21, 4, 'PAGATO'), (13, 31, 2, 'PAGATO'),
(14, 11, 2, 'PAGATO'), (14, 27, 2, 'PAGATO'),

-- Ordini di 2 giorni fa (tutti pagati)
(15, 3, 2, 'PAGATO'), (15, 24, 2, 'PAGATO'), (15, 29, 2, 'PAGATO'),
(16, 9, 1, 'PAGATO'), (16, 20, 1, 'PAGATO'), (16, 25, 1, 'PAGATO'),
(17, 1, 3, 'PAGATO'), (17, 21, 3, 'PAGATO'); -- Questo ordine sarà poi "cancellato"

-- =============================================================================
-- RIEPILOGO DATI CREATI
-- =============================================================================
/*
DATI INSERITI:
Tavoli: 20 tavoli (mix realistico di stati)
 - 12 LIBERO, 6 OCCUPATO, 2 RISERVATO

Prodotti: 32 prodotti (30 attivi + 2 soft deleted)
 - 8 Pizze, 6 Primi, 4 Secondi, 8 Bevande, 4 Dolci
 - 2 prodotti rimossi dal menu (soft delete)

Ordini: 18 ordini (17 attivi + 1 soft deleted)
 - 6 ordini attivi oggi (vari stati workflow)
 - 3 ordini chiusi oggi (pagati e completati)
 - 8 ordini storici (ieri e 2 giorni fa)
 - 1 ordine cancellato (esempio soft delete)

Dettagli Ordini: ~70 righe ordini_prodotti
 - Mix realistico di quantità e stati pagamento
 - Esempi di pagamenti parziali
 - Ordini completi per testing
*/
