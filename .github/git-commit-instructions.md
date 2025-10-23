# Istruzioni per messaggi di commit dettagliati e leggibili

Obiettivo: generare messaggi di commit chiari e strutturati come “Oggetto + sezioni con ### + liste puntate”, con dettagli su endpoint, DTO, annotazioni, migrazioni DB, file di configurazione e motivazioni tecniche.

## 1) Struttura del messaggio

- Oggetto (prima riga): riassunto conciso in italiano, tempo presente/imperativo, massimo ~70 caratteri.
- Riga vuota.
- Corpo: sezioni con titoli Markdown `###` e liste puntate `-` molto specifiche.

Esempio di oggetto:  
Implementa CRUD prodotti e documentazione Swagger completa

## 2) Sezioni consigliate per il corpo

Usa una o più tra queste sezioni, a seconda delle modifiche effettuate:

- ### Funzionalità Prodotti
- ### Gestione Tavoli
- ### DTO e Mapping
- ### Entità e Relazioni JPA
- ### Service e Transazioni
- ### Repository e Query
- ### Endpoint REST
- ### Documentazione e Configurazione
- ### Sicurezza e Validazione
- ### Migrazioni Database
- ### Refactoring
- ### Correzione Bug

## 3) Dettagli minimi richiesti sotto ogni sezione

Ogni sezione deve includere punti chiari e specifici, preferendo più dettagli a meno dettagli:

- Endpoint creati/modificati: metodo e path (es. `POST /prodotti`, `PATCH /gestione-sala/libera-tutti-i-tavoli`).
- Classi/DTO/Entità toccate: nomi file e principali campi aggiunti/rimossi.
- Annotazioni usate: es. `@SQLDelete`, `@PreRemove`, `@Transactional`, `@Operation`, `@Tag`, `@Valid`, constraint Bean Validation.
- Migrazioni DB: id versione (es. `V3__...`), colonne aggiunte/modificate (`deleted`, `deleted_at`), indici/constraint.
- Configurazioni: file aggiornati (es. `pom.xml`, `application.properties`), dipendenze aggiunte/rimosse.
- Motivazioni: perché la modifica è necessaria (compliance REST, performance, coerenza naming, bugfix).
- Stato HTTP e header significativi: es. `Location` su `POST`, codici 200/201/204/400/404/409/422.

## 4) Stile e lingua

- Scrivi in italiano, mantieni nomi di classi/metodi/annotazioni in inglese come nel codice.
- Usa frasi brevi e puntuali, una modifica per bullet.
- Evita testo generico (“varie modifiche”), privilegia i nomi esatti.

## 5) Template pronto all’uso

Sostituisci o rimuovi le sezioni non pertinenti:

Oggetto: <riassunto conciso al presente>

<riga vuota>

### Funzionalità Prodotti
- Nuovi endpoint: <metodo> <path>, <metodo> <path>.
- Implementato soft-delete su `<Entity>` con `@SQLDelete` e `@PreRemove`.
- Aggiornato `<Repository/Controller>` per supportare <operazione>.
- Aggiornato `pom.xml` con `<dipendenze>`.

### Gestione Tavoli
- Estesi endpoint in `<Controller>` per <operazione>.
- Cambiato mapping da `<vecchio>` a `<nuovo>` per coerenza.
- Aggiunto endpoint `<metodo> <path>`.

### DTO e Mapping
- Creato `<NomeDTO>.java` con campi: <elenco>.
- Aggiunte validazioni: `@NotNull`, `@Size`, `@Min/@Max` su <campi>.
- Creato/aggiornato mapper MapStruct: `<MapperName>` con mapping <Entity> ⇄ <DTO>.

### Entità e Relazioni JPA
- Aggiunti campi `<campo: tipo>` su `<Entity>`.
- Definite relazioni: `@OneToMany`, `@ManyToOne` tra `<A>` e `<B>`.
- Aggiornati `equals`/`hashCode` se necessario.

### Service e Transazioni
- Aggiunto metodo `<nomeMetodo>()` in `<Service>`.
- Gestione transazionale con `@Transactional` e controlli di stato.
- Migliorata gestione eccezioni: `<EccezioneCustom>`.

### Repository e Query
- Aggiunta query custom `<metodo>` (es. `findUnpaidOrders()`).
- Ottimizzata query per performance/indice.

### Endpoint REST
- Migliorata risposta `<metodo> <path>` con status appropriati.
- Aggiunto header `Location` su `POST` per compliance REST.

### Migrazioni Database
- Migration `<VX__descrizione>`: colonne `deleted`, `deleted_at`, indici/constraint.
- Script di rollback se applicabile.

### Documentazione e Configurazione
- Documentazione Swagger con `@Operation` e `@Tag` su <controller>.
- Config Swagger personalizzata con path `<path/swagger>`.
- Abilitato debug SQL in `application.properties`.

## 6) Esempi pronti (stile desiderato)

Esempio A — Aggiunta DTO e mapping

Oggetto: Aggiungi OrderDTO e mapping MapStruct

### DTO e Mapping
- Creato `OrderDTO.java` con campi `id`, `customerName`, `totalAmount`.
- Validazioni: `@NotNull` e `@Min(0)` su `totalAmount`.
- Creato `OrderMapper` per conversione `Order` ⇄ `OrderDTO`.

### Service e Transazioni
- Aggiunto `createOrder()` in `OrderService` con `@Transactional`.
- Gestione stato iniziale e validazione input.

### Documentazione e Configurazione
- Aggiunta `@Operation` e `@Tag` su `OrderController`.
- Aggiornato `pom.xml` con `spring-boot-starter-validation`.

Esempio B — Estensione service e endpoint sala

Oggetto: Estendi gestione sala e pagamenti

### Endpoint REST
- Aggiunto `PATCH /gestione-sala/libera-tutti-i-tavoli`.
- Uniformato mapping controller da `/cassa` a `/gestione-sala`.

### Service e Transazioni
- Aggiunto `processPayment()` in `OrderService` con controlli di stato.
- Gestione concorrenza con transazioni e lock ottimistico se necessario.

### Migrazioni Database
- `V4__add_payment_columns.sql`: aggiunte colonne `paid`, `paid_at`.

### Documentazione e Configurazione
- Aggiornata doc Swagger per i nuovi endpoint.
- Abilitato SQL debug per tracciare query critiche.

## 7) Preferenze di dettaglio

- Non limitare la lunghezza del corpo: meglio troppo dettagliato che troppo sintetico.
- Includi sempre nomi file/classi/metodi/annotazioni quando rilevanti.
- Mantieni l’ordine: prima API/contract, poi domain (DTO/Entity), poi service/repository, poi DB/config/doc.
