/*
 * Copyright (C) 2016 Bradley Campbell.
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

package nz.bradcampbell.benchmarkdemo.model.serializable;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public final class User implements Serializable {
  private final String id;
  private final int index;
  private final String guid;
  @SerializedName("is_active") private final boolean isActive;
  private final String balance;
  @SerializedName("picture") private final String pictureUrl;
  private final int age;
  private final Name name;
  private final String company;
  private final String email;
  private final String address;
  private final String about;
  private final String registered;
  private final double latitude;
  private final double longitude;
  private final List<String> tags;
  private final List<Integer> range;
  private final List<Friend> friends;
  private final List<Image> images;
  private final String greeting;
  @SerializedName("favorite_fruit") private final String favoriteFruit;
  @SerializedName("eye_color") private final String eyeColor;
  private final String phone;

  public User(
      String id,
      int index,
      String guid,
      boolean isActive,
      String balance,
      String pictureUrl,
      int age,
      Name name,
      String company,
      String email,
      String address,
      String about,
      String registered,
      double latitude,
      double longitude,
      List<String> tags,
      List<Integer> range,
      List<Friend> friends,
      List<Image> images,
      String greeting,
      String favoriteFruit,
      String eyeColor,
      String phone) {
    this.id = id;
    this.index = index;
    this.guid = guid;
    this.isActive = isActive;
    this.balance = balance;
    this.pictureUrl = pictureUrl;
    this.age = age;
    this.name = name;
    this.company = company;
    this.email = email;
    this.address = address;
    this.about = about;
    this.registered = registered;
    this.latitude = latitude;
    this.longitude = longitude;
    this.tags = tags;
    this.range = range;
    this.friends = friends;
    this.images = images;
    this.greeting = greeting;
    this.favoriteFruit = favoriteFruit;
    this.eyeColor = eyeColor;
    this.phone = phone;
  }

  public String getId() {
    return id;
  }

  public int getIndex() {
    return index;
  }

  public String getGuid() {
    return guid;
  }

  public boolean isActive() {
    return isActive;
  }

  public String getBalance() {
    return balance;
  }

  public String getPictureUrl() {
    return pictureUrl;
  }

  public int getAge() {
    return age;
  }

  public Name getName() {
    return name;
  }

  public String getCompany() {
    return company;
  }

  public String getEmail() {
    return email;
  }

  public String getAddress() {
    return address;
  }

  public String getAbout() {
    return about;
  }

  public String getRegistered() {
    return registered;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public List<String> getTags() {
    return tags;
  }

  public List<Integer> getRange() {
    return range;
  }

  public List<Friend> getFriends() {
    return friends;
  }

  public List<Image> getImages() {
    return images;
  }

  public String getGreeting() {
    return greeting;
  }

  public String getFavoriteFruit() {
    return favoriteFruit;
  }

  public String getEyeColor() {
    return eyeColor;
  }

  public String getPhone() {
    return phone;
  }
}
