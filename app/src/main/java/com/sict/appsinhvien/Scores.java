package com.sict.appsinhvien;

public class Scores {

    private String idCode;
    private Double scores;
    private String tenMonHoc;

    public Scores() {
    }

    public Scores(String idCode, Double scores, String tenMonHoc) {
        this.idCode = idCode;
        this.scores = scores;
        this.tenMonHoc = tenMonHoc;
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode;
    }

    public Double getScores() {
        return scores;
    }

    public void setScores(Double scores) {
        this.scores = scores;
    }

    public String getTenMonHoc() {
        return tenMonHoc;
    }

    public void setTenMonHoc(String tenMonHoc) {
        this.tenMonHoc = tenMonHoc;
    }
}
