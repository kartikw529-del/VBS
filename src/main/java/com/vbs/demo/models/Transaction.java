package com.vbs.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "`transaction`")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private double amount;

    @Column(name = "curr_balance", nullable = false)
    private double currBalance;

    @Column(nullable = false)
    private String description;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime date;


}