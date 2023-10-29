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
@ToString(exclude = { "board", "user" })
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    private String content;

    private Integer like;

    private Integer unlike;

    @ManyToOne
    Board board;

    @ManyToOne
    User user;

    private LocalDateTime writeDateTime;

}