--
-- PostgreSQL database dump
--

SET client_encoding = 'SQL_ASCII';
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = true;

--
-- Name: customers; Type: TABLE; Schema: public; Owner: pos; Tablespace: 
--

CREATE TABLE customers (
    customerid serial NOT NULL,
    balance numeric(10,2) DEFAULT 0 NOT NULL,
    firstname character varying(32) NOT NULL,
    lastname character varying(32) NOT NULL,
    comp smallint DEFAULT 0 NOT NULL,
    renewamount numeric(10,2) DEFAULT 0 NOT NULL
);


ALTER TABLE public.customers OWNER TO pos;

--
-- Name: customers_customerid_seq; Type: SEQUENCE SET; Schema: public; Owner: pos
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('customers', 'customerid'), 1, true);


--
-- Name: inventory; Type: TABLE; Schema: public; Owner: pos; Tablespace: 
--

CREATE TABLE inventory (
    sku character varying(32) NOT NULL,
    quantity integer DEFAULT 0 NOT NULL,
    name character varying(128) NOT NULL,
    price numeric(10,2) DEFAULT 0 NOT NULL,
    tax numeric(3,2) DEFAULT 0 NOT NULL,
    rentable smallint DEFAULT 0 NOT NULL,
    unlimited smallint DEFAULT 0 NOT NULL
);


ALTER TABLE public.inventory OWNER TO pos;

--
-- Name: notes; Type: TABLE; Schema: public; Owner: pos; Tablespace: 
--

CREATE TABLE notes (
    customerid integer NOT NULL,
    note character varying(255) NOT NULL
);


ALTER TABLE public.notes OWNER TO pos;

--
-- Name: transaction_items; Type: TABLE; Schema: public; Owner: pos; Tablespace: 
--

CREATE TABLE transaction_items (
    transaction_id integer NOT NULL,
    sku character varying(32) NOT NULL,
    quantity integer DEFAULT 0 NOT NULL,
    price numeric(10,2) DEFAULT 0 NOT NULL
);


ALTER TABLE public.transaction_items OWNER TO pos;

--
-- Name: transactions; Type: TABLE; Schema: public; Owner: pos; Tablespace: 
--

CREATE TABLE transactions (
    transaction_id serial NOT NULL,
    transaction_time timestamp without time zone NOT NULL,
    code character(1) NOT NULL,
    cashierid integer NOT NULL,
    customerid integer,
    subtotal numeric(10,2) NOT NULL,
    tax numeric(10,2) NOT NULL
);


ALTER TABLE public.transactions OWNER TO pos;

--
-- Name: transactions_transaction_id_seq; Type: SEQUENCE SET; Schema: public; Owner: pos
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('transactions', 'transaction_id'), 1, true);


--
-- Name: users; Type: TABLE; Schema: public; Owner: pos; Tablespace: 
--

CREATE TABLE users (
    userid serial NOT NULL,
    username character varying(32) NOT NULL,
    "password" character varying(41) NOT NULL,
    "level" smallint DEFAULT 0 NOT NULL,
    firstname character varying(32) NOT NULL,
    lastname character varying(32) NOT NULL
);


ALTER TABLE public.users OWNER TO pos;

--
-- Name: users_userid_seq; Type: SEQUENCE SET; Schema: public; Owner: pos
--

SELECT pg_catalog.setval(pg_catalog.pg_get_serial_sequence('users', 'userid'), 1, true);


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: pos
--

INSERT INTO users VALUES (1, 'admin', MD5('pos'), 3, 'pos', 'admin');


--
-- Name: customers_pkey; Type: CONSTRAINT; Schema: public; Owner: pos; Tablespace: 
--

ALTER TABLE ONLY customers
    ADD CONSTRAINT customers_pkey PRIMARY KEY (customerid);


ALTER INDEX public.customers_pkey OWNER TO pos;

--
-- Name: inventory_pkey; Type: CONSTRAINT; Schema: public; Owner: pos; Tablespace: 
--

ALTER TABLE ONLY inventory
    ADD CONSTRAINT inventory_pkey PRIMARY KEY (sku);


ALTER INDEX public.inventory_pkey OWNER TO pos;

--
-- Name: notes_pkey; Type: CONSTRAINT; Schema: public; Owner: pos; Tablespace: 
--

ALTER TABLE ONLY notes
    ADD CONSTRAINT notes_pkey PRIMARY KEY (customerid);


ALTER INDEX public.notes_pkey OWNER TO pos;

--
-- Name: transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: pos; Tablespace: 
--

ALTER TABLE ONLY transactions
    ADD CONSTRAINT transactions_pkey PRIMARY KEY (transaction_id);


ALTER INDEX public.transactions_pkey OWNER TO pos;

--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: pos; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (userid);


ALTER INDEX public.users_pkey OWNER TO pos;

--
-- Name: fki_notes_customerid_fkey; Type: INDEX; Schema: public; Owner: pos; Tablespace: 
--

CREATE INDEX fki_notes_customerid_fkey ON notes USING btree (customerid);


ALTER INDEX public.fki_notes_customerid_fkey OWNER TO pos;

--
-- Name: fki_transaction_items_transaction_id_fkey; Type: INDEX; Schema: public; Owner: pos; Tablespace: 
--

CREATE INDEX fki_transaction_items_transaction_id_fkey ON transaction_items USING btree (transaction_id);


ALTER INDEX public.fki_transaction_items_transaction_id_fkey OWNER TO pos;

--
-- Name: fki_transactions_cashierid_fkey; Type: INDEX; Schema: public; Owner: pos; Tablespace: 
--

CREATE INDEX fki_transactions_cashierid_fkey ON transactions USING btree (cashierid);


ALTER INDEX public.fki_transactions_cashierid_fkey OWNER TO pos;

--
-- Name: fki_transactions_customerid_fkey; Type: INDEX; Schema: public; Owner: pos; Tablespace: 
--

CREATE INDEX fki_transactions_customerid_fkey ON transactions USING btree (customerid);


ALTER INDEX public.fki_transactions_customerid_fkey OWNER TO pos;

--
-- Name: notes_customerid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: pos
--

ALTER TABLE ONLY notes
    ADD CONSTRAINT notes_customerid_fkey FOREIGN KEY (customerid) REFERENCES customers(customerid) ON DELETE CASCADE;


--
-- Name: transaction_items_transaction_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: pos
--

ALTER TABLE ONLY transaction_items
    ADD CONSTRAINT transaction_items_transaction_id_fkey FOREIGN KEY (transaction_id) REFERENCES transactions(transaction_id) ON DELETE CASCADE;


--
-- Name: transactions_cashierid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: pos
--

ALTER TABLE ONLY transactions
    ADD CONSTRAINT transactions_cashierid_fkey FOREIGN KEY (cashierid) REFERENCES users(userid) ON DELETE SET RESTRICT;


--
-- Name: transactions_customerid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: pos
--

ALTER TABLE ONLY transactions
    ADD CONSTRAINT transactions_customerid_fkey FOREIGN KEY (customerid) REFERENCES customers(customerid) ON DELETE RESTRICT;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--
