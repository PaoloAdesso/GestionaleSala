-- =============================================================================
-- DOCUMENTAZIONE COMPLETA E MIGLIORAMENTI DATABASE GESTIONALE ORDINI
-- Commenti, ottimizzazioni e finalizzazioni
-- =============================================================================

-- =============================================================================
-- COMMENTI ALLE TABELLE
-- =============================================================================
COMMENT ON TABLE tavoli IS 'Anagrafica dei tavoli del ristorante con gestione stati occupazione e supporto soft delete';
COMMENT ON TABLE prodotti IS 'Catalogo prodotti del menu con prezzi e categorizzazione. Supporta soft delete per conservazione storico';
COMMENT ON TABLE ordini IS 'Gestisce gli ordini del ristorante. Ogni record rappresenta un''ordinazione per tavolo con workflow di stato. Supporta soft delete per audit trail';
COMMENT ON TABLE ordini_prodotti IS 'Tabella di collegamento Many-to-Many tra ordini e prodotti. Gestisce quantità e stato pagamento per singolo prodotto';

-- =============================================================================
-- COMMENTI ALLE SEQUENZE
-- =============================================================================
COMMENT ON SEQUENCE tavoli_id_seq IS 'Sequenza per generare ID univoci tabella tavoli';
COMMENT ON SEQUENCE prodotti_id_seq IS 'Sequenza per generare ID univoci tabella prodotti';
COMMENT ON SEQUENCE ordini_id_seq IS 'Sequenza per generare ID univoci tabella ordini';

-- =============================================================================
-- COMMENTI COLONNE TABELLA TAVOLI
-- =============================================================================
COMMENT ON COLUMN tavoli.id_tavolo IS 'Chiave primaria univoca del tavolo, generata automaticamente da sequenza';
COMMENT ON COLUMN tavoli.numero_nome_tavolo IS 'Identificativo del tavolo visibile ai camerieri (es: "Tavolo 1", "Sala A-3", "Terrazza 5")';
COMMENT ON COLUMN tavoli.stato IS 'Stato corrente del tavolo: LIBERO (disponibile), OCCUPATO (clienti seduti), RISERVATO (prenotato)';
COMMENT ON COLUMN tavoli.deleted IS 'Flag soft delete: FALSE=tavolo attivo in sala, TRUE=tavolo rimosso/chiuso ma conservato per storico ordini';
COMMENT ON COLUMN tavoli.deleted_at IS 'Timestamp di rimozione del tavolo. NULL=attivo, NOT NULL=rimosso. Permette riattivazione e audit trail';

-- =============================================================================
-- COMMENTI COLONNE TABELLA PRODOTTI
-- =============================================================================
COMMENT ON COLUMN prodotti.id_prodotto IS 'Chiave primaria univoca del prodotto, generata automaticamente da sequenza';
COMMENT ON COLUMN prodotti.nome_prodotto IS 'Nome commerciale del prodotto mostrato nel menu (es: "Spaghetti Carbonara", "Caffè Espresso")';
COMMENT ON COLUMN prodotti.categoria_prodotto IS 'Categoria merceologica per organizzazione menu (es: ANTIPASTI, PRIMI, SECONDI, BEVANDE, DOLCI)';
COMMENT ON COLUMN prodotti.prezzo IS 'Prezzo di vendita in Euro con 2 decimali (es: 12.50). Range tipico: 0.50-50.00';
COMMENT ON COLUMN prodotti.deleted IS 'Flag soft delete: FALSE=prodotto attivo nel menu, TRUE=rimosso dal menu ma conservato per storico';
COMMENT ON COLUMN prodotti.deleted_at IS 'Timestamp preciso di rimozione dal menu. NULL=attivo, NOT NULL=rimosso. Utilizzato per audit trail';

