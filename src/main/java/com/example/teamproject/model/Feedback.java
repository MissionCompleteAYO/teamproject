package com.example.teamproject.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Feedback {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String cancelReason;   
    private LocalDateTime cancelDate;   
}
