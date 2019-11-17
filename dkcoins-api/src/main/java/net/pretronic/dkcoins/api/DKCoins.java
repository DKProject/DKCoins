/*
 * (C) Copyright 2019 The DKCoins Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 17.11.19, 14:19
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkcoins.api;

import net.prematic.libraries.logging.PrematicLogger;
import net.pretronic.dkcoins.api.account.AccountManager;
import net.pretronic.dkcoins.api.currency.CurrencyManager;
import net.pretronic.dkcoins.api.storage.DKCoinsStorage;
import net.pretronic.dkcoins.api.user.DKCoinsUserManager;

public interface DKCoins {

    PrematicLogger getLogger();

    AccountManager getAccountManager();

    CurrencyManager getCurrencyManager();

    DKCoinsUserManager getUserManager();

    DKCoinsStorage getStorage();

    static DKCoins getInstance() {
        return InstanceHolder.INSTANCE;
    }

    static void setInstance(DKCoins instance) {
        InstanceHolder.INSTANCE = instance;
    }

    class InstanceHolder {
        public static DKCoins INSTANCE;
    }
}