-- =============================================================================
-- COMMENTI COLONNE TABELLA ORDINI
-- =============================================================================
COMMENT ON COLUMN ordini.id_ordine IS 'Chiave primaria univoca dell''ordine, generata automaticamente da sequenza';
COMMENT ON COLUMN ordini.id_tavolo IS 'Riferimento al tavolo che ha effettuato l''ordine. Foreign Key verso tavoli.id_tavolo';
COMMENT ON COLUMN ordini.data_ordine IS 'Data di creazione dell''ordine (formato YYYY-MM-DD). Utilizzata per reportistica giornaliera';
COMMENT ON COLUMN ordini.stato_ordine IS 'Workflow dell''ordine: IN_ATTESA (default) → IN_PREPARAZIONE → SERVITO → CHIUSO. Gestisce il ciclo di vita';
COMMENT ON COLUMN ordini.deleted IS 'Flag soft delete: FALSE=ordine attivo, TRUE=ordine cancellato ma conservato per audit';
COMMENT ON COLUMN ordini.deleted_at IS 'Timestamp di cancellazione dell''ordine. NULL=attivo, NOT NULL=cancellato. Permette ripristino e audit trail';

-- =============================================================================
-- COMMENTI COLONNE TABELLA ORDINI_PRODOTTI
-- =============================================================================
COMMENT ON COLUMN ordini_prodotti.id_ordine IS 'Riferimento all''ordine. Foreign Key verso ordini.id_ordine. Parte della chiave primaria composta';
COMMENT ON COLUMN ordini_prodotti.id_prodotto IS 'Riferimento al prodotto ordinato. Foreign Key verso prodotti.id_prodotto. Parte della chiave primaria composta';
COMMENT ON COLUMN ordini_prodotti.quantita_prodotto IS 'Quantità ordinata del prodotto (numero pezzi/porzioni). Deve essere > 0';
COMMENT ON COLUMN ordini_prodotti.stato_pagato IS 'Stato pagamento del singolo prodotto: NON_PAGATO (default), PAGATO. Permette pagamenti parziali per ordine';

-- =============================================================================
-- COMMENTI AGLI INDICI
-- =============================================================================
COMMENT ON INDEX idx_tavoli_deleted IS 'Indice principale per filtrare tavoli attivi/rimossi nelle operazioni quotidiane';
COMMENT ON INDEX idx_prodotti_deleted IS 'Indice per ottimizzare query di filtro su prodotti attivi/cancellati (soft delete)';
COMMENT ON INDEX idx_ordini_deleted IS 'Indice per filtrare rapidamente ordini attivi/cancellati nelle query quotidiane';

COMMENT ON INDEX idx_tavoli_active_stato IS 'Indice parziale ottimizzato per query su tavoli attivi filtrati per stato occupazione';
COMMENT ON INDEX idx_prodotti_active_categoria IS 'Indice parziale per query prodotti attivi per categoria (gestione menu)';
COMMENT ON INDEX idx_ordini_active_data IS 'Indice parziale per query ordini attivi per data (reportistica giornaliera)';

COMMENT ON INDEX idx_tavoli_audit IS 'Indice parziale per audit trail dei tavoli rimossi ordinati per data rimozione';
COMMENT ON INDEX idx_prodotti_audit IS 'Indice parziale per audit trail dei prodotti rimossi ordinati per data rimozione';
COMMENT ON INDEX idx_ordini_audit IS 'Indice parziale per audit trail degli ordini cancellati ordinati per data cancellazione';

-- =============================================================================
-- INDICI AGGIUNTIVI PER PERFORMANCE
-- =============================================================================

-- Indice per ricerche per numero tavolo (query frequenti)
CREATE INDEX idx_tavoli_numero_nome ON tavoli(numero_nome_tavolo) WHERE deleted = false;

-- Indice per ricerche prodotti per nome (autocomplete menu)
CREATE INDEX idx_prodotti_nome_search ON prodotti USING gin(to_tsvector('italian', nome_prodotto)) WHERE deleted = false;

-- Indice per query ordini per stato e data (dashboard operativa)
CREATE INDEX idx_ordini_stato_data ON ordini(stato_ordine, data_ordine) WHERE deleted = false;

-- Commenti ai nuovi indici
COMMENT ON INDEX idx_tavoli_numero_nome IS 'Indice per ricerca rapida tavoli per numero/nome nelle operazioni quotidiane';
COMMENT ON INDEX idx_prodotti_nome_search IS 'Indice full-text per ricerca prodotti per nome (autocomplete interfaccia)';
COMMENT ON INDEX idx_ordini_stato_data IS 'Indice composto per dashboard operativa: ordini per stato e data';

