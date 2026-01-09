-- Run this in SQL Server (adjust file encoding if needed)
CREATE DATABASE BugTracker;
GO
USE BugTracker;
GO

CREATE TABLE [User] (
    id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(100) NOT NULL UNIQUE,
    password NVARCHAR(256) NOT NULL,
    full_name NVARCHAR(200),
    email NVARCHAR(200),
    role NVARCHAR(50)
);

CREATE TABLE Product (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(200) NOT NULL,
    version NVARCHAR(50),
    description NVARCHAR(MAX)
);

CREATE TABLE Bug (
    id INT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(300) NOT NULL,
    description NVARCHAR(MAX),
    steps NVARCHAR(MAX),
    severity NVARCHAR(50),
    status NVARCHAR(50),
    created_at DATETIME2 DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2 DEFAULT SYSUTCDATETIME(),
    reporter_id INT FOREIGN KEY REFERENCES [User](id),
    assignee_id INT NULL FOREIGN KEY REFERENCES [User](id),
    product_id INT FOREIGN KEY REFERENCES Product(id)
);

CREATE TABLE BugComment (
    id INT IDENTITY(1,1) PRIMARY KEY,
    text NVARCHAR(MAX),
    created_at DATETIME2 DEFAULT SYSUTCDATETIME(),
    author_id INT FOREIGN KEY REFERENCES [User](id),
    bug_id INT FOREIGN KEY REFERENCES Bug(id)
);

CREATE TABLE BugHistory (
    id INT IDENTITY(1,1) PRIMARY KEY,
    action NVARCHAR(500),
    created_at DATETIME2 DEFAULT SYSUTCDATETIME(),
    bug_id INT FOREIGN KEY REFERENCES Bug(id)
);

-- sample data
INSERT INTO [User](username, password, full_name, email, role) VALUES
('admin','admin123','Administrator','admin@local','ADMIN'),
('manager','manager123','Project Manager','manager@local','MANAGER'),
('tech','tech123','Tech Person','tech@local','TECH_PERSON'),
('customer','cust123','Customer','cust@local','CUSTOMER');

INSERT INTO Product(name, version, description) VALUES
('App1','1.0','First product'),
('App2','2.1','Portal product');

GO
