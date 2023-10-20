package com.example.teamproject.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    int costEffectiveness;
    int quality;
    int service;
    int creativity;
    int waitingTime;
    String content;
    String userId;
    
    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    List<FileAttach> fileAttachs = new ArrayList<>();
}
