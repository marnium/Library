CREATE DATABASE IF NOT EXISTS biblioteca CHARSET utf8;

use biblioteca;

CREATE TABLE IF NOT EXISTS alumnos (
    num_control INT NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    apellido_pat VARCHAR(50) NOT NULL,
    apellido_mat VARCHAR(50) NOT NULL,
    telefono DECIMAL(10,0)  NOT NULL,
    direccion VARCHAR(60) NOT NULL,
    correo VARCHAR(50) NOT NULL,
    CONSTRAINT alumnos_pk PRIMARY KEY(num_control)
) ENGINE InnoDB;

CREATE TABLE IF NOT EXISTS personal (
    id_personal INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    apellido_pat VARCHAR(50) NOT NULL,
    apellido_mat VARCHAR(50) NOT NULL,
    telefono DECIMAL(10,0)  NOT NULL,
    direccion VARCHAR(60) NOT NULL,
    correo VARCHAR(50) NOT NULL,
    CONSTRAINT personal_pk PRIMARY KEY(id_personal)
) ENGINE InnoDB;

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    usuario VARCHAR(20) NOT NULL,
    contrasena BINARY(20) NOT NULL,
    tipo_usuario ENUM('Alumno', 'Personal', 'Admin') NOT NULL,
    fk_alumno INT NULL DEFAULT NULL,
    fk_personal INT NULL DEFAULT NULL,
    activo TINYINT(1) NOT NULL,
    CONSTRAINT usuarios_pk PRIMARY KEY(id_usuario),
    CONSTRAINT usuarios_unique UNIQUE(usuario),
    CONSTRAINT usuarios_fk_alumnos FOREIGN KEY(fk_alumno)
    REFERENCES alumnos(num_control),
    CONSTRAINT usuarios_fk_personal FOREIGN KEY(fk_personal)
    REFERENCES personal(id_personal)
) ENGINE InnoDB;

CREATE TABLE IF NOT EXISTS sesiones (
    id_sesion INT NOT NULL AUTO_INCREMENT,
    fk_usuario INT NOT NULL,
    fecha_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_final TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT sesiones_pk PRIMARY KEY(id_sesion),
    CONSTRAINT sesiones_fk_usuarios FOREIGN KEY(fk_usuario)
    REFERENCES usuarios(id_usuario)
) ENGINE InnoDB;

CREATE TABLE IF NOT EXISTS libros (
    id_libro INT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(80) NOT NULL,
    autor VARCHAR(50) NOT NULL,
    anio_publ INT DEFAULT 0 NOT NULL,
    editorial VARCHAR(60) NOT NULL,
    pais VARCHAR(15) NOT NULL,
    ciudad VARCHAR(30) NOT NULL,
    codigo DECIMAL(13,0) NOT NULL,
    ejemplares INT NOT NULL,
    activo TINYINT(1) NOT NULL,
    CONSTRAINT libros_pk PRIMARY KEY(id_libro)
) ENGINE InnoDB;

CREATE TABLE IF NOT EXISTS prestamos (
    id_prestamo INT NOT NULL AUTO_INCREMENT,
    fk_libro INT NOT NULL,
    fk_usuario INT NOT NULL,
    fecha_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fecha_final TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    observaciones VARCHAR(200),
    devuelto TINYINT(1) NOT NULL,
    CONSTRAINT prestamos_pk PRIMARY KEY(id_prestamo),
    CONSTRAINT prestamos_fk_libros FOREIGN KEY(fk_libro)
    REFERENCES libros(id_libro),
    CONSTRAINT prestamos_fk_usuarios FOREIGN KEY(fk_usuario)
    REFERENCES usuarios(id_usuario)
) ENGINE InnoDB;

CREATE TABLE IF NOT EXISTS sanciones (
    id_sancion INT NOT NULL AUTO_INCREMENT,
    fk_prestamo INT NOT NULL,
    inicio_sancion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    fin_sancion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    cumplido TINYINT(1) NOT NULL,
    CONSTRAINT sanciones_pk PRIMARY KEY(id_sancion),
    CONSTRAINT sanciones_fk_prestamos FOREIGN KEY(fk_prestamo)
    REFERENCES prestamos(id_prestamo)
) ENGINE InnoDB;

INSERT INTO personal VALUES(0,'Admin','Last','Name',958,'Address','admin@localhost');
INSERT INTO usuarios VALUES(0,'admin',UNHEX(SHA1('admin')),3,null,1,1);
