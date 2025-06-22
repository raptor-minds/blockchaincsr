package com.blockchain.utility.controller;

import com.blockchain.utility.model.vo.request.CreateTransactionRequest;
import com.blockchain.utility.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction", description = "Transaction management APIs")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Get transaction by ID", description = "Retrieves a specific transaction by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction found"),
        @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @GetMapping("/{transactionId}")
    public ResponseEntity<Object> getTransaction(@PathVariable String transactionId) {
        return ResponseEntity.ok(transactionService.getTransaction(transactionId));
    }

    @GetMapping
    public ResponseEntity<List<Object>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @Operation(summary = "Create new transaction", description = "Creates a new transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid transaction data")
    })
    @PostMapping("/add")
    public ResponseEntity<Object> createTransaction(@RequestBody CreateTransactionRequest transactionRequest) {
        return ResponseEntity.ok(transactionService.addTransaction(transactionRequest));
    }
} 