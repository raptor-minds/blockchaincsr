package com.blockchain.utility.service.impl;

import com.blockchain.utility.model.entity.Transaction;
import com.blockchain.utility.model.vo.TransactionStatus;
import com.blockchain.utility.model.vo.request.CreateTransactionRequest;
import com.blockchain.utility.model.vo.request.UpdateTransactionRequest;
import com.blockchain.utility.repository.TransactionRepository;
import com.blockchain.utility.service.TransactionService;
import com.blockchain.utility.util.StringUtil;
import com.blockchain.utility.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author zhangrucheng on 2025/6/2
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    @Override
    public Object getTransaction(String transactionId) {
        return transactionRepository.findByUuid(transactionId).orElse(null);
    }

    @Override
    public List<Object> getAllTransactions() {
        return null;
    }

    @Override
    public Object addTransaction(CreateTransactionRequest createTransactionRequest) {

        if (transactionRepository.existsByUuid(createTransactionRequest.getUuid().toString())) {
            throw new RuntimeException("tx already exists");
        }

        Transaction transaction = new Transaction();
        transaction.setFromAddress(createTransactionRequest.getSender());
        transaction.setToAddress(createTransactionRequest.getRecipient());
        
        // 如果请求中没有提供createdTime，使用当前UTC+8时间
        if (createTransactionRequest.getCreatedTime() != null) {
            transaction.setCreatedAt(createTransactionRequest.getCreatedTime().atStartOfDay());
        } else {
            transaction.setCreatedAt(TimeUtil.now());
        }

        transaction.setEndorser(createTransactionRequest.getEndorser());

        transaction.setStatus(TransactionStatus.ACTIVE.getStatus());
        String txHash = calculateHash(createTransactionRequest.getSender(), createTransactionRequest.getUuid(),
                createTransactionRequest.getEndorser(), createTransactionRequest.getTransaction().toString());
        transaction.setTxHash(txHash);
        transaction.setUuid(createTransactionRequest.getUuid().toString());

        // 保存交易 - 自动提交
        transactionRepository.save(transaction);
        return transaction.getUuid();
    }

    @Override
    public Object updateTransaction(UpdateTransactionRequest updateTransactionRequest) {
        // Find the transaction by ID
        Optional<Transaction> optionalTransaction =
                transactionRepository.findByUuid(updateTransactionRequest.getTransactionId().toString());
        
        if (optionalTransaction.isEmpty()) {
            throw new RuntimeException("Transaction not found with ID: " + updateTransactionRequest.getTransactionId());
        }
        
        Transaction transaction = optionalTransaction.get();
        
        // Update fields if provided
        if (updateTransactionRequest.getRecipient() != null) {
            transaction.setToAddress(updateTransactionRequest.getRecipient());
        }
        transaction.setEndorser(updateTransactionRequest.getEndorser());

        transaction.setStatus(TransactionStatus.ACTIVE.getStatus());
        String txHash = calculateHash(updateTransactionRequest.getSender(), updateTransactionRequest.getTransactionId(),
                updateTransactionRequest.getEndorser(), updateTransactionRequest.getTransaction().toString());
        transaction.setTxHash(txHash);
        
        // Save the updated transaction - 自动提交
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction updated successfully with ID: {}", savedTransaction.getId());
        
        return savedTransaction;
    }

    private String calculateHash(String sender, UUID uuid, String endorser, String txContent) {
        return StringUtil.applySha256(sender + uuid + endorser + txContent);
    }
}
