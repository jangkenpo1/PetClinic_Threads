import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class PetClinicFrame extends JFrame {
    private final PasienHewanDAO dao = new PasienHewanDAO();

    private final JTextField txtId = new JTextField("(Otomatis)");
    private final JTextField txtNama = new JTextField();
    private final JComboBox<String> cmbJenis = new JComboBox<>(
            new String[]{"Kucing", "Anjing", "Kelinci", "Burung", "Hamster"}
    );
    private final JTextField txtKeluhan = new JTextField();

    private final JButton btnSimpan = new JButton("Simpan");
    private final JButton btnUpdate = new JButton("Update");
    private final JButton btnHapus = new JButton("Hapus");
    private final JButton btnReset = new JButton("Reset");

    private final JLabel lblStatus = new JLabel("Status: Ready");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Nama Hewan", "Jenis", "Keluhan"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    private int selectedId = -1;

    private static final int DEMO_DELAY_MS = 800;

    public PetClinicFrame() {
        setTitle("Klinik Hewan (Pet Clinic) - Thread & Database");
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        initEvents();
        loadData();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        // ID otomatis dari MySQL (AUTO_INCREMENT)
        txtId.setEditable(false);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 8, 6));
        formPanel.add(new JLabel("ID Pasien:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Nama Hewan:"));
        formPanel.add(txtNama);
        formPanel.add(new JLabel("Jenis Hewan:"));
        formPanel.add(cmbJenis);
        formPanel.add(new JLabel("Keluhan:"));
        formPanel.add(txtKeluhan);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnReset);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.BOLD));

        root.add(topPanel, BorderLayout.NORTH);
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        root.add(lblStatus, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void initEvents() {
        btnSimpan.addActionListener(e -> simpanData());
        btnUpdate.addActionListener(e -> updateData());
        btnHapus.addActionListener(e -> hapusData());
        btnReset.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int row = table.getSelectedRow();
                selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                txtId.setText(String.valueOf(selectedId));
                txtNama.setText(tableModel.getValueAt(row, 1).toString());
                cmbJenis.setSelectedItem(tableModel.getValueAt(row, 2).toString());
                txtKeluhan.setText(tableModel.getValueAt(row, 3).toString());
            }
        });
    }

    private void simpanData() {
        String nama = txtNama.getText().trim();
        String jenis = cmbJenis.getSelectedItem().toString();
        String keluhan = txtKeluhan.getText().trim();

        if (nama.isEmpty() || keluhan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Hewan dan Keluhan wajib diisi.");
            return;
        }

        setButtonsEnabled(false);
        lblStatus.setText("Status: Saving...");

        PasienHewan pasien = new PasienHewan(nama, jenis, keluhan);

        new Thread(() -> {
            try {
                Thread.sleep(DEMO_DELAY_MS);
                dao.insert(pasien);
                List<PasienHewan> list = dao.findAll();

                SwingUtilities.invokeLater(() -> {
                    tampilkanTabel(list);
                    clearForm();
                    lblStatus.setText("Status: Data berhasil disimpan.");
                    setButtonsEnabled(true);
                });
            } catch (Exception ex) {
                tampilkanError("Gagal menyimpan data", ex);
            }
        }).start();
    }

    private void updateData() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Klik salah satu baris pada tabel untuk diubah.");
            return;
        }

        String nama = txtNama.getText().trim();
        String jenis = cmbJenis.getSelectedItem().toString();
        String keluhan = txtKeluhan.getText().trim();

        if (nama.isEmpty() || keluhan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Hewan dan Keluhan wajib diisi.");
            return;
        }

        setButtonsEnabled(false);
        lblStatus.setText("Status: Updating...");

        PasienHewan pasien = new PasienHewan(selectedId, nama, jenis, keluhan);

        new Thread(() -> {
            try {
                Thread.sleep(DEMO_DELAY_MS);
                boolean updated = dao.update(pasien);
                List<PasienHewan> list = dao.findAll();

                SwingUtilities.invokeLater(() -> {
                    tampilkanTabel(list);
                    if (updated) {
                        clearForm();
                        lblStatus.setText("Status: Data berhasil diperbarui.");
                    } else {
                        lblStatus.setText("Status: Data tidak ditemukan.");
                    }
                    setButtonsEnabled(true);
                });
            } catch (Exception ex) {
                tampilkanError("Gagal memperbarui data", ex);
            }
        }).start();
    }

    private void hapusData() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Klik salah satu baris pada tabel untuk dihapus.");
            return;
        }

        int idToDelete = selectedId;
        setButtonsEnabled(false);
        lblStatus.setText("Status: Deleting...");

        new Thread(() -> {
            try {
                Thread.sleep(DEMO_DELAY_MS);
                boolean deleted = dao.delete(idToDelete);
                List<PasienHewan> list = dao.findAll();

                SwingUtilities.invokeLater(() -> {
                    tampilkanTabel(list);
                    if (deleted) {
                        clearForm();
                        lblStatus.setText("Status: Data berhasil dihapus.");
                    } else {
                        lblStatus.setText("Status: Data tidak ditemukan.");
                    }
                    setButtonsEnabled(true);
                });
            } catch (Exception ex) {
                tampilkanError("Gagal menghapus data", ex);
            }
        }).start();
    }

    private void loadData() {
        setButtonsEnabled(false);
        lblStatus.setText("Status: Loading...");

        new Thread(() -> {
            try {
                List<PasienHewan> list = dao.findAll();
                SwingUtilities.invokeLater(() -> {
                    tampilkanTabel(list);
                    lblStatus.setText("Status: Ready (" + list.size() + " pasien)");
                    setButtonsEnabled(true);
                });
            } catch (Exception ex) {
                tampilkanError("Gagal memuat data", ex);
            }
        }).start();
    }

    private void tampilkanTabel(List<PasienHewan> list) {
        tableModel.setRowCount(0);
        for (PasienHewan p : list) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getNamaHewan(), p.getJenisHewan(), p.getKeluhan()
            });
        }
    }

    private void tampilkanError(String pesan, Exception ex) {
        SwingUtilities.invokeLater(() -> {
            lblStatus.setText("Status: Error.");
            setButtonsEnabled(true);
            JOptionPane.showMessageDialog(this, pesan + ":\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    private void clearForm() {
        selectedId = -1;
        txtId.setText("(Otomatis)");
        txtNama.setText("");
        cmbJenis.setSelectedIndex(0);
        txtKeluhan.setText("");
        table.clearSelection();
        txtNama.requestFocus();
    }

    private void setButtonsEnabled(boolean enabled) {
        btnSimpan.setEnabled(enabled);
        btnUpdate.setEnabled(enabled);
        btnHapus.setEnabled(enabled);
        btnReset.setEnabled(enabled);
    }
}
