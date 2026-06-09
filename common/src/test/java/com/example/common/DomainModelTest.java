package com.example.common;

import com.example.common.models.BankAccount;
import com.example.common.models.Person;
import com.example.common.models.Transaction;

import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

public class DomainModelTest {
    public static void main(String[] args) {
        var jte = new JteTemplateEngine(Path.of("common/src/main/jte"));

        var person = new Person("Alice", "alice@example.com", 30);
        var account = new BankAccount("ACC-001", person, 5000.00, "CHECKING");
        var timestamp = Instant.now().toString();
        var transaction = new Transaction("TXN-TEST-001", 250.00,
            "Online payment via nested domain models", timestamp, account, "Amazon");

        System.out.println("=== Person ===");
        System.out.println(jte.renderPerson(person));

        System.out.println("\n=== BankAccount ===");
        System.out.println(jte.renderBankAccount(account));

        System.out.println("\n=== Domain Transaction ===");
        var body = jte.renderDomainTransaction(transaction);
        System.out.println(body);

        System.out.println("\n=== Domain SQS Event ===");
        System.out.println(jte.renderDomainSqsEvent(UUID.randomUUID().toString(), body, timestamp));
    }
}
