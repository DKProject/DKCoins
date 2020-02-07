/*
 * (C) Copyright 2019 The DKCoins Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 22.11.19, 19:44
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkcoins.minecraft.currency;

import net.pretronic.dkcoins.api.DKCoins;
import net.pretronic.dkcoins.api.currency.Currency;
import net.pretronic.dkcoins.api.currency.CurrencyExchangeRate;

public class DefaultCurrencyExchangeRate implements CurrencyExchangeRate {

    private final int id;
    private final Currency currency;
    private final Currency targetCurrency;
    private double exchangeAmount;

    public DefaultCurrencyExchangeRate(int id, Currency currency, Currency targetCurrency, double exchangeAmount) {
        this.id = id;
        this.currency = currency;
        this.targetCurrency = targetCurrency;
        this.exchangeAmount = exchangeAmount;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Currency getCurrency() {
        return this.currency;
    }

    @Override
    public Currency getTargetCurrency() {
        return this.targetCurrency;
    }

    @Override
    public double getExchangeAmount() {
        return this.exchangeAmount;
    }

    @Override
    public void setExchangeAmount(double amount) {
        this.exchangeAmount = amount;
        DKCoins.getInstance().getCurrencyManager().updateCurrencyExchangeRateAmount(this);
    }

    @Override
    public void incrementExchangeAmount(double amount) {
        setExchangeAmount(this.exchangeAmount+amount);
    }

    @Override
    public void decrementExchangeAmount(double amount) {
        setExchangeAmount(this.exchangeAmount-amount);
    }
}