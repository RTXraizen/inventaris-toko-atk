package com.tokoatk;

import com.tokoatk.dao.SupplierDAO;
import com.tokoatk.model.Supplier;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
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
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SupplierController implements Initializable {

    // --- DAO INJECTION ---
    private final SupplierDAO supplierDAO = new SupplierDAO();

    // --- FXML ELEMENTS ---
    @FXML private TableView<Supplier> tabelSupplier;
    @FXML private TableColumn<Supplier, String> kolomNama;
    @FXML private TableColumn<Supplier, String> kolomKontak;
    @FXML private TableColumn<Supplier, String> kolomTelepon;
    @FXML private TableColumn<Supplier, String> kolomAlamat;

    @FXML private TextField fieldNama;
    @FXML private TextField fieldKontak;
    @FXML private TextField fieldTelepon;
    @FXML private TextArea fieldAlamat;

    @FXML private Button btnSimpan;
    @FXML private Button btnHapus;
    @FXML private Button btnBersih;
    
    private Supplier supplierTerpilih = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup kolom tabel
        kolomNama.setCellValueFactory(new PropertyValueFactory<>("namaSupplier"));
        kolomKontak.setCellValueFactory(new PropertyValueFactory<>("kontakPerson"));
        kolomTelepon.setCellValueFactory(new PropertyValueFactory<>("nomorTelepon"));
        kolomAlamat.setCellValueFactory(new PropertyValueFactory<>("alamat"));

        // Listener untuk seleksi tabel
        tabelSupplier.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> tampilkanDetailSupplier(newValue)
        );

        btnHapus.setDisable(true);
        muatDataTabelSupplier();
    }
    
    private void muatDataTabelSupplier() {
        ObservableList<Supplier> listSupplier = supplierDAO.getAllSuppliers();
        tabelSupplier.setItems(listSupplier);
    }
    
    private void tampilkanDetailSupplier(Supplier supplier) {
        if (supplier != null) {
            supplierTerpilih = supplier;
            fieldNama.setText(supplier.getNamaSupplier());
            fieldTelepon.setText(supplier.getNomorTelepon());
            fieldAlamat.setText(supplier.getAlamat());
            btnHapus.setDisable(false);
        } else {
            bersihkanForm();
        }
    }
    
    private void bersihkanForm() {
        tabelSupplier.getSelectionModel().clearSelection();
        supplierTerpilih = null;
        fieldNama.clear();
        fieldKontak.clear();
        fieldTelepon.clear();
        fieldAlamat.clear();
        btnHapus.setDisable(true);
    }

    @FXML
    private void handleSimpanButton(ActionEvent event) {
        String nama = fieldNama.getText();
        String kontak = fieldKontak.getText();
        String telepon = fieldTelepon.getText();
        String alamat = fieldAlamat.getText();

        if (nama.isEmpty()) {
            tampilkanAlert(Alert.AlertType.ERROR, "Error", "Nama Supplier wajib diisi.");
            return;
        }

        if (supplierTerpilih == null) { // Mode Tambah Data
            Supplier supplierBaru = new Supplier(0, nama, telepon, alamat);
            if (supplierDAO.addSupplier(supplierBaru)) {
                tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Supplier baru berhasil disimpan.");
            }
        } else { // Mode Update Data
            supplierTerpilih.setNamaSupplier(nama);
            supplierTerpilih.setNomorTelepon(telepon);
            supplierTerpilih.setAlamat(alamat);
            if (supplierDAO.updateSupplier(supplierTerpilih)) {
                tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Data supplier berhasil diperbarui.");
            }
        }
        muatDataTabelSupplier();
        bersihkanForm();
    }
    
    @FXML
    private void handleHapusButton(ActionEvent event) {
        if (supplierTerpilih != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Konfirmasi Hapus");
            alert.setHeaderText("Hapus Supplier");
            alert.setContentText("Apakah Anda yakin ingin menghapus supplier '" + supplierTerpilih.getNamaSupplier() + "'?");
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if(supplierDAO.deleteSupplier(supplierTerpilih.getSupplierId())) {
                    tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Supplier berhasil dihapus.");
                    muatDataTabelSupplier();
                    bersihkanForm();
                }
            }
        }
    }
    
    @FXML
    private void handleBersihButton(ActionEvent event) {
        bersihkanForm();
    }
    
    private void tampilkanAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        try {
            Image icon = new Image(getClass().getResourceAsStream("iconzoro.jpeg"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            // Abaikan jika icon tidak ditemukan
        }
        alert.showAndWait();
    }
}