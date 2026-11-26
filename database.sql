CREATE DATABASE pet_clinic_db;
USE pet_clinic_db;

CREATE TABLE pasien_hewan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama_hewan VARCHAR(50) NOT NULL,
    jenis_hewan VARCHAR(30) NOT NULL,
    keluhan VARCHAR(100) NOT NULL
);