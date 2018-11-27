package root.example.com.tar_q;

public class UserGuru {
    public String id_user;
    public String nama;
    public String nohp;
    public String alamat;
    public String tanggallahir;
    public String lembaga;
    public int level;
    private String latitude;
    private String longitude;
    private String verifikasi;

    public UserGuru(String id_user, String nama, String nohp, String alamat, int level ,String tanggallahir, String latitude, String longitude) {
        this.id_user = id_user;
        this.nama = nama;
        this.nohp = nohp;
        this.alamat = alamat;
        this.tanggallahir = tanggallahir;
        this.level = level;
        this.latitude = latitude;
        this.longitude = longitude;
        this.verifikasi = "false";
    }
}