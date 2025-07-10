package com.blockchain.utility.model.entity;

import lombok.Data;
import jakarta.persistence.*;
import com.blockchain.utility.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tx_hash", length = 64, unique = true)
    private String txHash;

    @Column
    private Long blockId;

    @Column(name = "block_number", nullable = true)
    private Long blockNumber;

    @Column(name = "from_address", length = 42, nullable = false)
    private String fromAddress;

    @Column(name = "to_address", length = 42)
    private String toAddress;

    @Column(name = "endorser")
    private String endorser;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "input_data", columnDefinition = "TEXT")
    private String inputData;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = TimeUtil.now();
    }
} 