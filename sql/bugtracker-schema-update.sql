-- Bug Tracker Schema Update: Add File Attachment Support
-- Run this after the initial schema has been created

USE BugTracker;
GO

-- Create BugAttachment table to store file metadata
CREATE TABLE BugAttachment (
    id INT IDENTITY(1,1) PRIMARY KEY,
    bug_id INT NOT NULL FOREIGN KEY REFERENCES Bug(id) ON DELETE CASCADE,
    file_name NVARCHAR(500) NOT NULL,
    file_path NVARCHAR(1000) NOT NULL,
    file_size BIGINT NOT NULL,
    uploaded_at DATETIME2 DEFAULT SYSUTCDATETIME()
);

-- Create index for faster queries by bug_id
CREATE INDEX IX_BugAttachment_BugId ON BugAttachment(bug_id);

GO
