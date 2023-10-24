package com.example.teamproject.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Store {
    @Id
    @GeneratedValue
    Long id;

    String storeName;

    String storePicture;

    String storeCatchPhrase;
}
