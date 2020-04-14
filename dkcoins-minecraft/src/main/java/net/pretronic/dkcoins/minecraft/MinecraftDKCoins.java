/*
 * (C) Copyright 2019 The DKCoins Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 17.11.19, 21:06
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkcoins.minecraft;

import net.pretronic.dkcoins.api.DKCoins;
import net.pretronic.dkcoins.api.DKCoinsStorage;
import net.pretronic.dkcoins.api.account.AccountManager;
import net.pretronic.dkcoins.api.account.member.AccountMemberRole;
import net.pretronic.dkcoins.api.account.transaction.TransactionFilter;
import net.pretronic.dkcoins.api.account.transaction.TransactionPropertyBuilder;
import net.pretronic.dkcoins.api.currency.CurrencyManager;
import net.pretronic.dkcoins.api.migration.Migration;
import net.pretronic.dkcoins.api.user.DKCoinsUser;
import net.pretronic.dkcoins.api.user.DKCoinsUserManager;
import net.pretronic.dkcoins.minecraft.account.*;
import net.pretronic.dkcoins.minecraft.account.transaction.DefaultAccountTransaction;
import net.pretronic.dkcoins.minecraft.account.transaction.DefaultTransactionFilter;
import net.pretronic.dkcoins.minecraft.commands.dkcoins.DKCoinsCommand;
import net.pretronic.dkcoins.minecraft.commands.account.AccountTransferCommand;
import net.pretronic.dkcoins.minecraft.commands.bank.BankCommand;
import net.pretronic.dkcoins.minecraft.commands.currency.CurrencyCommand;
import net.pretronic.dkcoins.minecraft.config.DKCoinsConfig;
import net.pretronic.dkcoins.minecraft.currency.DefaultCurrency;
import net.pretronic.dkcoins.minecraft.currency.DefaultCurrencyExchangeRate;
import net.pretronic.dkcoins.minecraft.currency.DefaultCurrencyManager;
import net.pretronic.dkcoins.minecraft.listener.MinecraftPlayerListener;
import net.pretronic.dkcoins.minecraft.migration.EssentialsXMigration;
import net.pretronic.dkcoins.minecraft.migration.LegacyDKCoinsMigration;
import net.pretronic.dkcoins.minecraft.user.DefaultDKCoinsUser;
import net.pretronic.dkcoins.minecraft.user.DefaultDKCoinsUserManager;
import net.pretronic.libraries.logging.PretronicLogger;
import net.pretronic.libraries.message.bml.variable.describer.VariableDescriberRegistry;
import net.pretronic.libraries.utility.Iterators;
import org.mcnative.common.McNative;
import org.mcnative.common.plugin.configuration.ConfigurationProvider;
import org.mcnative.common.serviceprovider.economy.EconomyProvider;
import org.mcnative.common.serviceprovider.placeholder.PlaceholderService;

import java.util.ArrayList;
import java.util.Collection;

public class MinecraftDKCoins extends DKCoins {

    private final PretronicLogger logger;
    private final DKCoinsStorage storage;
    private final AccountManager accountManager;
    private final CurrencyManager currencyManager;
    private final DKCoinsUserManager userManager;
    private final TransactionPropertyBuilder transactionPropertyBuilder;
    private final Collection<Migration> migrations;

    MinecraftDKCoins(PretronicLogger logger, TransactionPropertyBuilder transactionPropertyBuilder) {
        DKCoins.setInstance(this);
        this.logger = logger;
        this.storage = new DefaultDKCoinsStorage(McNative.getInstance().getPluginManager().getService(ConfigurationProvider.class)
                .getDatabase(DKCoinsPlugin.getInstance(), true));
        this.accountManager = new DefaultAccountManager();
        this.currencyManager = new DefaultCurrencyManager();
        this.userManager = new DefaultDKCoinsUserManager();
        this.transactionPropertyBuilder = transactionPropertyBuilder;
        this.migrations = new ArrayList<>();

        createDefaults();
        DKCoinsConfig.init();
        registerPlayerAdapter();
        registerEconomyProvider();
        PlaceholderService.registerPlaceHolders(DKCoinsPlugin.getInstance(), "dkcoins", new DKCoinsPlaceholderHook());
        setupMigration();
        registerCommandsAndListeners();
        registerVariableDescribers();
    }

    @Override
    public PretronicLogger getLogger() {
        return this.logger;
    }

    @Override
    public AccountManager getAccountManager() {
        return this.accountManager;
    }

    @Override
    public CurrencyManager getCurrencyManager() {
        return this.currencyManager;
    }

    @Override
    public DKCoinsUserManager getUserManager() {
        return this.userManager;
    }

    @Override
    public DKCoinsStorage getStorage() {
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

    private void createDefaults() {
        if(getAccountManager().searchAccountType("Bank") == null) {
            getAccountManager().createAccountType("Bank", "*");
        }
        if(getAccountManager().searchAccountType("User") == null) {
            getAccountManager().createAccountType("User", "");
        }
    }

    private void registerPlayerAdapter() {
        McNative.getInstance().getPlayerManager().registerPlayerAdapter(DKCoinsUser.class, minecraftPlayer ->
                DKCoins.getInstance().getUserManager().getUser(minecraftPlayer.getUniqueId()));
    }

    private void registerCommandsAndListeners() {
        McNative.getInstance().getLocal().getCommandManager().registerCommand(new DKCoinsCommand(DKCoinsPlugin.getInstance()));
        McNative.getInstance().getLocal().getCommandManager().registerCommand(new BankCommand(DKCoinsPlugin.getInstance()));
        McNative.getInstance().getLocal().getCommandManager().registerCommand(new CurrencyCommand(DKCoinsPlugin.getInstance()));
        McNative.getInstance().getLocal().getCommandManager().registerCommand(new AccountTransferCommand(DKCoinsPlugin.getInstance(), DKCoinsConfig.COMMAND_PAY));
        McNative.getInstance().getLocal().getEventBus().subscribe(DKCoinsPlugin.getInstance(), new MinecraftPlayerListener());
    }

    private void setupMigration() {
        registerMigration(new LegacyDKCoinsMigration());
        registerMigration(new EssentialsXMigration());
    }

    private void registerEconomyProvider() {
        if(DKCoinsConfig.ECONOMY_PROVIDER_ENABLED) {
            getLogger().info("Enabling economy provider");
            McNative.getInstance().getPluginManager().registerService(DKCoinsPlugin.getInstance(), EconomyProvider.class,
                    new DKCoinsEconomyProvider(), DKCoinsConfig.ECONOMY_PROVIDER_PRIORITY);
            getLogger().info("Economy provider enabled with priority " + DKCoinsConfig.ECONOMY_PROVIDER_PRIORITY);
        }
    }

    private void registerVariableDescribers() {
        VariableDescriberRegistry.registerDescriber(DefaultAccountTransaction.class);
        VariableDescriberRegistry.registerDescriber(DefaultCurrency.class);
        VariableDescriberRegistry.registerDescriber(DefaultCurrencyExchangeRate.class);
        VariableDescriberRegistry.registerDescriber(DefaultBankAccount.class);
        VariableDescriberRegistry.registerDescriber(DefaultAccountMember.class);
        VariableDescriberRegistry.registerDescriber(AccountMemberRole.class);
        VariableDescriberRegistry.registerDescriber(DefaultAccountCredit.class);
        VariableDescriberRegistry.registerDescriber(DefaultAccountLimitation.class);
        VariableDescriberRegistry.registerDescriber(DefaultDKCoinsUser.class);
        VariableDescriberRegistry.registerDescriber(DefaultRankedAccountCredit.class);
    }
}