-- =============================================================================
-- VINCOLI AGGIUNTIVI (Check Constraints)
-- =============================================================================

-- Vincoli di business per validazione dati
ALTER TABLE prodotti ADD CONSTRAINT chk_prezzo_positivo CHECK (prezzo > 0);
ALTER TABLE ordini_prodotti ADD CONSTRAINT chk_quantita_positiva CHECK (quantita_prodotto > 0);

-- Commenti ai vincoli
COMMENT ON CONSTRAINT chk_prezzo_positivo ON prodotti IS 'Vincolo: il prezzo deve essere maggiore di zero';
COMMENT ON CONSTRAINT chk_quantita_positiva ON ordini_prodotti IS 'Vincolo: la quantità ordinata deve essere maggiore di zero';

-- =============================================================================
-- DOCUMENTAZIONE ARCHITETTURALE
-- =============================================================================

/*
=== DESIGN PATTERNS IMPLEMENTATI ===

1. SOFT DELETE PATTERN:
   - Implementato su tutte le entità principali (tavoli, prodotti, ordini)
   - Colonne: deleted (BOOLEAN), deleted_at (TIMESTAMP)
   - Vantaggi: conserva storico, permette ripristino, audit trail completo
   - Indici ottimizzati per query su dati attivi/cancellati

2. ENUM PATTERN per Stati:
   - StatoTavolo: LIBERO, OCCUPATO, RISERVATO
   - StatoOrdine: IN_ATTESA, IN_PREPARAZIONE, SERVITO, CHIUSO
   - StatoPagato: NON_PAGATO, PAGATO
   - Vantaggi: tipo safety, validazione automatica, chiarezza business

3. MANY-TO-MANY con Attributi:
   - Tabella ordini_prodotti con quantita_prodotto e stato_pagato
   - Permette gestione granulare: pagamenti parziali, quantità variabili
   - Chiave primaria composta per performance

4. SEQUENZE PostgreSQL:
   - Generazione automatica ID con performance ottimali
   - Thread-safe per ambiente multi-utente
   - Evita gap negli ID in caso di rollback

=== VINCOLI FOREIGN KEY ===

1. ordini.id_tavolo → tavoli.id_tavolo (fk_tavoli)
   - ON DELETE CASCADE: eliminando tavolo vengono eliminati gli ordini
   - Garantisce integrità: non esistono ordini senza tavolo

2. ordini_prodotti.id_ordine → ordini.id_ordine (fk_ordini)
   - ON DELETE CASCADE: eliminando ordine vengono eliminati i dettagli
   - Mantiene coerenza: non esistono dettagli senza ordine

3. ordini_prodotti.id_prodotto → prodotti.id_prodotto (fk_prodotti)
   - ON DELETE CASCADE: eliminando prodotto vengono eliminate le associazioni
   - Attenzione: con soft delete questo non dovrebbe mai accadere

=== STRATEGIE DI PERFORMANCE ===

1. INDICI PARZIALI:
   - Solo su record attivi (WHERE deleted = false)
   - Riduce dimensioni indici, migliora performance
   - Separati indici per audit trail (deleted = true)

2. INDICI COMPOSTI:
   - Combinazioni frequenti: (deleted, stato), (deleted, data), ecc.
   - Ottimizzati per query dashboard e reportistica
   - Coprono query più comuni senza table scan

3. FULL-TEXT SEARCH:
   - Indice GIN su nome prodotti per ricerca veloce
   - Supporta autocomplete interfaccia utente
   - Configurazione italiana per stemming corretto

=== CASI D'USO SUPPORTATI ===

- Gestione completa workflow ordini
- Soft delete con possibilità ripristino
- Reportistica storica completa
- Dashboard operativa real-time
- Ricerca veloce prodotti/tavoli
- Pagamenti parziali per ordine
- Audit trail completo operazioni
- Gestione stati tavoli dinamica
- Categorizzazione prodotti flessibile
- Scalabilità per crescita dati

 */