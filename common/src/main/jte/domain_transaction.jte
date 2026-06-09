@param com.example.common.models.Transaction transaction
{
  "id": "${transaction.id()}",
  "amount": ${transaction.amount()},
  "description": "${transaction.description()}",
  "timestamp": "${transaction.timestamp()}",
  "merchant": "${transaction.merchant()}",
  "account": {
    "accountNumber": "${transaction.account().accountNumber()}",
    "balance": ${transaction.account().balance()},
    "accountType": "${transaction.account().accountType()}",
    "accountHolder": {
      "name": "${transaction.account().accountHolder().name()}",
      "email": "${transaction.account().accountHolder().email()}",
      "age": ${transaction.account().accountHolder().age()}
    }
  }
}
