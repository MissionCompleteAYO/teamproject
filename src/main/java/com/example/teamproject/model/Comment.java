package com.example.teamproject.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString(exclude = {"board", "user"})
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
    User user;

    LocalDateTime writeDateTime;

}
