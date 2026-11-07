CREATE DATABASE IF NOT EXISTS nassauTicket
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE nassauTicket;

CREATE TABLE clientes(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    nascimento DATE NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(100)
);

CREATE TABLE atendentes(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    nascimento DATE NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(100)
);

CREATE TABLE senhas(
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    data_emissao DATE NOT NULL,
    codigo_senha VARCHAR(12) NOT NULL UNIQUE,
    tipo VARCHAR(12) NOT NULL,
    prioridade NUMERIC(3, 0),
    descricao VARCHAR(50),
    id_cliente INTEGER,
    id_atendente INTEGER,

    FOREIGN KEY(id_cliente) REFERENCES clientes(id),
    FOREIGN KEY(id_atendente) REFERENCES atendentes(id)
);

CREATE TABLE sequencia_senhas(
    data_emissao DATE NOT NULL PRIMARY KEY,
    ultima_sequencia INTEGER NOT NULL
);