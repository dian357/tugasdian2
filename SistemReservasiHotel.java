import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SistemReservasiHotel {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        ReservasiService reservasiService = new ReservasiServiceImpl(scanner);
        reservasiService.jalankanSistemReservasi();
    }
}

interface ReservasiService {
    void jalankanSistemReservasi();
    void viewReservations();
}

class ReservasiServiceImpl implements ReservasiService {
    private final Scanner scanner;
    private List<Reservasi> daftarReservasi = new ArrayList<>();

    public ReservasiServiceImpl(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public void jalankanSistemReservasi() {
        OUTER:
        while (true) {
            System.out.println("\n=== SISTEM RESERVASI HOTEL ===");
            System.out.println("1. Buat Reservasi Baru");
            System.out.println("2. Lihat Daftar Reservasi");
            System.out.println("3. Keluar");
            System.out.print("Pilih menu (1-3): ");
            int pilihan = scanner.nextInt();
            scanner.nextLine();
            switch (pilihan) {
                case 1 -> buatReservasi(scanner);
                case 2 -> viewReservations();
                case 3 -> {
                    System.out.println("Terima kasih telah menggunakan sistem reservasi hotel.");
                    break OUTER;
                }
                default -> System.out.println("Pilihan tidak valid!");
            }
        }
    }

    @Override
    public void viewReservations() {
        System.out.println("\n=== DAFTAR RESERVASI ===");
        if (daftarReservasi.isEmpty()) {
            System.out.println("Tidak ada reservasi yang tersedia.");
        } else {
            for (Reservasi reservasi : daftarReservasi) {
                reservasi.tampilkanDetailReservasi();
                System.out.println();
            }
        }
    }

    private void buatReservasi(Scanner scanner) {
        // Input data tamu
        System.out.println("\n=== INPUT DATA TAMU ===");
        System.out.print("ID Tamu: ");
        String idTamu = scanner.nextLine();
        System.out.print("Nama: ");
        String nama = scanner.nextLine();
        System.out.print("No. Telepon: ");
        String noTelp = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        Tamu tamu = new Tamu(idTamu, nama, noTelp, email);

        // Pilih tipe kamar
        System.out.println("\n=== PILIH TIPE KAMAR ===");
        System.out.println("1. Standard Room (Rp 500.000/malam)");
        System.out.println("2. Suite Room (Rp 1.500.000/malam)");
        System.out.print("Pilih tipe kamar (1-2): ");
        int tipeKamar = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Kamar kamar;
        if (tipeKamar == 1) {
            kamar = new KamarStandard(101, "Standard Single", 500000);
        } else {
            kamar = new KamarSuite(201, "Executive Suite", 1500000, "Jacuzzi, Mini Bar");
        }

        // Input tanggal check-in dan check-out
        System.out.println("\n=== INPUT TANGGAL ===");
        System.out.print("Tanggal Check-in (YYYY-MM-DD): ");
        String tanggalCheckin = scanner.nextLine();
        System.out.print("Tanggal Check-out (YYYY-MM-DD): ");
        String tanggalCheckout = scanner.nextLine();

        // Buat reservasi
        String idReservasi = "R" + System.currentTimeMillis();
        Reservasi reservasi = new Reservasi(idReservasi, tamu, kamar, tanggalCheckin, tanggalCheckout);
        daftarReservasi.add(reservasi);

        // Tampilkan detail reservasi
        reservasi.tampilkanDetailReservasi();

        // Proses pembayaran
        System.out.println("\n=== PROSES PEMBAYARAN ===");
        System.out.println("Total biaya: Rp " + reservasi.hitungTotalBiaya());
        System.out.println("Pilih metode pembayaran:");
        System.out.println("1. Transfer Bank");
        System.out.println("2. Kartu Kredit");
        System.out.print("Pilihan (1-2): ");
        int metodePembayaran = scanner.nextInt();
        scanner.nextLine(); // consume newline

        String metode = metodePembayaran == 1 ? "Transfer Bank" : "Kartu Kredit";
        PembayaranReservasi pembayaran = new PembayaranReservasi("P" + System.currentTimeMillis(), 
                                                                reservasi, metode);
        pembayaran.prosesPembayaran();

        System.out.println("\nStatus Pembayaran: " + pembayaran.getStatusPembayaran());
        System.out.println("Reservasi berhasil dikonfirmasi!");
    }
}

interface Kamar {
    int getNomorKamar();
    String getTipeKamar();
    double getHargaPerMalam();
    boolean isStatusTersedia();
    void setStatusTersedia(boolean status);
    String getDeskripsi();
}

class KamarStandard implements Kamar {
    private int nomorKamar;
    private String tipeKamar;
    private final double hargaPerMalam;
    private boolean statusTersedia;

    public KamarStandard(int nomorKamar, String tipeKamar, double hargaPerMalam) {
        this.nomorKamar = nomorKamar;
        this.tipeKamar = tipeKamar;
        this.hargaPerMalam = hargaPerMalam;
        this.statusTersedia = true;
    }

