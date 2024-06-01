import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.*;

public class Main {
    private static final String HOSTNAME = "rsk.h.filess.io";
    private static final String DATABASE = "myProject_viewsource";
    private static final String PORT = "3307";
    private static final String USERNAME = "myProject_viewsource";
    private static final String PASSWORD = "basmalah";

    private static MySQL mysql;
    private static Connection conn;
    private static DefaultTableModel tableModel;

    public static void main(String[] args) {
        mysql = new MySQL(HOSTNAME, DATABASE, PORT, USERNAME, PASSWORD);
        try {
            conn = mysql.connect();
            System.out.println("Connected: " + !conn.isClosed());

            JFrame frame = new JFrame("PhoneBook Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            // Load the application icon
            URL iconURL = Main.class.getResource("/app_icon.png");
            if (iconURL != null) {
                ImageIcon appIcon = new ImageIcon(iconURL);
                frame.setIconImage(appIcon.getImage());
            } else {
                System.err.println("Icon not found, using default icon.");
            }

            // Set background color
            frame.getContentPane().setBackground(new Color(220, 220, 220));

            JMenuBar menuBar = new JMenuBar();
            JMenu helpMenu = new JMenu("Help");
            JMenuItem helpItem = new JMenuItem("How to Use");
            helpMenu.add(helpItem);
            menuBar.add(helpMenu);
            frame.setJMenuBar(menuBar);

            JPanel panel = new JPanel();
            panel.setLayout(null);
            panel.setBackground(new Color(240, 240, 240));
            frame.add(panel);

            helpItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showHelpDialog();
                }
            });

            placeComponents(panel);

            frame.setVisible(true);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void placeComponents(JPanel panel) {
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(10, 20, 80, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20);
        nameText.setBounds(100, 20, 165, 25);
        nameText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        panel.add(nameText);

        JLabel subNameLabel = new JLabel("SubName");
        subNameLabel.setBounds(10, 50, 80, 25);
        panel.add(subNameLabel);

        JTextField subNameText = new JTextField(20);
        subNameText.setBounds(100, 50, 165, 25);
        subNameText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        panel.add(subNameText);

        JLabel phoneLabel = new JLabel("Phone");
        phoneLabel.setBounds(10, 80, 80, 25);
        panel.add(phoneLabel);

        JTextField phoneText = new JTextField(20);
        phoneText.setBounds(100, 80, 165, 25);
        phoneText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        panel.add(phoneText);

        JLabel addressLabel = new JLabel("Address");
        addressLabel.setBounds(10, 110, 80, 25);
        panel.add(addressLabel);

        JTextField addressText = new JTextField(20);
        addressText.setBounds(100, 110, 165, 25);
        addressText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        panel.add(addressText);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setBounds(10, 140, 80, 25);
        panel.add(emailLabel);

        JTextField emailText = new JTextField(20);
        emailText.setBounds(100, 140, 165, 25);
        emailText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        panel.add(emailText);

        JButton addButton = new JButton("Add");
        addButton.setBounds(10, 170, 80, 25);
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        panel.add(addButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(100, 170, 80, 25);
        deleteButton.setBackground(new Color(178, 34, 34));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        panel.add(deleteButton);

        JButton editButton = new JButton("Edit");
        editButton.setBounds(190, 170, 80, 25);
        editButton.setBackground(new Color(34, 139, 34));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        panel.add(editButton);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "SubName", "Phone", "Address", "Email"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 200, 760, 350);
        panel.add(scrollPane);

        // Add mouse listener to the table to fill in the text fields when a row is clicked
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String name = (String) tableModel.getValueAt(selectedRow, 1);
                    String subName = (String) tableModel.getValueAt(selectedRow, 2);
                    String phone = (String) tableModel.getValueAt(selectedRow, 3);
                    String address = (String) tableModel.getValueAt(selectedRow, 4);
                    String email = (String) tableModel.getValueAt(selectedRow, 5);

                    nameText.setText(name);
                    subNameText.setText(subName);
                    phoneText.setText(phone);
                    addressText.setText(address);
                    emailText.setText(email);
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameText.getText();
                String subName = subNameText.getText();
                String phone = phoneText.getText();
                String address = addressText.getText();
                String email = emailText.getText();
                if (name.isEmpty() || subName.isEmpty() || phone.isEmpty() || address.isEmpty() || email.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                addData(name, subName, phone, address, email);
                nameText.setText("");
                subNameText.setText("");
                phoneText.setText("");
                addressText.setText("");
                emailText.setText("");
                refreshTable();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) tableModel.getValueAt(selectedRow, 0);
                    deleteData(id);
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(panel, "Please select a row to delete", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) tableModel.getValueAt(selectedRow, 0);
                    String name = nameText.getText();
                    String subName = subNameText.getText();
                    String phone = phoneText.getText();
                    String address = addressText.getText();
                    String email = emailText.getText();
                    updateData(id, name, subName, phone, address, email);
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(panel, "Please select a row to edit", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        refreshTable();
    }

    private static void addData(String name, String subName, String phone, String address, String email) {
        String sql = "INSERT INTO PhoneBook (nama, subNama, nomorTelefon, alamat, email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, subName);
            pstmt.setString(3, phone);
            pstmt.setString(4, address);
            pstmt.setString(5, email);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteData(int id) {
        String sql = "DELETE FROM PhoneBook WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateData(int id, String name, String subName, String phone, String address, String email) {
        String sql = "UPDATE PhoneBook SET nama = ?, subNama = ?, nomorTelefon = ?, alamat = ?, email = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, subName);
            pstmt.setString(3, phone);
            pstmt.setString(4, address);
            pstmt.setString(5, email);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void refreshTable() {
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM PhoneBook";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("nama");
                String subName = rs.getString("subNama");
                String phone = rs.getString("nomorTelefon");
                String address = rs.getString("alamat");
                String email = rs.getString("email");
                tableModel.addRow(new Object[]{id, name, subName, phone, address, email});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showHelpDialog() {
        String helpMessage = "Cara Menggunakan Program Manajemen PhoneBook:\n\n" +
                "1. Menambahkan Entri Baru:\n" +
                "- Isi semua kolom: Nama, SubNama, Telepon, Alamat, dan Email.\n" +
                "- Klik tombol 'Add' untuk menambahkan entri ke database.\n\n" +
                "2. Menghapus Entri:\n" +
                "- Pilih baris dalam tabel.\n" +
                "- Klik tombol 'Delete' untuk menghapus entri yang dipilih dari database.\n\n" +
                "3. Mengedit Entri:\n" +
                "- Pilih baris dalam tabel.\n" +
                "- Modifikasi kolom: Nama, SubNama, Telepon, Alamat, dan Email.\n" +
                "- Klik tombol 'Edit' dan konfirmasi pembaruan untuk menyimpan perubahan ke database.\n\n" +
                "Catatan: Pastikan untuk memilih baris dalam tabel sebelum mencoba menghapus atau mengedit entri.";

        JOptionPane.showMessageDialog(null, helpMessage, "Cara Menggunakan", JOptionPane.INFORMATION_MESSAGE);
    }
}
