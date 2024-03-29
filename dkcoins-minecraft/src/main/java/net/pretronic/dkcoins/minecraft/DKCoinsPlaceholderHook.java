/*
 * (C) Copyright 2020 The DKCoins Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 02.08.20, 20:44
 * @web %web%
 *
 * The DKCoins Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package net.pretronic.dkcoins.minecraft;

import net.pretronic.dkcoins.api.DKCoins;
import net.pretronic.dkcoins.api.account.AccountCredit;
import net.pretronic.dkcoins.api.account.BankAccount;
import net.pretronic.dkcoins.api.currency.Currency;
import net.pretronic.dkcoins.api.user.DKCoinsUser;
import net.pretronic.dkcoins.minecraft.config.DKCoinsConfig;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.Validate;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.serviceprovider.placeholder.PlaceholderHook;

public class DKCoinsPlaceholderHook implements PlaceholderHook {

    /*
    Currency: Sonst default

    Direct player:
    dkcoins_balance_[currency]

    Specific player:
    dkcoins_player_(player)_balance_[currency]

    Top:
    dkcoins_top_(rank)_name_[currency]
    dkcoins_top_(rank)_balance_[currency]

    dkcoins_toppos_[currency]

    Bank:
    dkcoins_bank_(bank)_balance_[currency]
     */


    @Override
    public Object onRequest(MinecraftPlayer player, String parameter) {
        String[] parameters = parameter.toLowerCase().split("_");
        switch (parameters[0].toLowerCase()) {
            case "balance": {
                Currency currency = parseCurrency(parameters,1);
                DKCoinsUser user = player.getAs(DKCoinsUser.class);

                BankAccount defaultAccount = user.getDefaultAccount();
                Validate.notNull(defaultAccount, "Default account for player " + player.getName() + " is not available");

                AccountCredit credit = defaultAccount.getCredit(currency);
                Validate.notNull(credit, "Account credit for player " + player.getName() + " with currency " + currency.getName() + " is not available");

                return DKCoins.getInstance().getFormatter().formatCurrencyAmount(credit.getAmount());
            }
            case "player": {
                if(parameter.length() > 2) {
                    MinecraftPlayer minecraftPlayer =  McNative.getInstance().getPlayerManager().getPlayer(parameters[1]);
                    if(minecraftPlayer == null) return null;
                    DKCoinsUser user = minecraftPlayer.getAs(DKCoinsUser.class);
                    if(user != null) {
                        if ("balance".equalsIgnoreCase(parameters[2])) {
                            Currency currency = parseCurrency(parameters, 3);
                            return DKCoins.getInstance().getFormatter().formatCurrencyAmount(user.getDefaultAccount().getCredit(currency).getAmount());
                        }
                    }
                }
                break;
            }
            case "top:": {
                if(parameters.length > 2) {
                    String rank0 = parameters[1];
                    if(GeneralUtil.isNaturalNumber(rank0)) {
                        int rank = Integer.parseInt(rank0);
                        Currency currency = parseCurrency(parameters, 3);
                        switch (parameters[2].toLowerCase()) {
                            case "name": {
                                BankAccount account = DKCoins.getInstance().getAccountManager().getAccountByRank(currency, rank);
                                return account.getName();
                            }
                            case "balance": {
                                BankAccount account = DKCoins.getInstance().getAccountManager().getAccountByRank(currency, rank);
                                return DKCoins.getInstance().getFormatter().formatCurrencyAmount(account.getCredit(currency).getAmount());
                            }
                        }
                    }
                }
                break;
            }
            case "bank": {
                if(parameters.length > 2) {
                    String bank0 = parameters[1];
                    BankAccount account = DKCoins.getInstance().getAccountManager().searchAccount(bank0);
                    if(account != null) {
                        if ("balance".equalsIgnoreCase(parameters[2])) {
                            Currency currency = parseCurrency(parameters, 3);
                            return DKCoins.getInstance().getFormatter().formatCurrencyAmount(account.getCredit(currency).getAmount());
                        }
                    }
                }
                break;
            }
            case "toppos": {
                Currency currency = parseCurrency(parameters, 1);
                AccountCredit credit = player.getAs(DKCoinsUser.class).getDefaultAccount().getCredit(currency);
                return credit.getTopPos();
            }
        }
        return null;
    }

    private Currency parseCurrency(String[] parameters, int currencyPlace) {
        Currency currency;
        if(parameters.length > currencyPlace) {
            String currency0 = parameters[currencyPlace];
            currency = DKCoins.getInstance().getCurrencyManager().searchCurrency(currency0);
            Validate.notNull(currency, "Currency " + currency0 + " is not available");
        } else {
            currency = DKCoinsConfig.CURRENCY_DEFAULT;
        }
        return currency;
    }


}
