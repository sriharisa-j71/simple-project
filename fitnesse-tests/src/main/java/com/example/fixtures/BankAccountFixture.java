package com.example.fixtures;

import com.example.common.JteTemplateEngine;
import com.example.common.models.BankAccount;
import com.example.common.models.Person;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BankAccountFixture {

    private final JteTemplateEngine jte = new JteTemplateEngine();
    private final ObjectMapper mapper = new ObjectMapper();

    private String accountNumber;
    private String accountHolderJson;
    private double balance;
    private String accountType;

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAccountHolder(String accountHolderJson) {
        this.accountHolderJson = accountHolderJson;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String render() throws Exception {
        var person = mapper.readValue(accountHolderJson, Person.class);
        var account = new BankAccount(accountNumber, person, balance, accountType);
        var json = jte.renderBankAccount(account);
        var tree = mapper.readTree(json);
        return mapper.writeValueAsString(tree);
    }
}
