package com.example.teamproject.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class Store {
    @Id
    @GeneratedValue
    Long id;

    String name;

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE)
    List<Board> boards = new ArrayList<>();

}
