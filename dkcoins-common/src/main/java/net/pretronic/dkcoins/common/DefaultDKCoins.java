/*
 * (C) Copyright 2019 The DKCoins Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 17.11.19, 21:06
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkcoins.common;

import net.pretronic.databasequery.api.Database;
import net.pretronic.dkcoins.api.DKCoins;
import net.pretronic.dkcoins.api.DKCoinsFormatter;
import net.pretronic.dkcoins.api.account.transaction.TransactionFilter;
import net.pretronic.dkcoins.api.account.transaction.TransactionPropertyBuilder;
import net.pretronic.dkcoins.api.migration.Migration;
import net.pretronic.dkcoins.common.account.DefaultAccountManager;
import net.pretronic.dkcoins.common.account.transaction.DefaultTransactionFilter;
import net.pretronic.dkcoins.common.currency.DefaultCurrencyManager;
import net.pretronic.dkcoins.common.user.DefaultDKCoinsUserManager;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.logging.PretronicLogger;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultDKCoins extends DKCoins {

    private final PretronicLogger logger;
    private final EventBus eventBus;
    private final DefaultDKCoinsStorage storage;
    private final DefaultAccountManager accountManager;
    private final DefaultCurrencyManager currencyManager;
    private final DefaultDKCoinsUserManager userManager;
    private final TransactionPropertyBuilder transactionPropertyBuilder;
    private final Collection<Migration> migrations;
    private final DKCoinsFormatter formatter;

    public DefaultDKCoins(PretronicLogger logger, EventBus eventBus, Database database, DefaultDKCoinsUserManager userManager
            , TransactionPropertyBuilder transactionPropertyBuilder, DKCoinsFormatter formatter) {
        Validate.notNull(logger,
                eventBus,
                database,
                userManager,
                transactionPropertyBuilder,
                formatter);
        DKCoins.setInstance(this);
        this.logger = logger;
        this.eventBus = eventBus;
        this.storage = new DefaultDKCoinsStorage(database);
        this.accountManager = new DefaultAccountManager();
        this.currencyManager = new DefaultCurrencyManager();
        this.userManager = userManager;
        this.transactionPropertyBuilder = transactionPropertyBuilder;
        this.formatter = formatter;
        this.migrations = new ArrayList<>();
    }

    @Override
    public PretronicLogger getLogger() {
        return this.logger;
    }

    @Override
    public EventBus getEventBus() {
        return this.eventBus;
    }

    @Override
    public DefaultAccountManager getAccountManager() {
        return this.accountManager;
    }

    @Override
    public DefaultCurrencyManager getCurrencyManager() {
        return this.currencyManager;
    }

    @Override
    public DefaultDKCoinsUserManager getUserManager() {
        return this.userManager;
    }

    public DefaultDKCoinsStorage getStorage() {
        return this.storage;
    }

    @Override
    public TransactionPropertyBuilder getTransactionPropertyBuilder() {
        return this.transactionPropertyBuilder;
    }

    @Override
    public TransactionFilter newTransactionFilter() {
        return new DefaultTransactionFilter();
    }

    @Override
    public Collection<Migration> getMigrations() {
        return this.migrations;
    }

    @Override
    public Migration getMigration(String name) {
        return Iterators.findOne(this.migrations, migration -> migration.getName().equalsIgnoreCase(name));
    }

    @Override
    public void registerMigration(Migration migration) {
        this.migrations.add(migration);
    }

    @Override
    public DKCoinsFormatter getFormatter() {
        return formatter;
    }

    public void createDefaults() {
        if(getAccountManager().searchAccountType("Bank") == null) {
            getAccountManager().createAccountType("Bank", "*");
        }
        if(getAccountManager().searchAccountType("User") == null) {
            getAccountManager().createAccountType("User", "");
        }
    }

    public static DefaultDKCoins getInstance() {
        return (DefaultDKCoins) DKCoins.getInstance();
    }
}
