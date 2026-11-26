public class PasienHewan {
    private int id;
    private String namaHewan;
    private String jenisHewan;
    private String keluhan;

    public PasienHewan(String namaHewan, String jenisHewan, String keluhan) {
        this(0, namaHewan, jenisHewan, keluhan);
    }

    public PasienHewan(int id, String namaHewan, String jenisHewan, String keluhan) {
        this.id = id;
        this.namaHewan = namaHewan;
        this.jenisHewan = jenisHewan;
        this.keluhan = keluhan;
    }

    public int getId() {
        return id;
    }

    public String getNamaHewan() {
        return namaHewan;
    }

    public String getJenisHewan() {
        return jenisHewan;
    }

    public String getKeluhan() {
        return keluhan;
    }
}
