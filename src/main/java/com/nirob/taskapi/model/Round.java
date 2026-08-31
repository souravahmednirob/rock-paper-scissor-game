package com.nirob.taskapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "round")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    @JsonIgnore
    private Game game;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Move playerMove;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Move computerMove;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Result result;

    @Column(nullable = false)
    private LocalDateTime playedAt;
}