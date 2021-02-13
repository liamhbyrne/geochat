package com.example.geochat_hack;

/**
        * Copyright Google Inc. All Rights Reserved.
        *
        * Licensed under the Apache License, Version 2.0 (the "License");
        * you may not use this file except in compliance with the License.
        * You may obtain a copy of the License at
        *
        * http://www.apache.org/licenses/LICENSE-2.0
        *
        * Unless required by applicable law or agreed to in writing, software
        * distributed under the License is distributed on an "AS IS" BASIS,
        * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        * See the License for the specific language governing permissions and
        * limitations under the License.
        */

public class FriendlyMessage {

    private String text;
    private String name;
    private String photoUrl;
    private String imageUrl;
    private String hours;
    private String minutes;
    private String locality;

    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String name, String photoUrl, String imageUrl, String hours, String minutes, String locality) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;
        this.hours = hours;
        this.minutes = minutes;
        this.locality = locality;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String seconds) {
        this.minutes = seconds;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }
}
