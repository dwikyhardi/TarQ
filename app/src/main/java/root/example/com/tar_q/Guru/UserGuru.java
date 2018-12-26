package root.example.com.tar_q.Guru;

public class UserGuru {
    public String id_user;
    public String nama;
    public String nohp;
    public String alamat;
    public String tanggallahir;
    public String lembaga;
    public int level;
    public int saldo;
    public String pratahsin1;
    public String pratahsin2;
    public String pratahsin3;
    public String tahsin1;
    public String tahsin2;
    public String tahsin3;
    public String tahsin4;
    public String bahasaarab;
    public String tahfizh;
    public String latitude;
    public String longitude;
    public String latitudeRumah;
    public String longitudeRumah;
    public String verifikasi;

    public UserGuru(String id_user, String nama, String nohp,
                    String alamat ,String tanggallahir,String lembaga, String pratahsin1,
                    String pratahsin2,  String pratahsin3, String tahsin1,
                    String tahsin2,  String tahsin3,  String tahsin4, String bahasaarab,
                    String tahfizh, String latitude, String longitude, String latitudeRumah, String longitudeRumah) {
        this.id_user = id_user;
        this.nama = nama;
        this.nohp = nohp;
        this.alamat = alamat;
        this.tanggallahir = tanggallahir;
        this.lembaga = lembaga;
        this.level = 3;
        this.saldo = 0;
        this.latitude = latitude;
        this.longitude = longitude;
        this.latitudeRumah = latitudeRumah;
        this.longitudeRumah = longitudeRumah;
        this.pratahsin1 = pratahsin1;
        this.pratahsin2 = pratahsin2;
        this.pratahsin3 = pratahsin3;
        this.tahsin1 = tahsin1;
        this.tahsin2 = tahsin2;
        this.tahsin3 = tahsin3;
        this.tahsin4 = tahsin4;
        this.bahasaarab = bahasaarab;
        this.tahfizh = tahfizh;
        this.verifikasi = "false";
    }
}