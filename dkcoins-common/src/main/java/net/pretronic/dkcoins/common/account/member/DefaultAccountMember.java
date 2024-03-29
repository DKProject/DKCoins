/*
 * (C) Copyright 2019 The DKCoins Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 20.11.19, 15:33
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkcoins.common.account.member;

import net.pretronic.dkcoins.api.account.BankAccount;
import net.pretronic.dkcoins.api.account.limitation.AccountLimitation;
import net.pretronic.dkcoins.api.account.limitation.AccountLimitationCalculationType;
import net.pretronic.dkcoins.api.account.limitation.AccountLimitationInterval;
import net.pretronic.dkcoins.api.account.member.AccountMember;
import net.pretronic.dkcoins.api.account.member.AccountMemberRole;
import net.pretronic.dkcoins.api.currency.Currency;
import net.pretronic.dkcoins.api.user.DKCoinsUser;
import net.pretronic.dkcoins.common.DefaultDKCoins;
import net.pretronic.dkcoins.common.DefaultDKCoinsStorage;
import net.pretronic.dkcoins.common.SyncAction;
import net.pretronic.dkcoins.common.account.DefaultAccountManager;
import net.pretronic.dkcoins.common.account.DefaultBankAccount;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.Collection;

public class DefaultAccountMember implements AccountMember {

    private final int id;
    private final DefaultBankAccount account;
    private final DKCoinsUser user;
    private AccountMemberRole role;
    private boolean receiveNotifications;

    public DefaultAccountMember(int id, DefaultBankAccount account, DKCoinsUser user, AccountMemberRole role, boolean receiveNotifications) {
        this.id = id;
        this.account = account;
        this.user = user;
        this.role = role;
        this.receiveNotifications = receiveNotifications;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public BankAccount getAccount() {
        return this.account;
    }

    @Override
    public DKCoinsUser getUser() {
        return this.user;
    }

    @Override
    public AccountMemberRole getRole() {
        return this.role;
    }

    @Override
    public void setRole(AccountMemberRole role) {
        DefaultDKCoinsStorage storage = DefaultDKCoins.getInstance().getStorage();
        DefaultAccountManager accountManager = DefaultDKCoins.getInstance().getAccountManager();

        storage.getAccountMember().update()
                .set("RoleId", role.getId())
                .where("Id", getId())
                .execute();

        updateRole(role);

        accountManager.getAccountCache().getCaller().updateAndIgnore(getAccount().getId(), Document.newDocument()
                .add("action", SyncAction.ACCOUNT_MEMBER_UPDATE_ROLE)
                .add("memberId", getId())
                .add("roleId", getRole().getId()));
    }

    @Override
    public Collection<AccountLimitation> getLimitations() {
        return Iterators.filter(getAccount().getLimitations(), limitation -> this.equals(limitation.getMember()));
    }

    @Override
    public AccountLimitation getLimitation(Currency comparativeCurrency, AccountLimitationCalculationType calculationType, double amount, AccountLimitationInterval interval) {
        return Iterators.findOne(getLimitations(), limitation -> {
            if(calculationType != limitation.getCalculationType()) return false;
            if(!comparativeCurrency.equals(limitation.getComparativeCurrency())) return false;
            if(amount != limitation.getAmount()) return false;
            if(interval != limitation.getInterval()) return false;
            return true;
        });
    }

    @Override
    public boolean hasLimitation(Currency currency, double amount) {
        return this.account.hasLimitationInternal(this, currency, amount);
    }

    @Override
    public AccountLimitation addLimitation(Currency comparativeCurrency, AccountLimitationCalculationType calculationType, double amount, AccountLimitationInterval interval) {
        return this.account.addLimitationInternal(this, null, comparativeCurrency, calculationType, amount, interval);
    }

    @Override
    public boolean removeLimitation(AccountLimitation limitation) {
        if(!this.equals(limitation.getMember())) return false;
        return getAccount().removeLimitation(limitation);
    }

    @Override
    public boolean receiveNotifications() {
        return this.receiveNotifications;
    }

    @Override
    public void setReceiveNotifications(boolean receiveNotifications) {
        DefaultDKCoinsStorage storage = DefaultDKCoins.getInstance().getStorage();
        DefaultAccountManager accountManager = DefaultDKCoins.getInstance().getAccountManager();

        storage.getAccountMember().update()
                .set("ReceiveNotifications", receiveNotifications)
                .where("Id", getId())
                .execute();

        updateReceiveNotifications(receiveNotifications);

        accountManager.getAccountCache().getCaller().updateAndIgnore(getAccount().getId(), Document.newDocument()
                .add("action", SyncAction.ACCOUNT_MEMBER_UPDATE_RECEIVE_NOTIFICATIONS)
                .add("memberId", getId())
                .add("receiveNotifications", receiveNotifications()));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AccountMember && ((AccountMember)obj).getId() == getId();
    }

    @Internal
    public void updateRole(AccountMemberRole role) {
        this.role = role;
    }

    @Internal
    public void updateReceiveNotifications(boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }
}
