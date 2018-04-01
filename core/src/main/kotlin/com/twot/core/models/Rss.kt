package com.twot.core.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.core.ResponseDeserializable
import java.util.*


@JacksonXmlRootElement(localName = "rss")
@JsonIgnoreProperties(ignoreUnknown = true)
data class Rss(val channel: Channel) {

    companion object {
        val xmlMapper = XmlMapper().registerKotlinModule()

//        init {
//            //setup so we can deserialize dates correctly
//            val javaTimeModule = JavaTimeModule()
//
//            javaTimeModule.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(DateTimeFormatter.RFC_1123_DATE_TIME))
//            xmlMapper.registerModule(javaTimeModule)
//        }
    }

    class Deserializer: ResponseDeserializable<Rss> {
        override fun deserialize(content: String) : Rss? {
            if (content.isBlank()) return null

            return xmlMapper.readValue(content)
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Channel(
        @JacksonXmlElementWrapper(useWrapping = false)
        val item: List<Item>?,
        val image: Image?)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Image(val url: String?)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Item(
        val link: String,
        val pubDate: Date,
        val title: String,
        val creator: String)
