class Penjualan {
    protected String noFaktur; 
    protected String kodeBarang; 
    protected String namaBarang;
    protected int hargaJual; 
    protected int jumlahBeli;

    public Penjualan() {
        this.noFaktur = ""; 
        this.kodeBarang = ""; 
        this.namaBarang = ""; 
        this.hargaJual = 0;
        this.jumlahBeli = 0;
    }

    public int hitungTotal() {
        return hargaJual * jumlahBeli;
    }
} 
