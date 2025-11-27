-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema llajtazo
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema llajtazo
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `llajtazo` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `llajtazo` ;

-- -----------------------------------------------------
-- Table `llajtazo`.`categorias`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`categorias` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `nombre` (`nombre` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`usuarios`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`usuarios` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL,
  `nombre_completo` VARCHAR(255) NOT NULL,
  `password_hash` VARCHAR(50) NOT NULL,
  `avatar_url` TEXT NULL DEFAULT NULL,
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email` (`email` ASC) VISIBLE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`organizadores`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`organizadores` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NOT NULL,
  `about` TEXT NULL DEFAULT NULL,
  `logo_url` TEXT NULL DEFAULT NULL,
  `followers` INT NULL DEFAULT '0',
  `suscribed` TINYINT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`lugares`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`lugares` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(255) NOT NULL,
  `direccion` VARCHAR(500) NULL DEFAULT NULL,
  `latitud` DECIMAL(9,6) NULL DEFAULT NULL,
  `longitud` DECIMAL(9,6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`events`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`events` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `organizador_id` INT NULL DEFAULT NULL,
  `lugar_id` INT NULL DEFAULT NULL,
  `categoria_id` INT NULL DEFAULT NULL,
  `titulo` VARCHAR(255) NOT NULL,
  `descripcion` TEXT NULL DEFAULT NULL,
  `start_time` DATETIME NOT NULL,
  `end_time` DATETIME NOT NULL,
  `cover_url` TEXT NULL DEFAULT NULL,
  `estado` VARCHAR(50) NULL DEFAULT 'PUBLISHED',
  PRIMARY KEY (`id`),
  INDEX `organizador_id` (`organizador_id` ASC) VISIBLE,
  INDEX `lugar_id` (`lugar_id` ASC) VISIBLE,
  INDEX `categoria_id` (`categoria_id` ASC) VISIBLE,
  CONSTRAINT `events_ibfk_1`
    FOREIGN KEY (`organizador_id`)
    REFERENCES `llajtazo`.`organizadores` (`id`)
    ON DELETE SET NULL,
  CONSTRAINT `events_ibfk_2`
    FOREIGN KEY (`lugar_id`)
    REFERENCES `llajtazo`.`lugares` (`id`)
    ON DELETE SET NULL,
  CONSTRAINT `events_ibfk_3`
    FOREIGN KEY (`categoria_id`)
    REFERENCES `llajtazo`.`categorias` (`id`)
    ON DELETE SET NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`compartidos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`compartidos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NULL DEFAULT NULL,
  `evento_id` INT NULL DEFAULT NULL,
  `canal` VARCHAR(50) NULL DEFAULT NULL,
  `creado_en` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `usuario_id` (`usuario_id` ASC) VISIBLE,
  INDEX `evento_id` (`evento_id` ASC) VISIBLE,
  CONSTRAINT `compartidos_ibfk_1`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `llajtazo`.`usuarios` (`id`)
    ON DELETE SET NULL,
  CONSTRAINT `compartidos_ibfk_2`
    FOREIGN KEY (`evento_id`)
    REFERENCES `llajtazo`.`events` (`id`)
    ON DELETE SET NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`estadisticas_evento_diarias`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`estadisticas_evento_diarias` (
  `evento_id` INT NOT NULL,
  `dia` DATE NOT NULL,
  `visitas` INT NULL DEFAULT '0',
  `tickets_vendidos` INT NULL DEFAULT '0',
  PRIMARY KEY (`evento_id`, `dia`),
  CONSTRAINT `estadisticas_evento_diarias_ibfk_1`
    FOREIGN KEY (`evento_id`)
    REFERENCES `llajtazo`.`events` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`favoritos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`favoritos` (
  `usuario_id` INT NOT NULL,
  `evento_id` INT NOT NULL,
  `creado_en` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`usuario_id`, `evento_id`),
  INDEX `evento_id` (`evento_id` ASC) VISIBLE,
  CONSTRAINT `favoritos_ibfk_1`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `llajtazo`.`usuarios` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `favoritos_ibfk_2`
    FOREIGN KEY (`evento_id`)
    REFERENCES `llajtazo`.`events` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`imagenes_evento`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`imagenes_evento` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `event_id` INT NOT NULL,
  `url` TEXT NOT NULL,
  `orden` INT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  INDEX `event_id` (`event_id` ASC) VISIBLE,
  CONSTRAINT `imagenes_evento_ibfk_1`
    FOREIGN KEY (`event_id`)
    REFERENCES `llajtazo`.`events` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`intereses_usuario`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`intereses_usuario` (
  `usuario_id` INT NOT NULL,
  `categorias_id` INT NOT NULL,
  PRIMARY KEY (`usuario_id`, `categorias_id`),
  INDEX `fk_intereses_usuario_categorias1_idx` (`categorias_id` ASC) VISIBLE,
  CONSTRAINT `fk_intereses_usuario`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `llajtazo`.`usuarios` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_intereses_usuario_categorias1`
    FOREIGN KEY (`categorias_id`)
    REFERENCES `llajtazo`.`categorias` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`invitaciones`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`invitaciones` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `invitador_id` INT NOT NULL,
  `email_invitado` VARCHAR(255) NULL DEFAULT NULL,
  `evento_id` INT NULL DEFAULT NULL,
  `usuario_aceptado_id` INT NULL DEFAULT NULL,
  `creado_en` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `invitador_id` (`invitador_id` ASC) VISIBLE,
  INDEX `evento_id` (`evento_id` ASC) VISIBLE,
  INDEX `usuario_aceptado_id` (`usuario_aceptado_id` ASC) VISIBLE,
  CONSTRAINT `invitaciones_ibfk_1`
    FOREIGN KEY (`invitador_id`)
    REFERENCES `llajtazo`.`usuarios` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `invitaciones_ibfk_2`
    FOREIGN KEY (`evento_id`)
    REFERENCES `llajtazo`.`events` (`id`)
    ON DELETE SET NULL,
  CONSTRAINT `invitaciones_ibfk_3`
    FOREIGN KEY (`usuario_aceptado_id`)
    REFERENCES `llajtazo`.`usuarios` (`id`)
    ON DELETE SET NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`ordenes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`ordenes` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NULL DEFAULT NULL,
  `organizador_id` INT NULL DEFAULT NULL,
  `estado` VARCHAR(30) NULL DEFAULT 'PENDING',
  `subtotal` DOUBLE NOT NULL,
  `fees` DOUBLE NULL DEFAULT '0',
  `total` DOUBLE NOT NULL,
  `creado_en` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `usuario_id` (`usuario_id` ASC) VISIBLE,
  INDEX `organizador_id` (`organizador_id` ASC) VISIBLE,
  CONSTRAINT `ordenes_ibfk_1`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `llajtazo`.`usuarios` (`id`)
    ON DELETE SET NULL,
  CONSTRAINT `ordenes_ibfk_2`
    FOREIGN KEY (`organizador_id`)
    REFERENCES `llajtazo`.`organizadores` (`id`)
    ON DELETE SET NULL)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`tipos_ticket`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`tipos_ticket` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `evento_id` INT NOT NULL,
  `nombre` VARCHAR(100) NOT NULL,
  `precio` DOUBLE NOT NULL,
  `moneda` VARCHAR(30) NULL DEFAULT 'BOB',
  `cantidad_total` INT NOT NULL,
  `cantidad_vendida` INT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  INDEX `evento_id` (`evento_id` ASC) VISIBLE,
  CONSTRAINT `tipos_ticket_ibfk_1`
    FOREIGN KEY (`evento_id`)
    REFERENCES `llajtazo`.`events` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`items_orden`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`items_orden` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `orden_id` INT NOT NULL,
  `tipo_ticket_id` INT NOT NULL,
  `precio_unitario` DOUBLE NOT NULL,
  `cantidad` INT NOT NULL,
  `codigo_ticket` VARCHAR(40) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `codigo_ticket` (`codigo_ticket` ASC) VISIBLE,
  INDEX `orden_id` (`orden_id` ASC) VISIBLE,
  INDEX `tipo_ticket_id` (`tipo_ticket_id` ASC) VISIBLE,
  CONSTRAINT `items_orden_ibfk_1`
    FOREIGN KEY (`orden_id`)
    REFERENCES `llajtazo`.`ordenes` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `items_orden_ibfk_2`
    FOREIGN KEY (`tipo_ticket_id`)
    REFERENCES `llajtazo`.`tipos_ticket` (`id`)
    ON DELETE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`notificaciones`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`notificaciones` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NOT NULL,
  `tipo` VARCHAR(100) NULL DEFAULT NULL,
  `titulo` VARCHAR(200) NOT NULL,
  `cuerpo` TEXT NULL DEFAULT NULL,
  `data` JSON NULL DEFAULT NULL,
  `creado_en` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `leido_en` TIMESTAMP NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `usuario_id` (`usuario_id` ASC) VISIBLE,
  CONSTRAINT `notificaciones_ibfk_1`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `llajtazo`.`usuarios` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`pagos`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`pagos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `orden_id` INT NOT NULL,
  `proveedor` VARCHAR(50) NOT NULL,
  `referencia_proveedor` VARCHAR(255) NULL DEFAULT NULL,
  `monto` DOUBLE NOT NULL,
  `estado` VARCHAR(100) NULL DEFAULT NULL,
  `pagado_en` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `orden_id` (`orden_id` ASC) VISIBLE,
  CONSTRAINT `pagos_ibfk_1`
    FOREIGN KEY (`orden_id`)
    REFERENCES `llajtazo`.`ordenes` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `llajtazo`.`seguidores`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `llajtazo`.`seguidores` (
  `usuario_id` INT NOT NULL,
  `organizador_id` INT NOT NULL,
  `creado_en` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`usuario_id`, `organizador_id`),
  INDEX `organizador_id` (`organizador_id` ASC) VISIBLE,
  CONSTRAINT `seguidores_ibfk_1`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `llajtazo`.`usuarios` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `seguidores_ibfk_2`
    FOREIGN KEY (`organizador_id`)
    REFERENCES `llajtazo`.`organizadores` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
