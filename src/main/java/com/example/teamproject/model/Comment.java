package com.example.teamproject.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class Comment {
    @Id
    @GeneratedValue
    Long id;

    String comment;

    Integer likeUnlike;

    @ManyToOne
    Board board;

    @ManyToOne
    Store store;

    LocalDateTime writeDateTime;

}
