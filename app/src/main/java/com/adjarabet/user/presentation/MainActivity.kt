package com.adjarabet.user.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adjarabet.user.R
import com.adjarabet.user.common.helpers.viewBinding
import com.adjarabet.user.databinding.ActivityMainBinding
import com.adjarabet.user.domain.models.*
import com.adjarabet.user.presentation.adapters.GameHistoryAdapter
import com.adjarabet.user.presentation.resultDialog.ResultDialog
import org.koin.android.viewmodel.ext.android.viewModel
import com.adjarabet.common.UserBotCommonLogicConstants.MSG_WORD_RECEIVED_KEY
import com.adjarabet.common.UserBotCommonLogicConstants.MSG_WORD_RECEIVED_WHAT
import com.adjarabet.common.UserBotCommonLogicConstants.MSG_WORD_SEND_WHAT
import com.adjarabet.common.UserBotCommonLogicConstants.MSG_WORD_SENT_KEY


class MainActivity : AppCompatActivity() {
    private val viewBinding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel by viewModel<MainViewModel>()
    private var botService: Messenger? = null
    private var bound: Boolean? = null
    private var gameHistoryAdapter: GameHistoryAdapter? = null
    private val gameHistoryList = mutableListOf<GameHistoryItemDataModel>()
    private val receiveMessageHandler = Handler.Callback {
        when (it.what) {
            MSG_WORD_SEND_WHAT -> viewModel.onWordReceivedIntent(it.data.getString(MSG_WORD_SENT_KEY)!!)
        }
        true
    }
    private val receiveMessenger = Messenger(MessageReceiverMessenger(receiveMessageHandler))


    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            botService = Messenger(service)
            bound = true
            try {
                val msg = Message.obtain(null, MSG_WORD_SEND_WHAT, 0, 0).apply {
                    replyTo = receiveMessenger
                }
                botService?.send(msg)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            botService = null
            bound = false
        }

    }

    private fun handleIncomingMessage(word: String) {
        val mp = MediaPlayer.create(this, R.raw.mixt)
        mp.start()
        updateGameHistory(GameHistoryItemDataModel(PlayerType.BOT, word))
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent().apply {
            component = ComponentName(BOT_SERVICE_PACKAGE, BOT_SERVICE_CLASS_PATH)
        }
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        viewModel.getLiveViewState().observe(this, {
            reflectState(it)
        })
        gameHistoryAdapter = GameHistoryAdapter()
        viewBinding.apply {
            btnSubmit.setOnClickListener {
                if (viewBinding.etWordInput.text?.isBlank() == true) {
                    Toast.makeText(this@MainActivity, getString(R.string.main_enter_word_hint), Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.sendWordIntent(viewBinding.etWordInput.text.toString())
                }
            }
            rvHistory.layoutManager = LinearLayoutManager(this@MainActivity)
            rvHistory.adapter = gameHistoryAdapter
        }
    }


    private fun sendWord(string: String) {
        val msg = Message.obtain(null, MSG_WORD_RECEIVED_WHAT, 0, 0, null).apply {
            data = Bundle().apply {
                putString(MSG_WORD_RECEIVED_KEY, string)
            }
        }

        try {
            botService?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (bound == true) {
            unbindService(connection)
            bound = false
        }
    }

    private fun reflectState(mainViewState: MainViewState) {
        mainViewState.apply {
            validationResultOK?.getValue()?.also {
                sendWord(it)
            }
            wordReceived?.getValue()?.also {
                handleIncomingMessage(it)
            }
            gameLost?.getValue()?.also {
                showGameLost(it)
            }
            gameWon?.getValue()?.also {
                showGameWon()
            }
        }
    }

    private fun showGameWon() {
        clearGameData()
        ResultDialog.show(this@MainActivity).setData(null)
    }

    private fun clearGameData() {
        viewBinding.etWordInput.setText("")
        gameHistoryList.clear()
        gameHistoryAdapter?.diffUtilsUpdate(gameHistoryList, false)
    }

    private fun showGameLost(it: ValidationResultLost) {
        clearGameData()
        ResultDialog.show(this).setData(it)
    }

    private fun sendWord(validationResult: ValidationResultOK) {
        sendWord(validationResult.word)
        updateGameHistory(GameHistoryItemDataModel(playerType = PlayerType.USER, validationResult.word))
    }

    private fun updateGameHistory(gameHistoryItemDataModel: GameHistoryItemDataModel) {
        gameHistoryList.add(0, gameHistoryItemDataModel)
        gameHistoryAdapter?.diffUtilsUpdate(gameHistoryList, true)
        viewBinding.rvHistory.scrollToPosition(0)
    }

    companion object {
        const val BOT_SERVICE_PACKAGE = "com.adjarabet.bot"
        const val BOT_SERVICE_CLASS_PATH = "com.adjarabet.bot.services.DummyGameBot"

        class MessageReceiverMessenger(callback: Callback) : Handler(Looper.getMainLooper(), callback)
    }

}