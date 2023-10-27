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
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String catchphrase;
    String streetNameAddress;

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
    List<Board> boards = new ArrayList<>();
}
