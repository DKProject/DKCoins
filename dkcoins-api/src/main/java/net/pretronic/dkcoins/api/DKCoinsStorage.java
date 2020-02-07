/*
 * (C) Copyright 2019 The DKCoins Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 18.11.19, 21:21
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkcoins.api;

import net.prematic.libraries.utility.annonations.Nullable;
import net.pretronic.dkcoins.api.account.*;
import net.pretronic.dkcoins.api.account.member.AccountMember;
import net.pretronic.dkcoins.api.account.member.AccountMemberRole;
import net.pretronic.dkcoins.api.account.transaction.AccountTransactionProperty;
import net.pretronic.dkcoins.api.currency.Currency;
import net.pretronic.dkcoins.api.account.transaction.AccountTransaction;
import net.pretronic.dkcoins.api.currency.CurrencyExchangeRate;
import net.pretronic.dkcoins.api.user.DKCoinsUser;

import java.util.Collection;

public interface DKCoinsStorage {

    AccountType getAccountType(int id);

    AccountType createAccountType(String name, String symbol);

    void updateAccountTypeName(int id, String name);

    void updateAccountTypeSymbol(int id, String symbol);

    void deleteAccountType(int id);


    BankAccount getAccount(int id);

    BankAccount getAccountByCredit(int creditId);

    MasterBankAccount getMasterAccount(int id);

    BankAccount getSubAccount(int masterAccountId, int id);

    MasterBankAccount getSubMasterAccount(int masterAccountId, int id);

    BankAccount createAccount(String name, int typeId, boolean disabled, int parentId, int creatorId);

    MasterBankAccount createMasterAccount(String name, int typeId, boolean disabled, int parentId, int creatorId);

    void updateAccountName(int id, String name);

    void updateAccountTypeId(int id, int typeId);

    void updateAccountDisabled(int id, boolean disabled);

    void updateAccountParentId(int id, int parentId);

    void deleteAccount(int id);


    AccountCredit addAccountCredit(int accountId, int currencyId, double amount);

    void setAccountCreditAmount(int id, double amount);

    void deleteAccountCredit(int id);



    AccountLimitation addAccountLimitation(int accountId, @Nullable int memberId, int memberRoleId,
                                           int comparativeCurrencyId, double amount, long interval);

    void deleteAccountLimitation(int id);


    AccountMember getAccountMember(int id);

    AccountMember getAccountMember(int userId, int accountId);

    AccountMember addAccountMember(int accountId, int userId, AccountMemberRole role);

    void deleteAccountMember(int id);


    Collection<AccountTransaction> getAccountTransactions(int senderId, int start, int end);

    AccountTransaction addAccountTransaction(int sourceId, int senderId, int receiverId, double amount,
                                             double exchangeRate, String reason, String cause, long time,
                                             Collection<AccountTransactionProperty> properties);


    Currency getCurrency(int id);

    Currency getCurrency(String name);

    Currency createCurrency(String name, String symbol);

    void updateCurrencyName(int id, String name);

    void updateCurrencySymbol(int id, String symbol);

    void deleteCurrency(int id);



    CurrencyExchangeRate addCurrencyExchangeRate(int selectedCurrencyId, int targetCurrencyId, double exchangeAmount);

    void deleteCurrencyExchangeRate(int id);



    DKCoinsUser getUser(int id);
}