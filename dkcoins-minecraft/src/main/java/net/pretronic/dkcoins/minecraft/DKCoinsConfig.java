/*
 * (C) Copyright 2020 The DKCoins Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 29.01.20, 16:35
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkcoins.minecraft;

import net.pretronic.dkcoins.api.account.AccountType;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.document.annotations.DocumentIgnored;
import net.pretronic.dkcoins.api.DKCoins;
import net.pretronic.dkcoins.api.currency.Currency;
import net.pretronic.dkcoins.minecraft.commands.UserBankCommand;
import net.pretronic.libraries.document.annotations.DocumentKey;
import net.pretronic.libraries.plugin.service.ServicePriority;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.map.Maps;
import net.pretronic.libraries.utility.map.Pair;
import org.mcnative.common.McNative;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DKCoinsConfig {

    @DocumentKey("autoUpdate.enabled")
    public static boolean AUTO_UPDATE_ENABLED = true;

    @DocumentKey("autoUpdate.qualifier")
    public static String AUTO_UPDATE_QUALIFIER = "RELEASE";

    @DocumentKey("currency.default")
    private static String CURRENCY_DEFAULT0 = "Coins";

    @DocumentIgnored
    public static Currency CURRENCY_DEFAULT = null;

    @DocumentKey("currency.format.pattern")//currency.format.separator: ###,###.##
    private static String CURRENCY_FORMAT0 = "###,###.##";

    @DocumentIgnored
    private static DecimalFormat CURRENCY_FORMAT = null;

    @DocumentKey("currency.format.separator.grouping")
    private static char CURRENCY_FORMAT_SEPARATOR_GROUPING = '\'';

    @DocumentKey("currency.format.separator.decimal")
    private static char CURRENCY_FORMAT_SEPARATOR_DECIMAL = '.';


    @DocumentKey("date.format.pattern")
    private static String DATE_FORMAT0 = "dd-MM-yyyy hh:mm";

    @DocumentIgnored
    public static SimpleDateFormat DATE_FORMAT = null;


    @DocumentKey("account.type.startAmount")
    private static Map<String, Integer> ACCOUNT_TYPE_START_AMOUNT0 = Maps.ofValues(new Pair<>("User", 1000), new Pair<>("Bank", 0));

    @DocumentIgnored
    private static Map<AccountType, Integer> ACCOUNT_TYPE_START_AMOUNT = new HashMap<>();

    @DocumentKey("account.user.creditAliases")
    public static String[] ACCOUNT_USER_CREDIT_ALIASES = new String[]{"coins"};

    @DocumentKey("economyProvider.enabled")
    public static boolean ECONOMY_PROVIDER_ENABLED = true;

    @DocumentKey("economyProvider.priority")
    public static byte ECONOMY_PROVIDER_PRIORITY = ServicePriority.HIGHEST;

    @DocumentKey("economyProvider.currency")
    private static String ECONOMY_PROVIDER_CURRENCY0 = CURRENCY_DEFAULT0;

    @DocumentIgnored
    public static Currency ECONOMY_PROVIDER_CURRENCY = null;

    @DocumentKey("command.top.limit.default")
    public static int TOP_LIMIT_DEFAULT = 5;

    @DocumentKey("command.top.limit.max")
    public static int TOP_LIMIT_MAX = 10;

    @DocumentKey("command.bank")
    public static CommandConfiguration COMMAND_BANK = CommandConfiguration.newBuilder()
            .name("bank")
            .enabled(McNative.getInstance().getPlatform().isService())
            .permission("dkcoins.command.bank")
            .create();

    @DocumentKey("command.currency")
    public static CommandConfiguration COMMAND_CURRENCY = CommandConfiguration.newBuilder()
            .name("currency")
            .enabled(McNative.getInstance().getPlatform().isService())
            .permission("dkcoins.command.currency")
            .create();

    @DocumentKey("command.pay")
    public static CommandConfiguration COMMAND_PAY = CommandConfiguration.newBuilder()
            .name("pay")
            .aliases("transfer")
            .enabled(McNative.getInstance().getPlatform().isService())
            .create();

    public static void init() {
        CURRENCY_DEFAULT = DKCoins.getInstance().getCurrencyManager().searchCurrency(CURRENCY_DEFAULT0);
        CURRENCY_FORMAT = new DecimalFormat(CURRENCY_FORMAT0);

        DecimalFormatSymbols formatSymbols = CURRENCY_FORMAT.getDecimalFormatSymbols();
        formatSymbols.setGroupingSeparator(CURRENCY_FORMAT_SEPARATOR_GROUPING);
        formatSymbols.setDecimalSeparator(CURRENCY_FORMAT_SEPARATOR_DECIMAL);
        CURRENCY_FORMAT.setDecimalFormatSymbols(formatSymbols);

        ACCOUNT_TYPE_START_AMOUNT0.forEach((type, startAmount) ->
                ACCOUNT_TYPE_START_AMOUNT.put(DKCoins.getInstance().getAccountManager().searchAccountType(type), startAmount));

        for (String userBankCommand : ACCOUNT_USER_CREDIT_ALIASES) {
            McNative.getInstance().getLocal().getCommandManager().registerCommand(new UserBankCommand(DKCoinsPlugin.getInstance()
                    , CommandConfiguration.name(userBankCommand)));
        }

        ECONOMY_PROVIDER_CURRENCY = DKCoins.getInstance().getCurrencyManager().searchCurrency(ECONOMY_PROVIDER_CURRENCY0);

        DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT0);
    }

    public static String formatCurrencyAmount(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }

    public static String formatDate(long time) {
        return DATE_FORMAT.format(time);
    }

    public static double getAccountTypeStartAmount(AccountType type) {
        Validate.notNull(type);
        for (Map.Entry<AccountType, Integer> entry : ACCOUNT_TYPE_START_AMOUNT.entrySet()) {
            if(entry.getKey().equals(type)) return entry.getValue();
        }
        return 0.0D;
    }
}
