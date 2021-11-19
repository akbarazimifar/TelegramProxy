package com.hurhco.telegramPro;

public class ProList {

    public String location;
    public String publish;
    public String lipk;
    public String imgc;
    public Integer sortId;

    public ProList(String location, String publish, String lipk, String imgc, String sortId) {
        this.location = this.location;
        this.publish = this.publish;
        this.lipk = this.lipk;
        this.imgc = this.imgc;
        this.sortId = this.sortId;
    }

    public String getlocation() {
        return location;
    }

    public void setlocation(String location) {
        this.location = location;
    }

    public String getpublish() { return publish; }

    public void setpublish(String publish) { this.publish = publish; }

    public String getlipk() {
        return lipk;
    }

    public void setlipk(String lipk) {
        this.lipk = lipk;
    }

    public String getImgc() {
        return imgc;
    }

    public void setImgc( String imgc){
        this.imgc = imgc;
    }

}
