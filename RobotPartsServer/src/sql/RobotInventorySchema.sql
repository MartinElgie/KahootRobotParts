CREATE SCHEMA IF NOT EXISTS Robots;

SET SEARCH_PATH TO Robots;

CREATE TABLE IF NOT EXISTS Manufacturer (
  ManufacturerId int PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  ManufacturerName varchar(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS RobotPart (
  PartId int PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  PartName varchar(100) NOT NULL,
  SerialNumber varchar(36) UNIQUE NOT NULL,
  ManufacturerId int NOT NULL REFERENCES Manufacturer(ManufacturerId),
  WeightGrams numeric(10,3) NOT NULL
);

CREATE TABLE IF NOT EXISTS PartCompatibility (
  CompatibilityId int PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  FirstPart int NOT NULL REFERENCES RobotPart(PartId),
  SecondPart int NOT NULL REFERENCES RobotPart(PartId),
  CHECK (SecondPart > FirstPart)
);