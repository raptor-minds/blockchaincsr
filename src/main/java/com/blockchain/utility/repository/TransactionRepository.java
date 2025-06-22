package com.blockchain.utility.repository;

import com.blockchain.utility.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    Optional<Transaction> findByUuid(String uuid);
    
    Optional<Transaction> findByTxHash(String txHash);
    
    List<Transaction> findByBlockId(Long blockId);
    
    List<Transaction> findByBlockNumber(Long blockNumber);
    
    List<Transaction> findByFromAddress(String fromAddress);
    
    List<Transaction> findByToAddress(String toAddress);
    
    List<Transaction> findByFromAddressOrToAddress(String fromAddress, String toAddress);
    
    List<Transaction> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT t FROM Transaction t WHERE t.blockNumber >= :startBlock AND t.blockNumber <= :endBlock ORDER BY t.blockNumber DESC, t.createdAt DESC")
    List<Transaction> findTransactionsInBlockRange(@Param("startBlock") Long startBlock, @Param("endBlock") Long endBlock);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.fromAddress = :address OR t.toAddress = :address")
    Long countTransactionsByAddress(@Param("address") String address);
    
    @Query("SELECT t FROM Transaction t WHERE t.fromAddress = :address OR t.toAddress = :address ORDER BY t.createdAt DESC")
    List<Transaction> findTransactionsByAddressOrdered(@Param("address") String address);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.status = :status AND t.createdAt >= :startTime AND t.createdAt <= :endTime")
    Long countTransactionsByStatusAndTimeRange(@Param("status") String status,
                                             @Param("startTime") LocalDateTime startTime, 
                                             @Param("endTime") LocalDateTime endTime);
    
    boolean existsByUuid(String uuid);
    
    boolean existsByTxHash(String txHash);
} 