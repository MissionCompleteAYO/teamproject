package com.example.teamproject.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class FileAttach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String originName;

    String savedName;

    @ManyToOne
    Board board;
}