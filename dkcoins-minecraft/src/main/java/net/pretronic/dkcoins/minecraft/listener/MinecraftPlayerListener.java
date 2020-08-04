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

package net.pretronic.dkcoins.minecraft.listener;

import net.pretronic.dkcoins.api.DKCoins;
import net.pretronic.dkcoins.api.account.AccountType;
import net.pretronic.dkcoins.api.account.BankAccount;
import net.pretronic.dkcoins.api.user.DKCoinsUser;
import net.pretronic.dkcoins.minecraft.config.DKCoinsConfig;
import net.pretronic.libraries.event.Listener;
import org.mcnative.common.event.player.login.MinecraftPlayerPostLoginEvent;
import org.mcnative.common.player.OnlineMinecraftPlayer;

public class MinecraftPlayerListener {

    @Listener
    public void onPlayerLogin(MinecraftPlayerPostLoginEvent event) {
        OnlineMinecraftPlayer player = event.getOnlinePlayer();

        initUserAccount(player);

        DKCoinsUser user = player.getAs(DKCoinsUser.class);
        DKCoins.getInstance().getAccountManager().hasAccountLimitation(user.getAsMember(user.getDefaultAccount()),
                DKCoinsConfig.CURRENCY_DEFAULT, 100);
    }

    private void initUserAccount(OnlineMinecraftPlayer player) {
        AccountType accountType = DKCoins.getInstance().getAccountManager().searchAccountType("User");
        BankAccount account = DKCoins.getInstance().getAccountManager().getAccount(player.getName(), accountType);
        if(account == null) {
            DKCoins.getInstance().getAccountManager().createAccount(player.getName(),
                    accountType, false, null, player.getAs(DKCoinsUser.class));
        }
    }
}
