import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PeopleMenu extends JFrame {
    // Komponen GUI
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JTextField idField;
    private JTextField namaField;
    private JTextField tahunLahirField;
    private JComboBox<String> kategoriComboBox;
    private JComboBox<String> genderComboBox;
    private JTable personTable;
    private JButton addUpdateButton;
    private JButton deleteButton;
    private JButton cancelButton;

    // Atribut Database & Selected Index (Tanpa ArrayList)
    private Database database;
    private int selectedIndex = -1;

    // Main Method
    public static void main(String[] args) {
        PeopleMenu menu = new PeopleMenu();
        menu.setSize(750, 650);
        menu.setLocationRelativeTo(null);
        menu.setContentPane(menu.mainPanel);
        menu.getContentPane().setBackground(Color.WHITE);
        menu.setVisible(true);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Constructor
    public PeopleMenu() {
        // Inisialisasi Koneksi Database
        database = new Database();

        // Judul Utama
        titleLabel.setText("Data Musisi — Xdinary Heroes");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        // Options Combo Box Peran/Kategori Musisi
        String[] kategoriData = { "--- Pilih Peran/Kategori ---", "Vocalist", "Guitarist", "Bassist", "Drummer", "Keyboardist", "Synthesizer" };
        kategoriComboBox.setModel(new DefaultComboBoxModel<>(kategoriData));

        String[] genderData = { "--- Pilih Jenis Kelamin ---", "Laki-laki", "Perempuan" };
        genderComboBox.setModel(new DefaultComboBoxModel<>(genderData));

        // Tampilkan Data dari Database MySQL ke Tabel
        personTable.setModel(setTable());

        // Sembunyikan tombol Delete awal
        deleteButton.setVisible(false);

        // Listener: Tombol Add / Update
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1) {
                    insertData();
                } else {
                    updateData();
                }
            }
        });

        // Listener: Tombol Delete
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex >= 0) {
                    String curNama = personTable.getModel().getValueAt(selectedIndex, 2).toString();
                    int confirm = JOptionPane.showConfirmDialog(
                            mainPanel,
                            "Apakah Anda yakin ingin menghapus data musisi " + curNama + "?",
                            "Konfirmasi Hapus Data",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteData();
                    }
                }
            }
        });

        // Listener: Tombol Cancel
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        // Listener: Klik Baris Tabel (Mengambil data langsung dari JTable)
        personTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedIndex = personTable.getSelectedRow();

                if (selectedIndex != -1) {
                    String curId = personTable.getModel().getValueAt(selectedIndex, 1).toString();
                    String curNama = personTable.getModel().getValueAt(selectedIndex, 2).toString();
                    String curTahun = personTable.getModel().getValueAt(selectedIndex, 3).toString();
                    String curKategori = personTable.getModel().getValueAt(selectedIndex, 4).toString();
                    String curGender = personTable.getModel().getValueAt(selectedIndex, 5).toString();

                    idField.setText(curId);
                    namaField.setText(curNama);
                    tahunLahirField.setText(curTahun);
                    kategoriComboBox.setSelectedItem(curKategori);
                    genderComboBox.setSelectedItem(curGender);

                    // Kunci ID agar Primary Key tidak bisa diedit saat Update
                    idField.setEditable(false);

                    addUpdateButton.setText("Update");
                    deleteButton.setVisible(true);
                }
            }
        });
    }

    // Read Data dari Database MySQL
    public final DefaultTableModel setTable() {
        Object[] cols = { "No", "ID", "Nama", "Tahun Lahir", "Peran/Kategori", "Jenis Kelamin" };
        DefaultTableModel tmp = new DefaultTableModel(null, cols);

        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM person");
            int i = 1;
            while (resultSet.next()) {
                Object[] row = new Object[6];
                row[0] = i;
                row[1] = resultSet.getString("id");
                row[2] = resultSet.getString("nama");
                row[3] = resultSet.getString("thn_lahir");
                row[4] = resultSet.getString("kategori");
                row[5] = resultSet.getString("jenis_kelamin");
                tmp.addRow(row);
                i++;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(mainPanel, "Gagal mengambil data dari database: " + e.getMessage(), "Error Database", JOptionPane.ERROR_MESSAGE);
        }

        return tmp;
    }

    // Create Data Musisi Baru
    public void insertData() {
        try {
            String id = idField.getText().trim();
            String nama = namaField.getText().trim();
            String tahunStr = tahunLahirField.getText().trim();

            if (id.isEmpty() || nama.isEmpty() || tahunStr.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Semua kolom input wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (kategoriComboBox.getSelectedIndex() == 0 || genderComboBox.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(mainPanel, "Silakan pilih Peran dan Jenis Kelamin yang valid!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Cek ID Duplikat di MySQL
            ResultSet checkId = database.selectQuery("SELECT * FROM person WHERE id = '" + id + "'");
            if (checkId.next()) {
                JOptionPane.showMessageDialog(mainPanel, "ID '" + id + "' sudah terdaftar! Gunakan ID lain.", "Error ID Duplikat", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int tahunLahir = Integer.parseInt(tahunStr);
            String kategori = kategoriComboBox.getSelectedItem().toString();
            String jenisKelamin = genderComboBox.getSelectedItem().toString();

            String sqlQuery = "INSERT INTO person VALUES('" + id + "', '" + nama + "', " + tahunLahir + ", '" + kategori + "', '" + jenisKelamin + "')";
            database.insertUpdateDeleteQuery(sqlQuery);

            personTable.setModel(setTable());
            clearForm();

            JOptionPane.showMessageDialog(mainPanel, "Data musisi berhasil ditambahkan ke database!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Gagal menyimpan data: " + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Tahun Lahir harus berupa angka!", "Error Format", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Update Data Musisi
    public void updateData() {
        try {
            if (selectedIndex < 0) return;

            String id = idField.getText().trim();
            String nama = namaField.getText().trim();
            String tahunStr = tahunLahirField.getText().trim();

            if (id.isEmpty() || nama.isEmpty() || tahunStr.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Semua kolom input wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (kategoriComboBox.getSelectedIndex() == 0 || genderComboBox.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(mainPanel, "Silakan pilih Peran dan Jenis Kelamin yang valid!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int tahunLahir = Integer.parseInt(tahunStr);
            String kategori = kategoriComboBox.getSelectedItem().toString();
            String jenisKelamin = genderComboBox.getSelectedItem().toString();

            String sqlQuery = "UPDATE person SET nama = '" + nama + "', thn_lahir = " + tahunLahir +
                    ", kategori = '" + kategori + "', jenis_kelamin = '" + jenisKelamin +
                    "' WHERE id = '" + id + "'";
            database.insertUpdateDeleteQuery(sqlQuery);

            personTable.setModel(setTable());
            clearForm();

            JOptionPane.showMessageDialog(mainPanel, "Data musisi berhasil diubah!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(mainPanel, "Tahun Lahir harus berupa angka!", "Error Format", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Delete Data Musisi
    public void deleteData() {
        if (selectedIndex >= 0) {
            String id = idField.getText().trim();

            String sqlQuery = "DELETE FROM person WHERE id = '" + id + "'";
            database.insertUpdateDeleteQuery(sqlQuery);

            personTable.setModel(setTable());
            clearForm();

            JOptionPane.showMessageDialog(mainPanel, "Data musisi berhasil dihapus!");
        }
    }

    // Reset Form
    public void clearForm() {
        idField.setText("");
        idField.setEditable(true);
        namaField.setText("");
        tahunLahirField.setText("");
        kategoriComboBox.setSelectedIndex(0);
        genderComboBox.setSelectedIndex(0);

        addUpdateButton.setText("Add");
        deleteButton.setVisible(false);
        selectedIndex = -1;
    }
}