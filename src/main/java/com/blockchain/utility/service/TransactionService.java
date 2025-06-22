package com.blockchain.utility.service;


import com.blockchain.utility.model.vo.request.CreateTransactionRequest;

import java.util.List;

/**
 * The interface Transaction service.
 */
public interface TransactionService {
    /**
     * Gets transaction.
     *
     * @param transactionId the transaction id
     * @return the transaction
     */
    Object getTransaction(String transactionId);

    /**
     * Gets all transactions.
     *
     * @return the all transactions
     */
    List<Object> getAllTransactions();

    /**
     * Create transaction object.
     *
     * @param createTransactionRequest the create transaction request
     * @return the object
     */
    Object addTransaction(CreateTransactionRequest createTransactionRequest);
} 