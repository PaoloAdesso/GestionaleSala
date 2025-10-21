-- =============================================================================
-- CREAZIONE SCHEMA INIZIALE DATABASE GESTIONALE ORDINI
-- =============================================================================

-- =============================================================================
-- SEQUENZE POSTGRESQL
-- =============================================================================
CREATE SEQUENCE tavoli_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE prodotti_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ordini_id_seq START WITH 1 INCREMENT BY 1;

-- =============================================================================
-- TABELLA TAVOLI
-- =============================================================================
CREATE TABLE tavoli (
                        id_tavolo BIGINT PRIMARY KEY DEFAULT nextval('tavoli_id_seq'),
                        numero_nome_tavolo VARCHAR(255) NOT NULL UNIQUE,
                        stato VARCHAR(255) NOT NULL DEFAULT 'LIBERO',
                        deleted BOOLEAN NOT NULL DEFAULT FALSE,
                        deleted_at TIMESTAMP
);

-- =============================================================================
-- TABELLA PRODOTTI
-- =============================================================================
CREATE TABLE prodotti (
                          id_prodotto BIGINT PRIMARY KEY DEFAULT nextval('prodotti_id_seq'),
                          nome_prodotto VARCHAR(255) NOT NULL,
                          categoria_prodotto VARCHAR(255) NOT NULL,
                          prezzo NUMERIC(10,2) NOT NULL,
                          deleted BOOLEAN NOT NULL DEFAULT FALSE,
                          deleted_at TIMESTAMP
);

-- =============================================================================
-- TABELLA ORDINI
-- =============================================================================
CREATE TABLE ordini (
                        id_ordine BIGINT PRIMARY KEY DEFAULT nextval('ordini_id_seq'),
                        id_tavolo BIGINT NOT NULL,
                        data_ordine DATE NOT NULL,
                        stato_ordine VARCHAR(255) NOT NULL DEFAULT 'IN_ATTESA',
                        deleted BOOLEAN NOT NULL DEFAULT FALSE,
                        deleted_at TIMESTAMP,

    -- Foreign Key
                        CONSTRAINT fk_tavoli FOREIGN KEY (id_tavolo) REFERENCES tavoli(id_tavolo) ON DELETE CASCADE
);

-- =============================================================================
-- TABELLA PONTE ORDINI-PRODOTTI (Many-to-Many)
-- =============================================================================
CREATE TABLE ordini_prodotti (
                                 id_ordine BIGINT NOT NULL,
                                 id_prodotto BIGINT NOT NULL,
                                 quantita_prodotto BIGINT NOT NULL,
                                 stato_pagato VARCHAR(255) NOT NULL DEFAULT 'NON_PAGATO',

    -- Chiave primaria composta
                                 PRIMARY KEY (id_ordine, id_prodotto),

    -- Foreign Keys
                                 CONSTRAINT fk_ordini FOREIGN KEY (id_ordine) REFERENCES ordini(id_ordine) ON DELETE CASCADE,
                                 CONSTRAINT fk_prodotti FOREIGN KEY (id_prodotto) REFERENCES prodotti(id_prodotto) ON DELETE CASCADE
);

-- =============================================================================
-- INDICI PER PERFORMANCE
-- =============================================================================

-- Indici soft delete per tutte le tabelle
CREATE INDEX idx_tavoli_deleted ON tavoli(deleted);
CREATE INDEX idx_prodotti_deleted ON prodotti(deleted);
CREATE INDEX idx_ordini_deleted ON ordini(deleted);

-- Indici composti per query frequenti
CREATE INDEX idx_tavoli_active_stato ON tavoli(deleted, stato) WHERE deleted = false;
CREATE INDEX idx_prodotti_active_categoria ON prodotti(deleted, categoria_prodotto) WHERE deleted = false;
CREATE INDEX idx_ordini_active_data ON ordini(deleted, data_ordine) WHERE deleted = false;

-- Indici per audit trail (soft delete)
CREATE INDEX idx_tavoli_audit ON tavoli(deleted_at) WHERE deleted = true;
CREATE INDEX idx_prodotti_audit ON prodotti(deleted_at) WHERE deleted = true;
CREATE INDEX idx_ordini_audit ON ordini(deleted_at) WHERE deleted = true;

-- =============================================================================
-- SCHEMA COMPLETO CREATO
-- =============================================================================
/*
TABELLE CREATE:
tavoli - Anagrafica tavoli con stati occupazione + soft delete
prodotti - Catalogo menu con prezzi + soft delete
ordini - Gestione ordini con workflow + soft delete
ordini_prodotti - Relazione Many-to-Many ordini/prodotti

SEQUENZE CREATE:
tavoli_id_seq, prodotti_id_seq, ordini_id_seq

INDICI CREATI:
6 indici per soft delete (performance query attive)
3 indici composti per query frequenti
3 indici audit trail per dati eliminati

FOREIGN KEYS:
ordini.id_tavolo → tavoli.id_tavolo
ordini_prodotti.id_ordine → ordini.id_ordine
ordini_prodotti.id_prodotto → prodotti.id_prodotto
*/
