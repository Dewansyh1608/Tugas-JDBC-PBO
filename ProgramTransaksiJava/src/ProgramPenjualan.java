import java.sql.*;
import java.util.Scanner;

public class ProgramPenjualan {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;
        
        while (isRunning) {
            System.out.println("+------------------------------+");
            System.out.println("|       Menu Utama            |");
            System.out.println("+------------------------------+");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Keluar");
            System.out.print("Pilihan: ");
            
            // Menangani input yang berupa angka
            int pilihan = 0;
            boolean valid = false;
            while (!valid) {
                if (scanner.hasNextInt()) {
                    pilihan = scanner.nextInt();
                    scanner.nextLine();  // Menangani newline setelah nextInt()
                    valid = true;
                } else {
                    System.out.println("Input tidak valid. Harap masukkan angka.");
                    scanner.nextLine();  // Membersihkan buffer
                }
            }

            switch (pilihan) {
                case 1:
                    String username = loginUser(scanner);
                    if (username != null) { // Cek apakah login berhasil
                    menuTransaksi(scanner, username);
                        }
                    break;
                case 2:
                    registerUser(scanner);
                    break;
                case 3:
                    System.out.println("Terima kasih telah menggunakan aplikasi!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    
        scanner.close();
    }

    private static void registerUser(Scanner scanner) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.print("Masukkan Username: ");
            String username = scanner.nextLine();
            System.out.print("Masukkan Password: ");
            String password = scanner.nextLine();
    
            String query = "INSERT INTO users (username, password, created_at) VALUES (?, ?, NOW())";
            var stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
    
            stmt.executeUpdate();
            System.out.println("Registrasi berhasil!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static String loginUser(Scanner scanner) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.print("Masukkan Username: ");
            String username = scanner.nextLine();
            System.out.print("Masukkan Password: ");
            String password = scanner.nextLine();
    
            String query = "SELECT password FROM users WHERE username = ?";
            var stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
    
            var rs = stmt.executeQuery();
            if (rs.next()) {
                String dbPassword = rs.getString("password");
                if (password.equals(dbPassword)) {
                    System.out.println("Login berhasil!");
                    return username; // Mengembalikan username yang berhasil login
                }
            }
            System.out.println("Username atau password salah.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Mengembalikan null jika login gagal
    }
    

    private static void menuTransaksi(Scanner scanner, String username) {
        boolean isTransaksiRunning = true;
    
        while (isTransaksiRunning) {
            System.out.println("+------------------------------+");
            System.out.println("|       Menu Transaksi         |");
            System.out.println("+------------------------------+");
            System.out.println("1. Buat Transaksi");
            System.out.println("2. Lihat Riwayat Transaksi");
            System.out.println("3. Cari Transaksi");
            System.out.println("4. Edit Transaksi");
            System.out.println("5. Hapus Transaksi");
            System.out.println("6. Keluar");
            System.out.print("Pilihan: ");
            
            // Menangani input yang berupa angka
            int pilihan = 0;
            boolean valid = false;
            while (!valid) {
                if (scanner.hasNextInt()) {
                    pilihan = scanner.nextInt();
                    scanner.nextLine();  // Menangani newline setelah nextInt()
                    valid = true;
                } else {
                    System.out.println("Input tidak valid. Harap masukkan angka.");
                    scanner.nextLine();  // Membersihkan buffer
                }
            }
    
            switch (pilihan) {
                case 1:
                    buatTransaksi(scanner, username);
                    break;
                case 2:
                    lihatRiwayatTransaksi(scanner);
                    break;
                case 3:
                    cariTransaksi(scanner);
                    break;
                case 4:
                    editTransaksi(scanner);
                    break;
                case 5:
                    hapusTransaksi(scanner);
                    break;
                case 6:
                    isTransaksiRunning = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    // Fungsi lainnya tidak berubah, tinggal tambahkan scanner di setiap parameter untuk konsistensi
    private static void buatTransaksi(Scanner scanner, String username) { 
        try (Connection conn = DatabaseConnection.getConnection()) {

            FakturPenjualan faktur = new FakturPenjualan();

            // Generate nomor faktur otomatis
            faktur.noFaktur = generateNoFaktur(conn);

            System.out.print("Masukkan Kode Barang: ");
            faktur.kodeBarang = scanner.nextLine();
            System.out.print("Masukkan Nama Barang: ");
            faktur.namaBarang = scanner.nextLine();
            System.out.print("Masukkan Harga Jual: ");
            faktur.hargaJual = scanner.nextInt();
            System.out.print("Masukkan Jumlah Beli: ");
            faktur.jumlahBeli = scanner.nextInt();
            scanner.nextLine(); // Mengabaikan newline

            String query = "INSERT INTO transactions (no_faktur, kode_barang, nama_barang, harga_jual, jumlah_beli, total_harga, nama_kasir, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
            var stmt = conn.prepareStatement(query);
            stmt.setString(1, faktur.noFaktur);
            stmt.setString(2, faktur.kodeBarang);
            stmt.setString(3, faktur.namaBarang);
            stmt.setInt(4, faktur.hargaJual);
            stmt.setInt(5, faktur.jumlahBeli);
            stmt.setInt(6, faktur.hitungTotal());
            stmt.setString(7, username);

            stmt.executeUpdate();
            System.out.println("Transaksi berhasil disimpan.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void lihatRiwayatTransaksi(Scanner scanner) { 
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM transactions";
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(query);

            System.out.println("+----------------------------------------------------+");
            System.out.println("Riwayat Transaksi");
            System.out.println("+----------------------------------------------------+");
            while (rs.next()) {
                System.out.printf("No Faktur: %s, Barang: %s, Total: %d\n",
                        rs.getString("no_faktur"),
                        rs.getString("nama_barang"),
                        rs.getInt("total_harga"));
            }
            System.out.println("+----------------------------------------------------+");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void cariTransaksi(Scanner scanner) { 
        try (Connection conn = DatabaseConnection.getConnection()) {
    
            System.out.println("+------------------------------+");
            System.out.println("|         Cari Transaksi        |");
            System.out.println("+------------------------------+");
            System.out.println("1. Cari berdasarkan No Faktur");
            System.out.println("2. Cari berdasarkan Nama Barang");
            System.out.println("3. Cari berdasarkan Kode Barang");
            System.out.print("Pilihan: ");
            int pilihan = scanner.nextInt();
            scanner.nextLine(); // Mengabaikan newline
    
            String query = "";
            String parameter = "";
    
            switch (pilihan) {
                case 1:
                    System.out.print("Masukkan No Faktur: ");
                    parameter = scanner.nextLine();
                    query = "SELECT * FROM transactions WHERE no_faktur = ?";
                    break;
                case 2:
                    System.out.print("Masukkan Nama Barang: ");
                    parameter = scanner.nextLine();
                    query = "SELECT * FROM transactions WHERE nama_barang LIKE ?";
                    parameter = "%" + parameter + "%"; // Untuk pencarian dengan LIKE
                    break;
                case 3:
                    System.out.print("Masukkan Kode Barang: ");
                    parameter = scanner.nextLine();
                    query = "SELECT * FROM transactions WHERE kode_barang = ?";
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
                    return;
            }
    
            var stmt = conn.prepareStatement(query);
            stmt.setString(1, parameter);
            var rs = stmt.executeQuery();
    
            System.out.println("+------------------------------------------------+");
            System.out.println("|               Hasil Pencarian                 |");
            System.out.println("+------------------------------------------------+");
            boolean adaData = false;
    
            while (rs.next()) {
                adaData = true;
                System.out.println("No Faktur    : " + rs.getString("no_faktur"));
                System.out.println("Kode Barang  : " + rs.getString("kode_barang"));
                System.out.println("Nama Barang  : " + rs.getString("nama_barang"));
                System.out.println("Harga Jual   : " + rs.getInt("harga_jual"));
                System.out.println("Jumlah Beli  : " + rs.getInt("jumlah_beli"));
                System.out.println("Total Harga  : " + rs.getInt("total_harga"));
                System.out.println("+------------------------------------------------+");
            }
    
            if (!adaData) {
                System.out.println("Tidak ada transaksi yang ditemukan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void editTransaksi(Scanner scanner) { 
        try (Connection conn = DatabaseConnection.getConnection()) {

       System.out.print("Masukkan No Faktur yang ingin diubah: ");
       String noFaktur = scanner.nextLine();

       String queryCheck = "SELECT * FROM transactions WHERE no_faktur = ?";
       var stmtCheck = conn.prepareStatement(queryCheck);
       stmtCheck.setString(1, noFaktur);
       var rs = stmtCheck.executeQuery();

       if (!rs.next()) {
           System.out.println("Transaksi dengan No Faktur tersebut tidak ditemukan.");
           return;
       }

       System.out.println("Data saat ini: ");
       System.out.printf("Kode Barang: %s, Nama Barang: %s, Harga: %d, Jumlah: %d\n",
               rs.getString("kode_barang"),
               rs.getString("nama_barang"),
               rs.getInt("harga_jual"),
               rs.getInt("jumlah_beli"));

       // Input data baru
       System.out.print("Masukkan Kode Barang baru: ");
       String kodeBarang = scanner.nextLine();
       System.out.print("Masukkan Nama Barang baru: ");
       String namaBarang = scanner.nextLine();
       System.out.print("Masukkan Harga Jual baru: ");
       int hargaJual = scanner.nextInt();
       System.out.print("Masukkan Jumlah Beli baru: ");
       int jumlahBeli = scanner.nextInt();
       scanner.nextLine(); // Mengabaikan newline

       String queryUpdate = "UPDATE transactions SET kode_barang = ?, nama_barang = ?, harga_jual = ?, jumlah_beli = ?, total_harga = ? WHERE no_faktur = ?";
       var stmtUpdate = conn.prepareStatement(queryUpdate);
       stmtUpdate.setString(1, kodeBarang);
       stmtUpdate.setString(2, namaBarang);
       stmtUpdate.setInt(3, hargaJual);
       stmtUpdate.setInt(4, jumlahBeli);
       stmtUpdate.setInt(5, hargaJual * jumlahBeli);
       stmtUpdate.setString(6, noFaktur);

       stmtUpdate.executeUpdate();
       System.out.println("Transaksi berhasil diupdate.");
   } catch (SQLException e) {
       e.printStackTrace();
   }
    }

    private static void hapusTransaksi(Scanner scanner) { 
        try (Connection conn = DatabaseConnection.getConnection()) {

       System.out.print("Masukkan No Faktur yang ingin dihapus: ");
       String noFaktur = scanner.nextLine();

       String queryCheck = "SELECT * FROM transactions WHERE no_faktur = ?";
       var stmtCheck = conn.prepareStatement(queryCheck);
       stmtCheck.setString(1, noFaktur);
       var rs = stmtCheck.executeQuery();

       if (!rs.next()) {
           System.out.println("Transaksi dengan No Faktur tersebut tidak ditemukan.");
           return;
       }

       System.out.print("Apakah Anda yakin ingin menghapus transaksi ini? (y/n): ");
       String konfirmasi = scanner.nextLine();

       if (konfirmasi.equalsIgnoreCase("y")) {
           String queryDelete = "DELETE FROM transactions WHERE no_faktur = ?";
           var stmtDelete = conn.prepareStatement(queryDelete);
           stmtDelete.setString(1, noFaktur);
           stmtDelete.executeUpdate();

           System.out.println("Transaksi berhasil dihapus.");
       } else {
           System.out.println("Penghapusan dibatalkan.");
       }
   } catch (SQLException e) {
       e.printStackTrace();
   }
    }

    
    private static String generateNoFaktur(Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM transactions";
        var stmt = conn.prepareStatement(query);
        var rs = stmt.executeQuery();
        rs.next();
        int count = rs.getInt("count") + 1;
        return "APD" + String.format("%04d", count);
    }
}
