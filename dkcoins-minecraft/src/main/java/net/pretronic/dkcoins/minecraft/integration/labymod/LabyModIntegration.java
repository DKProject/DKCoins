/*
 * (C) Copyright 2021 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.02.21, 08:35
 *
 * The DKBans Project is under the Apache License, version 2.0 (the "License");
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

package net.pretronic.dkcoins.minecraft.integration.labymod;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.pretronic.dkcoins.api.account.AccountCredit;
import net.pretronic.dkcoins.api.account.AccountType;
import net.pretronic.dkcoins.minecraft.config.DKCoinsConfig;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.exception.OperationFailedException;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;

public class LabyModIntegration {

    public static final String BALANCE_TYPE_BANK = "bank";
    public static final String BALANCE_TYPE_CASH = "cash";

    public static String getBalanceType(AccountType type) {
        if(type.getName().equalsIgnoreCase("user")) return BALANCE_TYPE_CASH;
        return BALANCE_TYPE_BANK;
    }

    public static void sendPlayerBalance(ConnectedMinecraftPlayer player, AccountCredit credit) {
        sendPlayerBalance(player, getBalanceType(credit.getAccount().getType()), credit.getAmount());
    }

    public static void sendPlayerBalance(ConnectedMinecraftPlayer player, String cashType, double balance) {
        Document economyObject = Document.newDocument();
        Document cashObject = Document.newDocument();

        cashObject.set("visible", true);

        cashObject.set("balance", balance);

        economyObject.set(cashType, cashObject);

        sendLabyModMessage(player, "economy", economyObject);
    }

    public static void sendLabyModMessage(ConnectedMinecraftPlayer player,String key, Document document){
        String json = DocumentFileType.JSON.getWriter().write(document,false);
        byte[] bytes = getBytesToSend( key, json);
        player.sendData("lmc",bytes);
    }

    private static byte[] getBytesToSend(String messageKey, String messageContents ) {
        ByteBuf byteBuf = Unpooled.buffer();
        writeString( byteBuf, messageKey );
        writeString( byteBuf, messageContents );
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes( bytes );
        return bytes;
    }

    private static void writeVarIntToBuffer(ByteBuf buf,int input) {
        while ( (input & -128) != 0 ) {
            buf.writeByte( input & 127 | 128 );
            input >>>= 7;
        }
        buf.writeByte( input );
    }

    private static void writeString(ByteBuf buf, String string) {
        byte[] abyte = string.getBytes(StandardCharsets.UTF_8);

        if ( abyte.length > Short.MAX_VALUE ) {
            throw new OperationFailedException( "String too big (was " + string.length() + " bytes encoded, max " + Short.MAX_VALUE + ")" );
        } else {
            writeVarIntToBuffer( buf, abyte.length );
            buf.writeBytes( abyte );
        }
    }

    public static int readVarIntFromBuffer(ByteBuf buf) {
        int i = 0;
        int j = 0;

        byte b0;
        do {
            b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;
            if ( j > 5 )   throw new OperationFailedException( "VarInt too big" );
        } while ( (b0 & 128) == 128 );

        return i;
    }

    public static String readString(ByteBuf buf, int maxLength) {
        int i = readVarIntFromBuffer( buf );

        if ( i > maxLength * 4 ) {
            throw new OperationFailedException( "The received encoded string buffer length is longer than maximum allowed (" + i + " > " + maxLength * 4 + ")" );
        } else if ( i < 0 ) {
            throw new OperationFailedException( "The received encoded string buffer length is less than zero! Weird string!" );
        } else {
            byte[] bytes = new byte[i];
            buf.readBytes( bytes );

            String s = new String( bytes, StandardCharsets.UTF_8);
            if ( s.length() > maxLength ) {
                throw new OperationFailedException( "The received string length is longer than maximum allowed (" + i + " > " + maxLength + ")" );
            } else {
                return s;
            }
        }
    }
}
