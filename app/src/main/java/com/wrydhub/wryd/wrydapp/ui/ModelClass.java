package com.wrydhub.wryd.wrydapp.ui;

public class ModelClass {
    String fruitName,fruitNum;
    int img;
    int personId;
    String imgUrl;

    public String getFruitName() {
        return fruitName;
    }

    public void setFruitName(String fruitName) {
        this.fruitName = fruitName;
    }

    public String getFruitNum() {
        return fruitNum;
    }

    public void setFruitNum(String fruitNum) {
        this.fruitNum = fruitNum;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
