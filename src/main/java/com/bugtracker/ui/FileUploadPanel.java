package com.bugtracker.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern file upload panel with drag-and-drop support
 */
public class FileUploadPanel extends JPanel {
    private List<File> selectedFiles = new ArrayList<>();
    private JPanel fileListPanel;
    private JLabel dropLabel;
    private boolean isDragging = false;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final String[] ALLOWED_EXTENSIONS = {
            ".png", ".jpg", ".jpeg", ".gif", ".pdf", ".txt", ".log", ".zip"
    };

    public FileUploadPanel() {
        setLayout(new BorderLayout(0, 15));
        setOpaque(false);

        initDropZone();
        initFileList();
        setupDragAndDrop();
    }

    private void initDropZone() {
        JPanel dropZone = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                if (isDragging) {
                    g2.setColor(new Color(138, 180, 248, 50));
                } else {
                    g2.setColor(new Color(255, 255, 255, 30));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_SMALL, Theme.RADIUS_SMALL);

                // Dashed border
                g2.setColor(isDragging ? Theme.ACCENT_GLOW : Theme.GLASS_BORDER);
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                        0, new float[] { 5, 5 }, 0));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2,
                        Theme.RADIUS_SMALL, Theme.RADIUS_SMALL);

                g2.dispose();
            }
        };
        dropZone.setOpaque(false);
        dropZone.setPreferredSize(new Dimension(400, 80));
        dropZone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Label
        dropLabel = new JLabel("<html><center>📎 Drop files here or click to browse<br>" +
                "<small style='color: #cccccc;'>Max 10MB • PNG, JPG, PDF, TXT, ZIP</small></center></html>",
                SwingConstants.CENTER);
        dropLabel.setFont(Theme.FONT_REGULAR);
        dropLabel.setForeground(Theme.TEXT_WHITE);
        dropZone.add(dropLabel, BorderLayout.CENTER);

        // Click to browse
        dropZone.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                browseFiles();
            }
        });

        add(dropZone, BorderLayout.NORTH);
    }

    private void initFileList() {
        fileListPanel = new JPanel();
        fileListPanel.setLayout(new BoxLayout(fileListPanel, BoxLayout.Y_AXIS));
        fileListPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(fileListPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(400, 100));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupDragAndDrop() {
        new DropTarget(this, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                isDragging = true;
                repaint();
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
            }

            @Override
            public void dragExit(DropTargetEvent dte) {
                isDragging = false;
                repaint();
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                isDragging = false;
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    @SuppressWarnings("unchecked")
                    List<File> droppedFiles = (List<File>) dtde.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);

                    for (File file : droppedFiles) {
                        addFile(file);
                    }
                    dtde.dropComplete(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    dtde.dropComplete(false);
                }
                repaint();
            }
        });
    }

    private void browseFiles() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            for (File file : fileChooser.getSelectedFiles()) {
                addFile(file);
            }
        }
    }

    private void addFile(File file) {
        // Validate file
        if (!isValidFile(file)) {
            return;
        }

        // Check if already added
        if (selectedFiles.contains(file)) {
            JOptionPane.showMessageDialog(this, "File already added: " + file.getName(),
                    "Duplicate File", JOptionPane.WARNING_MESSAGE);
            return;
        }

        selectedFiles.add(file);
        addFileToList(file);
    }

    private boolean isValidFile(File file) {
        // Check size
        if (file.length() > MAX_FILE_SIZE) {
            JOptionPane.showMessageDialog(this,
                    "File too large: " + file.getName() + "\nMax size: 10MB",
                    "File Too Large", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check extension
        String fileName = file.getName().toLowerCase();
        boolean validExtension = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (fileName.endsWith(ext)) {
                validExtension = true;
                break;
            }
        }

        if (!validExtension) {
            JOptionPane.showMessageDialog(this,
                    "Invalid file type: " + file.getName() +
                            "\nAllowed: PNG, JPG, PDF, TXT, ZIP",
                    "Invalid File Type", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void addFileToList(File file) {
        JPanel fileItem = new JPanel(new BorderLayout(10, 0));
        fileItem.setOpaque(false);
        fileItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        fileItem.setBorder(new EmptyBorder(5, 10, 5, 10));

        // File info
        JLabel fileLabel = new JLabel("📄 " + file.getName() + " (" + formatFileSize(file.length()) + ")");
        fileLabel.setFont(Theme.FONT_SMALL);
        fileLabel.setForeground(Theme.TEXT_WHITE);
        fileItem.add(fileLabel, BorderLayout.CENTER);

        // Remove button
        JButton removeBtn = new JButton("X");
        removeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        removeBtn.setForeground(Theme.ERROR_COLOR);
        removeBtn.setContentAreaFilled(false);
        removeBtn.setBorderPainted(false);
        removeBtn.setFocusPainted(false);
        removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeBtn.addActionListener(e -> {
            selectedFiles.remove(file);
            fileListPanel.remove(fileItem);
            fileListPanel.revalidate();
            fileListPanel.repaint();
        });
        fileItem.add(removeBtn, BorderLayout.EAST);

        fileListPanel.add(fileItem);
        fileListPanel.revalidate();
        fileListPanel.repaint();
    }

    private String formatFileSize(long size) {
        if (size < 1024)
            return size + " B";
        if (size < 1024 * 1024)
            return String.format("%.1f KB", size / 1024.0);
        return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }

    public List<File> getSelectedFiles() {
        return new ArrayList<>(selectedFiles);
    }

    public void clearFiles() {
        selectedFiles.clear();
        fileListPanel.removeAll();
        fileListPanel.revalidate();
        fileListPanel.repaint();
    }
}
