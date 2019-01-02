package root.example.com.tar_q.Guru;

public class InputTahfizh {
    public String id_guru,
            id_jamaah,
            nama_guru,
            nama_jamaah,
            tanggal_belajar,
            setoran,
            murajaah,
            hafalanbaru,
            keterangan;
    public int pertemuan_ke;

    public InputTahfizh(
            String id_guru,
            String id_jamaah,
            String nama_guru,
            String nama_jamaah,
            String tanggal_belajar,
            int pertemuan_ke,
            String setoran,
            String murajaah,
            String hafalanbaru,
            String keterangan
    ) {
        this.id_guru = id_guru;
        this.id_jamaah = id_jamaah;
        this.nama_guru = nama_guru;
        this.nama_jamaah = nama_jamaah;
        this.tanggal_belajar = tanggal_belajar;
        this.pertemuan_ke = pertemuan_ke;
        this.setoran = setoran;
        this.murajaah = murajaah;
        this.hafalanbaru = hafalanbaru;
        this.keterangan = keterangan;

    }
}