    @Override
    public int getNomorKamar() { return nomorKamar; }
    @Override
    public String getTipeKamar() { return tipeKamar; }
    @Override
    public double getHargaPerMalam() { return hargaPerMalam; }
    @Override
    public boolean isStatusTersedia() { return statusTersedia; }
    @Override
    public void setStatusTersedia(boolean status) { this.statusTersedia = status; }

    @Override
    public String getDeskripsi() {
        return "Kamar " + nomorKamar + " - Tipe: " + tipeKamar + " (Standard Room)";
    }
}

class KamarSuite implements Kamar {
    private int nomorKamar;
    private String tipeKamar;
    private double hargaPerMalam;
    private String fasilitasTambahan;
    private boolean statusTersedia;

    public KamarSuite(int nomorKamar, String tipeKamar, double hargaPerMalam, String fasilitasTambahan) {
        this.nomorKamar = nomorKamar;
        this.tipeKamar = tipeKamar;
        this.hargaPerMalam = hargaPerMalam;
        this.fasilitasTambahan = fasilitasTambahan;
        this.statusTersedia = true;
    }

    @Override
    public int getNomorKamar() { return nomorKamar; }
    @Override
    public String getTipeKamar() { return tipeKamar; }
    @Override
    public double getHargaPerMalam() { return hargaPerMalam; }
    @Override
    public boolean isStatusTersedia() { return statusTersedia; }
    @Override
    public void setStatusTersedia(boolean status) { this.statusTersedia = status; }

    @Override
    public String getDeskripsi() {
        return "Kamar " + nomorKamar + " - Tipe: " + tipeKamar + " (Suite) - Fasilitas: " + fasilitasTambahan;
    }
}

class Tamu {
    private String idTamu;
    private String nama;
    private String noTelp;
    private String email;

    public Tamu(String idTamu, String nama, String noTelp, String email) {
        this.idTamu = idTamu;
        this.nama = nama;
        this.noTelp = noTelp;
        this.email = email;
    }

    public String getIdTamu() { return idTamu; }
    public String getNama() { return nama; }
    public String getNoTelp() { return noTelp; }
    public String getEmail() { return email; }
}

class Reservasi {
    private String idReservasi;
    private Tamu tamu;
    private Kamar kamar;
    private String tanggalCheckin;
    private String tanggalCheckout;
    private String status;

    public Reservasi(String idReservasi, Tamu tamu, Kamar kamar, String tanggalCheckin, String tanggalCheckout) {
        this.idReservasi = idReservasi;
        this.tamu = tamu;
        this.kamar = kamar;
        this.tanggalCheckin = tanggalCheckin;
        this.tanggalCheckout = tanggalCheckout;
        this.status = "Pending";
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double hitungTotalBiaya() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate checkin = LocalDate.parse(tanggalCheckin, formatter);
            LocalDate checkout = LocalDate.parse(tanggalCheckout, formatter);
            long jumlahHari = ChronoUnit.DAYS.between(checkin, checkout);
            return kamar.getHargaPerMalam() * jumlahHari;
        } catch (Exception e) {
            return kamar.getHargaPerMalam(); // default 1 hari jika format tanggal tidak valid
        }
    }

    public void tampilkanDetailReservasi() {
        System.out.println("\n=== Detail Reservasi ===");
        System.out.println("ID Reservasi: " + idReservasi);
        System.out.println("Tamu: " + tamu.getNama());
        System.out.println("Kamar: " + kamar.getDeskripsi());
        System.out.println("Check-in: " + tanggalCheckin);
        System.out.println("Check-out: " + tanggalCheckout);
        System.out.println("Status: " + status);
    }
}

class PembayaranReservasi {
    private String idPembayaran;
    private Reservasi reservasi;
    private double jumlahPembayaran;
    private String metodePembayaran;
    private String statusPembayaran;

    public PembayaranReservasi(String idPembayaran, Reservasi reservasi, String metodePembayaran) {
        this.idPembayaran = idPembayaran;
        this.reservasi = reservasi;
        this.jumlahPembayaran = reservasi.hitungTotalBiaya();
        this.metodePembayaran = metodePembayaran;
        this.statusPembayaran = "Pending";
    }

    public void prosesPembayaran() {
        this.statusPembayaran = "Success";
        this.reservasi.setStatus("Confirmed");
    }

    public String getStatusPembayaran() { return statusPembayaran; }
    public double getJumlahPembayaran() { return jumlahPembayaran; }

    public String getIdPembayaran() {
        return idPembayaran;
    }

    public void setIdPembayaran(String idPembayaran) {
        this.idPembayaran = idPembayaran;
    }

    public Reservasi getReservasi() {
        return reservasi;
    }

    public void setReservasi(Reservasi reservasi) {
        this.reservasi = reservasi;
    }

    public void setJumlahPembayaran(double jumlahPembayaran) {
        this.jumlahPembayaran = jumlahPembayaran;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }

    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }
}