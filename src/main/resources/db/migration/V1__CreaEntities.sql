-- =========================
-- Sequence
-- =========================
CREATE SEQUENCE tavoli_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE prodotti_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ordini_id_seq START WITH 1 INCREMENT BY 1;

-- =========================
-- Tabella Tavoli
-- =========================
CREATE TABLE tavoli (
                         id_tavolo BIGINT PRIMARY KEY DEFAULT nextval('tavoli_id_seq'),
                         numero_nome_tavolo VARCHAR(255) NOT NULL UNIQUE,
                         stato VARCHAR(255) NOT NULL DEFAULT 'LIBERO'
);
-- =========================
-- Tabella Prodotti
-- =========================
CREATE TABLE prodotti (
                              id_prodotto BIGINT PRIMARY KEY DEFAULT nextval('prodotti_id_seq'),
                              nome_prodotto VARCHAR(255) NOT NULL,
                              categoria_prodotto VARCHAR(255) NOT NULL,
                              prezzo NUMERIC(10,2) NOT NULL
);

-- =========================
-- Tabella Ordini
-- =========================
CREATE TABLE ordini (
                          id_ordine BIGINT PRIMARY KEY DEFAULT nextval('ordini_id_seq'),
                          id_tavolo BIGINT NOT NULL,
                          data_ordine DATE NOT NULL,
                          stato_ordine VARCHAR(255) NOT NULL default 'IN_ATTESA',
                          CONSTRAINT fk_tavoli FOREIGN KEY (id_tavolo) REFERENCES tavoli(id_tavolo) ON DELETE CASCADE
);
-- =========================
-- Tabella ponte Ordini - Prodotti (Many-to-Many)
-- =========================
CREATE TABLE ordini_prodotti (
                                           id_ordine BIGINT NOT NULL,
                                           id_prodotto BIGINT NOT NULL,
                                           quantita_prodotto BIGINT NOT NULL,
                                           stato_pagato VARCHAR(255) NOT NULL DEFAULT 'NON_PAGATO',
                                           PRIMARY KEY (id_ordine, id_prodotto),
                                           CONSTRAINT fk_ordini FOREIGN KEY (id_ordine) REFERENCES ordini(id_ordine) ON DELETE CASCADE,
                                           CONSTRAINT fk_prodotti FOREIGN KEY (id_prodotto) REFERENCES prodotti(id_prodotto) ON DELETE CASCADE
);