package root.example.com.tar_q;

public class UserJamaah {
    public String id_user;
    public String nama;
    public String noNIK;
    public String nohp;
    public String alamat;
    public String tanggallahir;
    public String latitude;
    public String longitude;
    public int level;

    public UserJamaah(String id_user, String nama, String noNIK, String nohp, String alamat, String tanggallahir, String latitude, String longitude, int level) {
        this.id_user = id_user;
        this.nama = nama;
        this.noNIK = noNIK;
        this.nohp = nohp;
        this.alamat = alamat;
        this.tanggallahir = tanggallahir;
        this.latitude = latitude;
        this.longitude = longitude;
        this.level = level;
    }
}
