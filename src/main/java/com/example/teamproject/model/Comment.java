package com.example.teamproject.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString(exclude = "board")
public class Comment {
    @Id
    @GeneratedValue
    Long id;

    String content;

    Integer like;

    Integer unlike;

    @ManyToOne
    Board board;

    @ManyToOne
    Store store;

    LocalDateTime writeDateTime;

}
