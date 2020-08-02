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

package ch.dkrieger.coinsystem.core.storage.storage;

/*
 *
 *  * Copyright (c) 2018 Davide Wietlisbach on 16.11.18 19:24
 *
 */

public enum StorageType {

    JSON(),
    SQLITE(),
    MYSQL(),
    MONGODB();

    public static StorageType parse(String name){
        try{
            return valueOf(name.toUpperCase());
        }catch (Exception exception){}
        return JSON;
    }
}