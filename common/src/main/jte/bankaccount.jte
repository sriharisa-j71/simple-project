@param com.example.common.models.BankAccount bankAccount
{
  "accountNumber": "${bankAccount.accountNumber()}",
  "balance": ${bankAccount.balance()},
  "accountType": "${bankAccount.accountType()}",
  "accountHolder": {
    "name": "${bankAccount.accountHolder().name()}",
    "email": "${bankAccount.accountHolder().email()}",
    "age": ${bankAccount.accountHolder().age()}
  }
}
