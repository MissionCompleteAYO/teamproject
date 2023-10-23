package com.example.teamproject.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.OneToMany;
import javax.persistence.PrePersist;

import lombok.Data;

@Entity
@Data
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    Integer costEffectiveness;
    Integer quality;
    Integer service;
    Integer unique;
    Integer waitingTime;
    String content;
    String userId;
    private Date registrationDateBoard;

    @PrePersist
    protected void onCreate() {
        registrationDateBoard = new Date();
    }
    
    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    List<FileAttach> fileAttachs = new ArrayList<>();
}
    
