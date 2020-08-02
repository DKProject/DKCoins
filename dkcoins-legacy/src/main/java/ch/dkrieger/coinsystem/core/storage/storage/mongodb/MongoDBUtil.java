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

package ch.dkrieger.coinsystem.core.storage.storage.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import net.pretronic.libraries.document.type.DocumentFileType;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;

import java.util.LinkedList;
import java.util.List;

/*
 *
 *  * Copyright (c) 2018 Davide Wietlisbach on 16.11.18 18:51
 *
 */

public class MongoDBUtil {

    public static JsonWriterSettings MONGOJSONSETTINGS = JsonWriterSettings.builder()
            .int64Converter((value, writer) -> writer.writeNumber(value.toString())).build();

    public static <O> O toObject(Document document, Class<O> clazz){
        return DocumentFileType.JSON.getReader().read(document.toJson(MONGOJSONSETTINGS)).getAsObject(clazz);
    }

    public static Document toDocument(Object object){
        return Document.parse(DocumentFileType.JSON.getWriter().write(net.pretronic.libraries.document.Document.newDocument(object), false));
    }

    public static void insertOne(MongoCollection collection, Object object){
        collection.insertOne(toDocument(object));
    }

    public static void insertMany(MongoCollection collection, Object... objects){
        List<Document> docuemtns = new LinkedList<>();
        for(Object object : objects) docuemtns.add(toDocument(object));
        collection.insertMany(docuemtns);
    }

    public static void updateOne(MongoCollection collection, String identifier, Object identifierObject, String valueIdentifier, Object value) {
        MongoDBUtil.updateOne(collection, new Document(identifier, identifierObject),new Document("$set",new Document().append(valueIdentifier,value)));
    }

    public static void updateOne(MongoCollection collection,Bson bson, Object object){
        collection.updateOne(bson,toDocument(object));
    }

    public static void updateOne(MongoCollection collection,Bson bson1, Bson bson2){
        collection.updateOne(bson1,bson2);
    }

    public static void updateMany(MongoCollection collection,Bson bson, Object object){
        collection.updateMany(bson,toDocument(object));
    }

    public static void replaceOne(MongoCollection collection,Bson bson, Object object){
        collection.replaceOne(bson,toDocument(object));
    }

    public static void deleteOne(MongoCollection collection,Bson bson){
        collection.deleteOne(bson);
    }

    public static void deleteMany(MongoCollection collection,Bson bson){
        collection.deleteOne(bson);
    }

    public static <O> List<O> findALL(MongoCollection collection,Class<O> clazz){
        FindIterable<Document> documents = collection.find();
        List<O> list = new LinkedList<>();
        if(documents != null) for(Document document : documents) list.add(toObject(document,clazz));
        return list;
    }

    public static <O> List<O> findAndSort(MongoCollection collection,Class<O> clazz, String sort, int limit){
        FindIterable<Document> documents = collection.find().sort(new Document(sort,-1)).limit(limit);
        List<O> list = new LinkedList<>();
        if(documents != null) for(Document document : documents) list.add(toObject(document,clazz));
        return list;
    }

    public static <O> List<O> find(MongoCollection collection, Bson bson, Class<O> clazz){
        FindIterable<Document> documents = collection.find(bson);
        List<O> list = new LinkedList<>();
        if(documents != null) for(Document document : documents) list.add(toObject(document,clazz));
        return list;
    }

    public static <O> O findFirst(MongoCollection collection,Bson bson,Class<O> clazz){
        Document document = (Document) collection.find(bson).first();
        if(document != null) return toObject(document,clazz);
        return null;
    }
}