package com.yourbank.banking_api.service;

import com.yourbank.banking_api.model.Branch;
import com.yourbank.banking_api.model.Transaction;
import com.yourbank.banking_api.model.TransactionType;
import com.yourbank.banking_api.repository.BranchRepository;
import com.yourbank.banking_api.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailySummaryService {

    private final TransactionRepository transactionRepository;
    private final BranchRepository branchRepository;
    private final JavaMailSender mailSender;

    @Scheduled(initialDelay = 60000, fixedDelay = Long.MAX_VALUE) //   @Scheduled(cron = "0 0 0 * * ?") Runs at midnight every day
    public void sendDailyTransactionSummary() {
        LocalDate today = LocalDate.now(); // Use today's date  // yesterdays data  LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Branch> branches = branchRepository.findAll();

        StringBuilder emailContent = new StringBuilder();

        for (Branch branch : branches) {
            List<Transaction> transactions = transactionRepository
                    .findByAccount_BranchAndCreatedAtBetween(
                            branch,
                            today.atStartOfDay(),                                  //instead of today put yesterday
                            today.atTime(23, 59, 59)            //same here
                    );

            if (!transactions.isEmpty()) {
                emailContent.append(generateBranchSummary(branch, transactions, today))
                        .append("\n\n------------------------------------\n\n");
            }
        }

        // Sending email if there is any content
        if (!emailContent.isEmpty()) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("shishutiwari2198@gmail.com");
            message.setSubject("Daily Transaction Summary - " + today);
            message.setText(emailContent.toString());

            mailSender.send(message);
        }
    }

    private String generateBranchSummary(Branch branch, List<Transaction> transactions, LocalDate date) {
        BigDecimal totalDeposits = transactions.stream()
                .filter(t -> t.getType() == TransactionType.DEPOSIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWithdrawals = transactions.stream()
                .filter(t -> t.getType() == TransactionType.WITHDRAWAL)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String transactionDetails = transactions.stream()
                .map(t -> String.format("%s: %s %s - %s",
                        t.getCreatedAt().toLocalTime(),
                        t.getType(),
                        t.getAmount(),
                        t.getDescription()))
                .collect(Collectors.joining("\n"));

        return String.format(
                "Branch: %s\nDate: %s\nTotal Transactions: %d\nTotal Deposits: %s\nTotal Withdrawals: %s\n\nTransactions:\n%s",
                branch.getName(),
                date,
                transactions.size(),
                totalDeposits,
                totalWithdrawals,
                transactionDetails
        );
    }


    private void sendBranchSummaryEmail(Branch branch, List<Transaction> transactions, LocalDate date) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("shishutiwari2198@gmail.com");
        message.setSubject(String.format("Daily Transaction Summary for %s - %s", branch.getName(), date));

        String summary = transactions.stream()
                .map(t -> String.format("%s: %s %s - %s",
                        t.getCreatedAt().toLocalTime(),
                        t.getType(),
                        t.getAmount(),
                        t.getDescription()))
                .collect(Collectors.joining("\n"));

        BigDecimal totalDeposits = transactions.stream()
                .filter(t -> t.getType() == TransactionType.DEPOSIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWithdrawals = transactions.stream()
                .filter(t -> t.getType() == TransactionType.WITHDRAWAL)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        message.setText(String.format(
                "Branch: %s\nDate: %s\n\nTotal Transactions: %d\nTotal Deposits: %s\nTotal Withdrawals: %s\n\nTransaction Details:\n%s",
                branch.getName(),
                date,
                transactions.size(),
                totalDeposits,
                totalWithdrawals,
                summary));

        mailSender.send(message);
    }
}
