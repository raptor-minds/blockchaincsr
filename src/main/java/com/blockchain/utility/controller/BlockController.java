package com.blockchain.utility.controller;

import com.blockchain.utility.model.entity.Block;
import com.blockchain.utility.service.BlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 区块控制器
 * @author zhangrucheng on 2025/1/15
 */
@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
@Tag(name = "Block", description = "Block management APIs")
public class BlockController {

    private final BlockService blockService;

    @Operation(summary = "Get latest block", description = "Retrieves the latest generated block")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Latest block found"),
        @ApiResponse(responseCode = "404", description = "No blocks found")
    })
    @GetMapping("/latest")
    public ResponseEntity<Block> getLatestBlock() {
        Block latestBlock = blockService.getLatestBlock();
        if (latestBlock == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(latestBlock);
    }

    @Operation(summary = "Get block by number", description = "Retrieves a specific block by its block number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Block found"),
        @ApiResponse(responseCode = "404", description = "Block not found")
    })
    @GetMapping("/{blockNumber}")
    public ResponseEntity<Block> getBlockByNumber(@PathVariable Long blockNumber) {
        Block block = blockService.getBlockByNumber(blockNumber);
        if (block == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(block);
    }

    @Operation(summary = "Get all blocks", description = "Retrieves all blocks in the blockchain")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Blocks retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<Block>> getAllBlocks() {
        List<Block> blocks = blockService.getAllBlocks();
        return ResponseEntity.ok(blocks);
    }

    @Operation(summary = "Manually trigger block generation", description = "Manually triggers the block generation process")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Block generation triggered successfully"),
        @ApiResponse(responseCode = "400", description = "No active transactions to block")
    })
    @PostMapping("/generate")
    public ResponseEntity<String> triggerBlockGeneration() {
        try {
            blockService.checkAndGenerateBlock();
            return ResponseEntity.ok("Block generation process completed");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Block generation failed: " + e.getMessage());
        }
    }
} 