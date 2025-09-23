
-- Script para criação da estrutura do banco de dados
CREATE DATABASE IF NOT EXISTS banco_digital;
USE banco_digital;
CREATE TABLE IF NOT EXISTS contas (
    numero INT PRIMARY KEY,
    titular VARCHAR(100) NOT NULL,
    saldo DECIMAL(10, 2) NOT NULL
);