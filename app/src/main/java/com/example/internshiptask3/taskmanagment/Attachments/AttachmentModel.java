package com.example.internshiptask3.taskmanagment.Attachments;

import android.net.Uri;

public class AttachmentModel {

    String ImageUrl;

    public  AttachmentModel(){}

    public AttachmentModel(String ImageUrl) {
        this.ImageUrl = ImageUrl;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String ImageUrl) {
        this.ImageUrl = ImageUrl;
    }
}
