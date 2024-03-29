/*
 * (C) Copyright 2020 The DKCoins Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 22.02.20, 15:28
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkcoins.minecraft.commands.bank;

import net.pretronic.dkcoins.api.DKCoins;
import net.pretronic.dkcoins.api.account.AccountType;
import net.pretronic.dkcoins.api.account.BankAccount;
import net.pretronic.dkcoins.api.user.DKCoinsUser;
import net.pretronic.dkcoins.minecraft.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;

public class BankCreateCommand extends ObjectCommand<String> {

    public BankCreateCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("create"));
    }

    @Override
    public void execute(CommandSender commandSender, String bankName, String[] args) {
        if(!(commandSender instanceof OnlineMinecraftPlayer)) {
            commandSender.sendMessage(Messages.ERROR_NOT_FROM_CONSOLE);
            return;
        }
        OnlineMinecraftPlayer player = (OnlineMinecraftPlayer)commandSender;

        String accountType0 = args.length == 1 ? args[0] : "Bank";
        AccountType accountType = DKCoins.getInstance().getAccountManager().searchAccountType(accountType0);
        if(accountType == null) {
            commandSender.sendMessage(Messages.ERROR_ACCOUNT_TYPE_NOT_EXISTS, VariableSet.create()
                    .add("name", accountType0));
            return;
        }

        BankAccount account = DKCoins.getInstance().getAccountManager().getAccount(bankName, accountType0);
        if(account != null) {
            commandSender.sendMessage(Messages.ERROR_ACCOUNT_ALREADY_EXISTS, VariableSet.create()
                    .addDescribed("account", account));
            return;
        }

        String requiredPermission = "dkcoins.account.type.permission."+accountType0;
        if(!commandSender.hasPermission(requiredPermission)) {
            commandSender.sendMessage(Messages.ERROR_NO_PERMISSION);
            return;
        }
        account = DKCoins.getInstance().getAccountManager().createAccount(bankName, accountType, false, null, player.getAs(DKCoinsUser.class));
        player.sendMessage(Messages.COMMAND_BANK_CREATE_DONE, VariableSet.create()
                .addDescribed("account", account));
    }
}
