package com.tokoatk;

// Import untuk CRUD, DAO, Model
import java.text.NumberFormat;
import java.util.Locale;
import javafx.scene.control.TableCell;
import com.tokoatk.dao.BarangDAO;
import com.tokoatk.dao.KategoriDAO;
import com.tokoatk.model.Barang;
import com.tokoatk.model.Kategori;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

// Import untuk Jam Digital
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

// Import untuk Konfirmasi
import java.util.Optional;
import javafx.scene.control.ButtonType;

// Import untuk Pencarian
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;


public class FXMLDocumentController implements Initializable {

    // --- Injeksi DAO ---
    private final BarangDAO barangDAO = new BarangDAO();
    private final KategoriDAO kategoriDAO = new KategoriDAO();

    // --- FXML Tabel ---
    @FXML private TableView<Barang> tabelBarang;
    @FXML private TableColumn<Barang, String> kolomNama;
    @FXML private TableColumn<Barang, String> kolomKategori;
    @FXML private TableColumn<Barang, Integer> kolomStok;
    @FXML private TableColumn<Barang, Double> kolomHarga;
    @FXML private TableColumn<Barang, String> kolomDeskripsi;

    // --- FXML Form ---
    @FXML private TextField fieldNama;
    @FXML private TextField fieldStok;
    @FXML private TextField fieldHarga;
    @FXML private TextArea fieldDeskripsi;
    @FXML private ComboBox<Kategori> comboKategori;
    @FXML private Button btnSimpan;
    @FXML private Button btnHapus;
    @FXML private Button btnBersih;

    // --- FXML Jam ---
    @FXML private Label labelJam;

    // --- FXML Pencarian ---
    @FXML private TextField fieldCari;

    private Barang barangTerpilih = null; 
    
    // --- Variabel Data ---
    private ObservableList<Barang> masterData = FXCollections.observableArrayList();
    
    // Variabel untuk Pesan Tabel Kosong
    private Label labelDataKosong = new Label("Belum ada data barang di database.");
    private Label labelCariKosong = new Label("Barang yang Anda cari tidak ditemukan.");
    
    
    private void initJam() {
        Locale localeID = new Locale("id", "ID");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "EEEE, dd MMMM yyyy  |  HH:mm:ss", localeID
        );
        Timeline jam = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            labelJam.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        jam.setCycleCount(Animation.INDEFINITE);
        jam.play();
    }
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ... (Setup kolom dan format Rupiah tidak berubah) ...
        kolomNama.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        kolomStok.setCellValueFactory(new PropertyValueFactory<>("stok"));
        kolomHarga.setCellValueFactory(new PropertyValueFactory<>("hargaJual"));
        kolomDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        kolomKategori.setCellValueFactory(cellData -> cellData.getValue().namaKategoriProperty());
        
        Locale localeID = new Locale("id", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        kolomHarga.setCellFactory(column -> {
            return new TableCell<Barang, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatRupiah.format(item));
                    }
                }
            };
        });

        muatDataKategori();
        
        tabelBarang.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> tampilkanDetailBarang(newValue)
        );
        
        btnHapus.setDisable(true);
        initJam();
        setupFilter();
        muatDataTabelBarang();
    }
    
    
    /**
     * ======================================
     * == METODE INI SUDAH DIPERBARUI ==
     * ======================================
     */
    private void setupFilter() {
        FilteredList<Barang> filteredData = new FilteredList<>(masterData, p -> true);

        fieldCari.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(barang -> {
                // Jika fieldCari kosong, tampilkan semua.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // --- INI LOGIKA BARUNYA ---
                
                // 1. Cek Nama Barang
                if (barang.getNamaBarang().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                } 
                // 2. Cek Kategori
                else if (barang.getKategori() != null && 
                           barang.getKategori().getNamaKategori().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                // 3. Cek Deskripsi
                else if (barang.getDeskripsi() != null && 
                           barang.getDeskripsi().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                // 4. Cek Stok (ubah angka jadi string)
                else if (String.valueOf(barang.getStok()).contains(lowerCaseFilter)) {
                    return true;
                }
                
                return false; // Filter tidak cocok
            });
        });

        SortedList<Barang> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tabelBarang.comparatorProperty());
        tabelBarang.setItems(sortedData);
    }
    
    
    private void muatDataTabelBarang() {
        // Ambil data dari DAO
        ObservableList<Barang> listBarang = barangDAO.getAllBarang();
        
        masterData.clear();
        masterData.addAll(listBarang);
        
        // Atur placeholder (pesan "data tidak ditemukan")
        if (masterData.isEmpty()) {
            tabelBarang.setPlaceholder(labelDataKosong);
        } else {
            tabelBarang.setPlaceholder(labelCariKosong);
        }
    }
    

    // --- Sisa Metode (Simpan, Hapus, Bersih, dll) TIDAK BERUBAH ---

    @FXML
    private void handleSimpanButton(ActionEvent event) {
        try {
            String nama = fieldNama.getText();
            int stok = Integer.parseInt(fieldStok.getText());
            double harga = Double.parseDouble(fieldHarga.getText());
            Kategori kategori = comboKategori.getValue();
            String deskripsi = fieldDeskripsi.getText();

            if (nama.isEmpty() || kategori == null) {
                tampilkanAlert(Alert.AlertType.ERROR, "Error", "Nama dan Kategori wajib diisi.");
                return;
            }

            if (barangTerpilih == null) {
                Barang barangBaru = new Barang(0, nama, deskripsi, stok, harga, kategori);
                barangDAO.addBarang(barangBaru);
                tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Barang baru berhasil disimpan.");
            } else {
                barangTerpilih.setNamaBarang(nama);
                barangTerpilih.setStok(stok);
                barangTerpilih.setHargaJual(harga);
                barangTerpilih.setKategori(kategori);
                barangTerpilih.setDeskripsi(deskripsi);
                barangDAO.updateBarang(barangTerpilih);
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
        if (barangTerpilih != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Konfirmasi Hapus");
            alert.setHeaderText(null); 
            alert.setContentText("Apakah Anda yakin ingin menghapus barang '" + barangTerpilih.getNamaBarang() + "'?");

            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if(barangDAO.deleteBarang(barangTerpilih.getBarangId())) {
                    tampilkanAlert(Alert.AlertType.INFORMATION, "Sukses", "Barang berhasil dihapus.");
                    muatDataTabelBarang(); 
                    bersihkanForm();
                }
            }
        } else {
            tampilkanAlert(Alert.AlertType.WARNING, "Tidak Ada Pilihan", "Pilih barang yang ingin dihapus.");
        }
    }

    @FXML
    private void handleBersihButton(ActionEvent event) {
        bersihkanForm();
    }

    private void muatDataKategori() {
        comboKategori.setItems(kategoriDAO.getAllKategori());
    }

    private void tampilkanDetailBarang(Barang barang) {
        if (barang != null) {
            barangTerpilih = barang;
            fieldNama.setText(barang.getNamaBarang());
            fieldStok.setText(String.valueOf(barang.getStok()));
            fieldHarga.setText(String.valueOf(barang.getHargaJual()));
            fieldDeskripsi.setText(barang.getDeskripsi());
            comboKategori.setValue(barang.getKategori()); 
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
        btnHapus.setDisable(true);
    }

    private void tampilkanAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}