-- =========================
-- Aggiunta Soft Delete alla tabella Prodotti
-- =========================

-- Aggiungo la colonna 'deleted' (obbligatoria)
ALTER TABLE prodotti
    ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Aggiungo la colonna 'deleted_at' (opzionale)
ALTER TABLE prodotti
    ADD COLUMN deleted_at TIMESTAMP;

-- Creo indice per migliorare performance query con filtro deleted
CREATE INDEX idx_prodotti_deleted ON prodotti(deleted);

-- Commenti esplicativi per documentare le colonne
COMMENT ON COLUMN prodotti.deleted IS 'Indica se il prodotto è stato eliminato (soft delete)';
COMMENT ON COLUMN prodotti.deleted_at IS 'Data e ora di eliminazione del prodotto';