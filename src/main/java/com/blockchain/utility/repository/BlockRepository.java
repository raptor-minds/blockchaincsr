package com.blockchain.utility.repository;

import com.blockchain.utility.model.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    
    Optional<Block> findByBlockHash(String blockHash);
    
    Optional<Block> findByBlockNumber(Long blockNumber);
    
    List<Block> findByBlockNumberBetween(Long startBlock, Long endBlock);
    
    List<Block> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT b FROM Block b WHERE b.blockNumber >= :startBlock AND b.blockNumber <= :endBlock ORDER BY b.blockNumber DESC")
    List<Block> findBlocksInRangeOrdered(@Param("startBlock") Long startBlock, @Param("endBlock") Long endBlock);
    
    @Query("SELECT MAX(b.blockNumber) FROM Block b")
    Optional<Long> findMaxBlockNumber();
    
    @Query("SELECT COUNT(b) FROM Block b WHERE b.timestamp >= :startTime AND b.timestamp <= :endTime")
    Long countBlocksInTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    boolean existsByBlockHash(String blockHash);
    
    boolean existsByBlockNumber(Long blockNumber);
} 