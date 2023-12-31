package com.example.teamproject.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Visits {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long numberOfVisitors;
}
