import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class FakturPenjualan extends Penjualan {

    public FakturPenjualan() {
        super();
    }

    public void tampilkanFaktur(String namaKasir) {
        SimpleDateFormat formatTanggal = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        System.out.println("\n+----------------------------------------------------+");
        System.out.println("Selamat Datang di Dewansyah Dealer");
        System.out.println("Tanggal dan Waktu : " + formatTanggal.format(new Date()));
        System.out.println("+----------------------------------------------------+");
        System.out.println("No Faktur    : " + noFaktur); 
        System.out.println("Kode Barang  : " + kodeBarang); 
        System.out.println("Nama Barang  : " + namaBarang); 
        System.out.println("Harga Jual   : " + formatRupiah.format(hargaJual));
        System.out.println("Jumlah Beli  : " + jumlahBeli);
        System.out.println("Total Harga  : " + formatRupiah.format(hitungTotal()));
        System.out.println("+----------------------------------------------------+");
        System.out.println("Kasir : " + namaKasir); 
        System.out.println("+----------------------------------------------------+");
    }
}
