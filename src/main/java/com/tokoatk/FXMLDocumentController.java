package com.tokoatk;

import com.tokoatk.dao.BarangDAO;
import com.tokoatk.dao.KategoriDAO;
import com.tokoatk.dao.SupplierDAO;
import com.tokoatk.model.Barang;
import com.tokoatk.model.Kategori;
import com.tokoatk.model.Supplier;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FXMLDocumentController implements Initializable {

    // --- DAO INJECTION ---
    private final BarangDAO barangDAO = new BarangDAO();
    private final KategoriDAO kategoriDAO = new KategoriDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    // --- FXML ELEMENTS ---
    @FXML private Button btnLogout;
    @FXML private Label labelJam;
    @FXML private Button btnSimpan;
    @FXML private Button btnHapus;
    @FXML private Button btnBersih;
    @FXML private TableView<Barang> tabelBarang;
    @FXML private TableColumn<Barang, String> kolomNama;
    @FXML private TableColumn<Barang, String> kolomKategori;
    @FXML private TableColumn<Barang, String> kolomSupplier;
    @FXML private TableColumn<Barang, Integer> kolomStok;
    @FXML private TableColumn<Barang, Double> kolomHarga;
    @FXML private TableColumn<Barang, String> kolomDeskripsi;
    @FXML private TableColumn<Barang, String> kolomTeleponSupplier;
    @FXML private TableColumn<Barang, String> kolomAlamatSupplier;
    @FXML private TextField fieldNama;
    @FXML private TextField fieldStok;
    @FXML private TextField fieldHarga;
    @FXML private TextArea fieldDeskripsi;
    @FXML private ComboBox<Kategori> comboKategori;
    @FXML private ComboBox<Supplier> comboSupplier;
    @FXML private Button btnManageSupplier;
    @FXML private TextField fieldCari;
    @FXML private Button btnRefresh;
    @FXML private Button btnCetak;
    @FXML private Button btnExport;

    // --- STATE VARIABLES ---
    private Barang barangTerpilih = null;
    private ObservableList<Barang> masterDataBarang = FXCollections.observableArrayList();

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initJam();
        setupTabelBarang();
        // Panggil metode filter baru di sini
        setupFilterListener();
        muatDataKategori();
        muatDataSupplier();
        muatDataTabelBarang();
        btnHapus.setDisable(true);
    }

    /**
     * DIMODIFIKASI: Metode ini sekarang hanya fokus pada pengaturan kolom tabel 
     * dan listener untuk item yang dipilih. Logika filter dipindahkan.
     */
    private void setupTabelBarang() {
        // Pengaturan CellValueFactory
        kolomNama.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        kolomKategori.setCellValueFactory(cellData -> cellData.getValue().namaKategoriProperty());
        kolomStok.setCellValueFactory(new PropertyValueFactory<>("stok"));
        kolomHarga.setCellValueFactory(new PropertyValueFactory<>("hargaJual"));
        kolomDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        kolomSupplier.setCellValueFactory(cellData -> cellData.getValue().namaSupplierProperty());
        kolomTeleponSupplier.setCellValueFactory(cellData -> cellData.getValue().teleponSupplierProperty());
        kolomAlamatSupplier.setCellValueFactory(cellData -> cellData.getValue().alamatSupplierProperty());

        // Listener untuk menampilkan detail saat item dipilih
        tabelBarang.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> tampilkanDetailBarang(newValue));
        
        // Pengaturan format Rupiah untuk kolom harga
        Locale localeID = new Locale("id", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        kolomHarga.setCellFactory(column -> new TableCell<Barang, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatRupiah.format(item));
            }
        });

        // Pengaturan warna background untuk stok rendah
        kolomStok.setCellFactory(column -> new TableCell<Barang, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    setStyle(item <= 10 ? "-fx-background-color: #ffd7d4; -fx-text-fill: red; -fx-font-weight: bold;" : "");
                }
            }
        });
    }
    
    /**
     * BARU: Metode ini berisi semua logika yang berhubungan dengan filter data tabel.
     */
    private void setupFilterListener() {
        // 1. Bungkus masterDataBarang dengan FilteredList.
        FilteredList<Barang> filteredData = new FilteredList<>(masterDataBarang, p -> true);

        // 2. Tambahkan listener pada fieldCari. Setiap kali teks berubah, predicate akan di-update.
        fieldCari.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(barang -> {
                // Jika filter kosong, tampilkan semua barang.
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }
                // Jika tidak, panggil metode helper untuk memeriksa kecocokan.
                return barangMatchesFilter(barang, newValue);
            });
        });

        // 3. Bungkus FilteredList dengan SortedList agar bisa diurutkan.
        SortedList<Barang> sortedData = new SortedList<>(filteredData);

        // 4. Hubungkan comparator dari SortedList ke comparator TabelBarang.
        sortedData.comparatorProperty().bind(tabelBarang.comparatorProperty());

        // 5. Set data yang sudah bisa di-filter dan di-sort ke dalam tabel.
        tabelBarang.setItems(sortedData);
    }
    
    /**
     * BARU: Metode helper untuk memeriksa apakah sebuah objek Barang cocok dengan teks filter.
     * @param barang Objek barang yang akan diperiksa.
     * @param filterText Teks dari field pencarian.
     * @return true jika cocok, false jika tidak.
     */
    private boolean barangMatchesFilter(Barang barang, String filterText) {
        String lowerCaseFilter = filterText.toLowerCase();

        // Cek nama barang
        if (barang.getNamaBarang() != null && barang.getNamaBarang().toLowerCase().contains(lowerCaseFilter)) {
            return true;
        }
        
        // Cek nama kategori (dengan pengaman jika kategori null)
        if (barang.getKategori() != null && barang.getKategori().getNamaKategori() != null && barang.getKategori().getNamaKategori().toLowerCase().contains(lowerCaseFilter)) {
            return true; 
        }
        
        // Cek nama supplier (dengan pengaman jika supplier null)
        if (barang.getSupplier() != null && barang.getSupplier().getNamaSupplier() != null && barang.getSupplier().getNamaSupplier().toLowerCase().contains(lowerCaseFilter)) {
            return true; 
        }
        
        // Cek stok
        if (String.valueOf(barang.getStok()).contains(lowerCaseFilter)) {
            return true;
        }

        return false; // Tidak ada yang cocok
    }

    @FXML
    private void handleSimpanButton(ActionEvent event) {
        try {
            if (fieldNama.getText().isEmpty() || comboKategori.getValue() == null) {
                tampilkanAlert(Alert.AlertType.ERROR, "Error", "Nama dan Kategori wajib diisi.");
                return;
            }
            Barang b = (barangTerpilih == null) ? new Barang(0, "", "", 0, 0, null, null) : barangTerpilih;
            b.setNamaBarang(fieldNama.getText());
            b.setStok(Integer.parseInt(fieldStok.getText()));
            b.setHargaJual(Double.parseDouble(fieldHarga.getText()));
            b.setKategori(comboKategori.getValue());
            b.setSupplier(comboSupplier.getValue());
            b.setDeskripsi(fieldDeskripsi.getText());

            if (barangTerpilih == null) {
                barangDAO.addBarang(b);
                tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Barang baru berhasil disimpan.");
            } else {
                barangDAO.updateBarang(b);
                tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Data barang berhasil diperbarui.");
            }
            muatDataTabelBarang();
            bersihkanForm();
        } catch (NumberFormatException e) {
            tampilkanAlert(Alert.AlertType.ERROR, "Input Tidak Valid", "Stok dan Harga harus berupa angka.");
        }
    }
    
    @FXML
    private void handleHapusButton(ActionEvent event) {
        if (barangTerpilih == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Yakin ingin menghapus barang '" + barangTerpilih.getNamaBarang() + "'?", ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle("Konfirmasi Hapus");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (barangDAO.deleteBarang(barangTerpilih.getBarangId())) {
                tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Barang berhasil dihapus.");
                muatDataTabelBarang();
                bersihkanForm();
            }
        }
    }

    @FXML
    private void handleBersihButton(ActionEvent event) {
        bersihkanForm();
    }

    private void muatDataTabelBarang() {
        masterDataBarang.setAll(barangDAO.getAllBarang());
        tabelBarang.setPlaceholder(new Label(masterDataBarang.isEmpty() ? "Belum ada data barang." : "Barang tidak ditemukan."));
    }
    
    private void muatDataKategori() {
        comboKategori.setItems(kategoriDAO.getAllKategori());
    }

    private void muatDataSupplier() {
        comboSupplier.setItems(supplierDAO.getAllSuppliers());
    }

    private void tampilkanDetailBarang(Barang barang) {
        barangTerpilih = barang;
        if (barang != null) {
            fieldNama.setText(barang.getNamaBarang());
            fieldStok.setText(String.valueOf(barang.getStok()));
            fieldHarga.setText(String.valueOf(barang.getHargaJual()));
            fieldDeskripsi.setText(barang.getDeskripsi());
            comboKategori.setValue(barang.getKategori());
            comboSupplier.setValue(barang.getSupplier());
            btnHapus.setDisable(false);
        } else {
             bersihkanForm();
        }
    }

    private void bersihkanForm() {
        tabelBarang.getSelectionModel().clearSelection();
        barangTerpilih = null;
        fieldNama.clear();
        fieldStok.clear();
        fieldHarga.clear();
        fieldDeskripsi.clear();
        comboKategori.setValue(null);
        comboSupplier.setValue(null);
        btnHapus.setDisable(true);
    }

    private void initJam() {
        Timeline jam = new Timeline(new KeyFrame(Duration.ZERO, e -> 
            labelJam.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy  |  HH:mm:ss", new Locale("id", "ID"))))
        ), new KeyFrame(Duration.seconds(1)));
        jam.setCycleCount(Animation.INDEFINITE);
        jam.play();
    }
    
    @FXML
    private void handleRefreshButton(ActionEvent event) {
        muatDataTabelBarang();
        muatDataKategori();
        muatDataSupplier();
        tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Data berhasil diperbarui dari database.");
    }
    
    @FXML
    private void handleLogoutButton(ActionEvent event) {
        try {
            Stage currentStage = (Stage) btnLogout.getScene().getWindow();
            currentStage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Stage loginStage = new Stage();
            loginStage.setTitle("Login - Sistem Inventaris Toko ATK");
            loginStage.setScene(new Scene(loader.load()));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleManageSupplierButton(ActionEvent event) {
        try {
            Supplier supplierSebelumnya = comboSupplier.getValue();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ManageSupplier.fxml"));
            Parent root = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Manajemen Supplier");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner((Stage) btnManageSupplier.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            
            dialogStage.showAndWait();
            
            muatDataSupplier();
            muatDataTabelBarang();
            
            comboSupplier.setValue(supplierSebelumnya);

        } catch (IOException e) {
            e.printStackTrace();
            tampilkanAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka form manajemen supplier.");
        }
    }
    
    @FXML
    private void handleCetakLaporan(ActionEvent event) {
        DateTimeFormatter filenameFormatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
        String timestamp = LocalDateTime.now().format(filenameFormatter);
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan Inventaris (PDF)");
        fileChooser.setInitialFileName("LaporanInventarisATK_" + timestamp + ".pdf");
        
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));

        Stage stage = (Stage) tabelBarang.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            Document document = new Document();
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                
                PdfWriter.getInstance(document, outputStream);
                document.open();

                Paragraph title = new Paragraph("Laporan Data Inventaris Toko ATK");
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20f);
                document.add(title);
                
                PdfPTable table = new PdfPTable(8);
                table.setWidthPercentage(100);
                
                table.addCell("Nama Barang");
                table.addCell("Kategori");
                table.addCell("Supplier");
                table.addCell("Stok");
                table.addCell("Harga Jual");
                table.addCell("Deskripsi");
                table.addCell("No. Telp Supplier");
                table.addCell("Alamat Supplier");

                for (Barang barang : tabelBarang.getItems()) {
                    table.addCell(barang.getNamaBarang());
                    table.addCell(barang.getKategori() != null ? barang.getKategori().getNamaKategori() : "-");
                    table.addCell(barang.getSupplier() != null ? barang.getSupplier().getNamaSupplier() : "-");
                    table.addCell(String.valueOf(barang.getStok()));
                    table.addCell(String.format(Locale.forLanguageTag("id-ID"), "Rp%,.0f", barang.getHargaJual()));
                    table.addCell(barang.getDeskripsi());
                    table.addCell(barang.getSupplier() != null ? barang.getSupplier().getNomorTelepon() : "-");
                    table.addCell(barang.getSupplier() != null ? barang.getSupplier().getAlamat() : "-");
                }

                document.add(table);
                document.close();
                
                tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Laporan PDF berhasil dibuat di: " + file.getAbsolutePath());

            } catch (Exception e) {
                tampilkanAlert(Alert.AlertType.ERROR, "Error Cetak PDF", "Gagal membuat laporan: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleExportExcel(ActionEvent event) {
        DateTimeFormatter filenameFormatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
        String timestamp = LocalDateTime.now().format(filenameFormatter);
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Data Barang ke Excel");
        fileChooser.setInitialFileName("DataInventarisATK_" + timestamp + ".xlsx");
        
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx"));

        Stage stage = (Stage) tabelBarang.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(file)) {

                Sheet sheet = workbook.createSheet("Inventaris Barang");

                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Nama Barang");
                header.createCell(1).setCellValue("Kategori");
                header.createCell(2).setCellValue("Supplier");
                header.createCell(3).setCellValue("Stok");
                header.createCell(4).setCellValue("Harga Jual");
                header.createCell(5).setCellValue("Deskripsi");
                header.createCell(6).setCellValue("No. Telp Supplier");
                header.createCell(7).setCellValue("Alamat Supplier");

                int rowNum = 1;
                for (Barang barang : tabelBarang.getItems()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(barang.getNamaBarang());
                    row.createCell(1).setCellValue(barang.getKategori() != null ? barang.getKategori().getNamaKategori() : "-");
                    row.createCell(2).setCellValue(barang.getSupplier() != null ? barang.getSupplier().getNamaSupplier() : "-");
                    row.createCell(3).setCellValue(barang.getStok());
                    row.createCell(4).setCellValue(barang.getHargaJual());
                    row.createCell(5).setCellValue(barang.getDeskripsi());
                    row.createCell(6).setCellValue(barang.getSupplier() != null ? barang.getSupplier().getNomorTelepon() : "-");
                    row.createCell(7).setCellValue(barang.getSupplier() != null ? barang.getSupplier().getAlamat() : "-");
                }
                
                for(int i = 0; i < 8; i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(outputStream);
                tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Data berhasil diekspor ke: " + file.getAbsolutePath());

            } catch (Exception e) {
                tampilkanAlert(Alert.AlertType.ERROR, "Error Export", "Gagal mengekspor data: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void tampilkanAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        try {
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("iconzoro.jpeg")));
        } catch (Exception e) { /* abaikan */ }
        alert.showAndWait();
    }
}