package com.tokoatk;

import com.tokoatk.dao.SupplierDAO;
import com.tokoatk.model.Supplier;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ManageSupplierController implements Initializable {

    @FXML private TableView<Supplier> tabelSupplier;
    @FXML private TableColumn<Supplier, String> kolomNama;
    @FXML private TableColumn<Supplier, String> kolomTelepon;
    @FXML private TableColumn<Supplier, String> kolomAlamat;
    @FXML private TextField fieldNama;
    @FXML private TextField fieldTelepon;
    @FXML private TextArea fieldAlamat;
    @FXML private Button btnSimpan;
    @FXML private Button btnHapus;
    @FXML private Button btnBersih;
    @FXML private Button btnTutup;

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private Supplier supplierTerpilih = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        kolomNama.setCellValueFactory(new PropertyValueFactory<>("namaSupplier"));
        kolomTelepon.setCellValueFactory(new PropertyValueFactory<>("nomorTelepon"));
        kolomAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));
        
        tabelSupplier.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    supplierTerpilih = newSelection;
                    tampilkanDetailSupplier();
                });
        
        muatDataSupplier();
    }
    
    private void muatDataSupplier() {
        tabelSupplier.setItems(supplierDAO.getAllSuppliers());
    }
    
    private void tampilkanDetailSupplier() {
        if (supplierTerpilih != null) {
            fieldNama.setText(supplierTerpilih.getNamaSupplier());
            fieldTelepon.setText(supplierTerpilih.getNomorTelepon());
            fieldAlamat.setText(supplierTerpilih.getAlamat());
        } else {
            bersihkanForm();
        }
    }
    
    @FXML
    private void handleSimpanButton(ActionEvent event) {
        String nama = fieldNama.getText();
        if (nama == null || nama.trim().isEmpty()) {
            tampilkanAlert(Alert.AlertType.ERROR, "Error", "Nama Supplier tidak boleh kosong.");
            return;
        }
        
        boolean sukses = false;
        String pesanSukses = "";

        if (supplierTerpilih == null) { // Mode Tambah Baru
            Supplier supplierBaru = new Supplier(0, nama, fieldTelepon.getText(), fieldAlamat.getText());
            sukses = supplierDAO.addSupplier(supplierBaru);
            pesanSukses = "Supplier baru berhasil ditambahkan.";
        } else { // Mode Update
            supplierTerpilih.setNamaSupplier(nama);
            supplierTerpilih.setNomorTelepon(fieldTelepon.getText());
            supplierTerpilih.setAlamat(fieldAlamat.getText());
            sukses = supplierDAO.updateSupplier(supplierTerpilih);
            pesanSukses = "Data supplier berhasil diperbarui.";
        }
        
        if (sukses) {
            tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", pesanSukses);
            handleTutupButton(null); // Panggil metode tutup untuk menutup window
        } else {
            tampilkanAlert(Alert.AlertType.ERROR, "Gagal", "Operasi gagal disimpan ke database.");
        }
    }

    @FXML
    private void handleHapusButton(ActionEvent event) {
        if (supplierTerpilih == null) {
            tampilkanAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih supplier yang ingin dihapus dari tabel.");
            return;
        }

        if (supplierDAO.isSupplierInUse(supplierTerpilih.getSupplierId())) {
            tampilkanAlert(Alert.AlertType.ERROR, "Gagal Menghapus", "Supplier ini tidak bisa dihapus karena masih digunakan oleh data barang.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menghapus '" + supplierTerpilih.getNamaSupplier() + "'?", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Konfirmasi Hapus");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (supplierDAO.deleteSupplier(supplierTerpilih.getSupplierId())) {
                tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Supplier berhasil dihapus.");
                muatDataSupplier(); // Muat ulang tabel di dialog
                bersihkanForm();    // Bersihkan form setelah hapus
            }
        }
    }
    
    @FXML
    private void handleBersihButton(ActionEvent event) {
        bersihkanForm();
    }
    
    private void bersihkanForm() {
        tabelSupplier.getSelectionModel().clearSelection();
        supplierTerpilih = null;
        fieldNama.clear();
        fieldTelepon.clear();
        fieldAlamat.clear();
    }

    @FXML
    private void handleTutupButton(ActionEvent event) {
        Stage stage = (Stage) btnTutup.getScene().getWindow();
        stage.close();
    }
    
    private void tampilkanAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}