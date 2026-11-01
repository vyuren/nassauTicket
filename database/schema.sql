CREATE DATABASE IF NOT EXISTS nassauTicket
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE nassauTicket;

CREATE TABLE clientes(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    nascimento DATE NOT NULL,
    cpf NUMERIC(14,0) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL UNIQUE,
);

CREATE TABLE atendentes(
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    nascimento DATE NOT NULL,
    cpf NUMERIC(14,0) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL UNIQUE,
);

CREATE TABLE senhasPrioritarias(
    id INTEGER NOT NULL UNIQUE PRIMARY KEY,
    dataEmissao DATE NOT NULL,
    codigoSenha NUMERIC(12,0) NOT NULL UNIQUE,
    prioridade NUMERIC(3, 0) NOT NULL,
    descricao VARCHAR(50) NOT NULL,
    id_cliente INTEGER,
    id_atendente INTEGER,

    FOREIGN KEY(id_cliente) clientes(id),
    FOREIGN KEY(id_atendente) atendente(id)

);

CREATE TABLE senhasGerais(
    id INTEGER NOT NULL UNIQUE PRIMARY KEY,
    dataEmissao DATE NOT NULL,
    codigoSenha NUMERIC(12,0) NOT NULL UNIQUE,
    prioridade NUMERIC(3, 0) NOT NULL,
    descricao VARCHAR(50) NOT NULL,
    id_cliente INTEGER,
    id_atendente INTEGER,

    FOREIGN KEY(id_cliente) clientes(id),
    FOREIGN KEY(id_atendente) atendente(id)
)

CREATE TABLE senhasExames(
    id INTEGER NOT NULL UNIQUE PRIMARY KEY,
    dataEmissao DATE NOT NULL,
    codigoSenha NUMERIC(12,0) NOT NULL UNIQUE,
    prioridade NUMERIC(3, 0) NOT NULL,
    descricao VARCHAR(50) NOT NULL,
    id_cliente INTEGER,
    id_atendente INTEGER,

    FOREIGN KEY(id_cliente) clientes(id),
    FOREIGN KEY(id_atendente) atendente(id)
)