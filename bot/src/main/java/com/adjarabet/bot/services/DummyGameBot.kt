package com.adjarabet.bot.services

import android.app.Service
import android.content.Intent
import android.os.*
import android.widget.Toast
import com.adjarabet.common.UserBotCommonLogicConstants.BOT_LOSING_ERR_MESSAGE
import com.adjarabet.common.UserBotCommonLogicConstants.MSG_WORD_RECEIVED_KEY
import com.adjarabet.common.UserBotCommonLogicConstants.MSG_WORD_RECEIVED_WHAT
import com.adjarabet.common.UserBotCommonLogicConstants.MSG_WORD_SEND_WHAT
import com.adjarabet.common.UserBotCommonLogicConstants.MSG_WORD_SENT_KEY
import org.apache.commons.lang3.RandomStringUtils
import java.util.*
import kotlin.random.Random


class DummyGameBot : Service() {
    private lateinit var messengerServer: Messenger
    private lateinit var messengerClient: Messenger
    private val losingProbabiltyRange = 0..BOT_LOSING_PROBABILITY_PERCENT
    private val handlerCallback = Handler.Callback { msg ->
        when (msg.what) {
            MSG_WORD_RECEIVED_WHAT -> handleReceivedMessage(msg.data.getString(MSG_WORD_RECEIVED_KEY))
            MSG_WORD_SEND_WHAT -> handleSendMessage(msg)
        }
        true
    }

    private fun handleSendMessage(message: Message) {
        messengerClient = message.replyTo
    }

    private fun handleReceivedMessage(receivedWord: String?) {
        Handler(Looper.getMainLooper()).postDelayed({
            messengerClient.send(Message.obtain(null, MSG_WORD_SEND_WHAT, 0, 0).apply {
                data = Bundle().apply {
                    putString(MSG_WORD_SENT_KEY, generateRandomWord(receivedWord))
                }
            })
        }, BOT_RESPONSE_DELAY_MS.toLong())

    }


    private fun generateRandomWord(word: String?): String {
        return if (losingProbabiltyRange.contains(Random.nextInt(PERCENT_MAX_VALUE))) {
            BOT_LOSING_ERR_MESSAGE
        } else {
            val newWordList = word?.split(' ')
            val randWord = RandomStringUtils.randomAlphabetic(Random.nextInt(RANDOM_WORD_MAX_LENGTH))
            if (newWordList?.contains(randWord) == true ) {
                generateRandomWord(word)
            }
            "$word $randWord"
        }

    }


    override fun onBind(intent: Intent?): IBinder? {
        messengerServer = Messenger(IncomingMessageHandler(handlerCallback))
        return messengerServer.binder
    }

    companion object {
        const val BOT_RESPONSE_DELAY_MS  = 500
        const val BOT_LOSING_PROBABILITY_PERCENT = 3
        const val PERCENT_MAX_VALUE = 100
        const val RANDOM_WORD_MAX_LENGTH = 12

        class IncomingMessageHandler(handlerCallBack: Callback) : Handler(Looper.getMainLooper(), handlerCallBack)
    }

}