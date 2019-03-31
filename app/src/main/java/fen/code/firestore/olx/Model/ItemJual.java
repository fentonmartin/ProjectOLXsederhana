package fen.code.firestore.olx.Model;

public class ItemJual {

    public String id, imageUpload, txtTitle, txtDeskripsi, txtHarga, imageProfile, nameProfile;

    public ItemJual(String id, String imageUpload, String txtTitle, String txtDeskripsi, String txtHarga, String imageProfile, String nameProfile) {
        this.id = id;
        this.imageUpload = imageUpload;
        this.txtTitle = txtTitle;
        this.txtDeskripsi = txtDeskripsi;
        this.txtHarga = txtHarga;
        this.imageProfile = imageProfile;
        this.nameProfile = nameProfile;
    }

    public ItemJual() {

    }

    public String getNameProfile() {
        return nameProfile;
    }

    public void setNameProfile(String nameProfile) {
        this.nameProfile = nameProfile;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUpload() {
        return imageUpload;
    }

    public void setImageUpload(String imageUpload) {
        this.imageUpload = imageUpload;
    }

    public String getTxtTitle() {
        return txtTitle;
    }

    public void setTxtTitle(String txtTitle) {
        this.txtTitle = txtTitle;
    }

    public String getTxtDeskripsi() {
        return txtDeskripsi;
    }

    public void setTxtDeskripsi(String txtDeskripsi) {
        this.txtDeskripsi = txtDeskripsi;
    }

    public String getTxtHarga() {
        return txtHarga;
    }

    public void setTxtHarga(String txtHarga) {
        this.txtHarga = txtHarga;
    }
}
