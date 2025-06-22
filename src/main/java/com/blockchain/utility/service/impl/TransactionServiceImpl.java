package com.blockchain.utility.service.impl;

import com.blockchain.utility.model.entity.Transaction;
import com.blockchain.utility.model.vo.TransactionStatus;
import com.blockchain.utility.model.vo.request.CreateTransactionRequest;
import com.blockchain.utility.repository.TransactionRepository;
import com.blockchain.utility.service.TransactionService;
import com.blockchain.utility.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return null;
    }

    @Override
    public List<Object> getAllTransactions() {
        return null;
    }

    @Transactional
    @Override
    public Object addTransaction(CreateTransactionRequest createTransactionRequest) {

        if (transactionRepository.existsByUuid(createTransactionRequest.getUuid().toString())) {
            throw new RuntimeException("tx already exists");
        }

        String txHash = calculateHash(createTransactionRequest.getSender(), createTransactionRequest.getUuid());

        Transaction transaction = new Transaction();
        transaction.setFromAddress(createTransactionRequest.getSender());
        transaction.setToAddress(createTransactionRequest.getRecipient());
        transaction.setCreatedAt(createTransactionRequest.getCreatedTime().atStartOfDay());
        transaction.setStatus(TransactionStatus.DE_ACTIVE.getStatus());
        transaction.setTxHash(txHash);
        transaction.setUuid(createTransactionRequest.getUuid().toString());

        transactionRepository.save(transaction);
        return transaction.getId();
    }

    private String calculateHash(String sender, UUID uuid) {
        return StringUtil.applySha256(sender + uuid);
    }
}
