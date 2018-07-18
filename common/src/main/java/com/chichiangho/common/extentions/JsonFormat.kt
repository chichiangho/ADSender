package com.chichiangho.common.extentions

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import java.lang.reflect.Type

fun Any.toJson(): String = gson.toJson(this)

@Throws(JsonSyntaxException::class)
fun <T> String.toObj(clazz: Class<T>): T = gson.fromJson(this, clazz)


@Throws(JsonSyntaxException::class)
fun <T> String.toObj(type: Type): T = gson.fromJson(this, type)

@Throws(JsonSyntaxException::class)
fun <T> String.toObjArray(clz: Class<T>): ArrayList<T> {
    val array = JsonParser().parse(this).asJsonArray
    return array.mapTo(ArrayList()) { gson.fromJson(it, clz) }
}

private val gson = Gson()
//private val gson = GsonBuilder().registerTypeAdapterFactory(EmptyAdapterFactory()).create()!!
//private val stringAdapter = StringAdapter()
//private val intAdapter = IntegerAdapter()
//private val longAdapter = LongAdapter()
//private val doubleAdapter = DoubleAdapter()
//
//class EmptyAdapterFactory : TypeAdapterFactory {
//    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
//        val rawType = type.rawType as Class<T>
//        if (rawType == String::class.java) {
//            return stringAdapter as TypeAdapter<T>
//        } else if (rawType == Int::class.javaPrimitiveType || rawType == Int::class.java) {
//            return intAdapter as TypeAdapter<T>
//        } else if (rawType == Long::class.javaPrimitiveType || rawType == Long::class.java) {
//            return longAdapter as TypeAdapter<T>
//        } else if (rawType == Double::class.javaPrimitiveType || rawType == Double::class.java) {
//            return doubleAdapter as TypeAdapter<T>
//        }
//        return null
//    }
//}
//
//class StringAdapter : TypeAdapter<String>() {
//    @Throws(IOException::class)
//    override fun read(reader: JsonReader): String {
//        if (reader.peek() == JsonToken.NULL) {
//            reader.nextNull()
//            return ""
//        }
//        return reader.nextString()
//    }
//
//    @Throws(IOException::class)
//    override fun write(writer: JsonWriter, value: String?) {
//        if (value == null) {
//            writer.nullValue()
//            return
//        }
//        writer.value(value)
//    }
//}
//
//class IntegerAdapter : TypeAdapter<Int>() {
//    @Throws(IOException::class)
//    override fun read(reader: JsonReader): Int? {
//        if (reader.peek() == JsonToken.NULL) {
//            reader.nextNull()
//            return -1314//某些接口0可能有特殊含义
//        }
//        return reader.nextInt()
//    }
//
//    @Throws(IOException::class)
//    override fun write(writer: JsonWriter, value: Int?) {
//        if (value == null) {
//            writer.nullValue()
//            return
//        }
//        writer.value(value)
//    }
//}
//
//class LongAdapter : TypeAdapter<Long>() {
//    @Throws(IOException::class)
//    override fun read(reader: JsonReader): Long? {
//        if (reader.peek() == JsonToken.NULL) {
//            reader.nextNull()
//            return -1314L
//        }
//        return reader.nextLong()
//    }
//
//    @Throws(IOException::class)
//    override fun write(writer: JsonWriter, value: Long?) {
//        if (value == null) {
//            writer.nullValue()
//            return
//        }
//        writer.value(value)
//    }
//}
//
//class DoubleAdapter : TypeAdapter<Double>() {
//    @Throws(IOException::class)
//    override fun read(reader: JsonReader): Double? {
//        if (reader.peek() == JsonToken.NULL) {
//            reader.nextNull()
//            return -1314.0
//        }
//        return reader.nextDouble()
//    }
//
//    @Throws(IOException::class)
//    override fun write(writer: JsonWriter, value: Double?) {
//        if (value == null) {
//            writer.nullValue()
//            return
//        }
//        writer.value(value)
//    }
//}