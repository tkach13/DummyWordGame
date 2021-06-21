package com.adjarabet.user.presentation.resultDialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.adjarabet.user.R
import com.adjarabet.user.databinding.DialogResultBinding
import com.adjarabet.user.domain.models.*

class ResultDialog(context: Context) : Dialog(context, R.style.ResultDialogStyle) {
    private val viewBinding = DialogResultBinding.inflate(LayoutInflater.from(context))

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(viewBinding.root)
        setCancelable(true)
        this.window?.apply {
            setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.color_black_70)))
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        }
        viewBinding.btnOK.setOnClickListener {
            dismiss()
        }
    }

    fun setData(validationResult: ValidationResultLost?) {
        if (validationResult == null) {
            showGameWon()
        } else {
            showGameLost(validationResult)
        }

    }


    private fun showGameLost(validationResult: ValidationResultLost) {
        viewBinding.apply {
            btnOK.background = ContextCompat.getDrawable(context, R.drawable.bg_button)
            tvTitle.text = context.getString(R.string.dialog_game_lost_text)
            val incorrectWordLit = StringBuilder()

            if (validationResult.lostReasons is LostReasonIncorrectWord) {
                validationResult.lostReasons.incorrectWord.keys.forEach {
                    incorrectWordLit.append(context.getString(R.string.dialog_game_lost_reason_incorrect_text, it,
                            validationResult.lostReasons.incorrectWord[it]))
                    incorrectWordLit.append("\n")
                }
            }
            tvSecondaryText.text  = when (validationResult.lostReasons) {
                LostReasonNoWordAdded -> context.getString(R.string.dialog_game_lost_reason_no_words_added_text)
                LostReasonTwoWordsAdded -> context.getString(R.string.dialog_game_lost_reason_two_words_text)
                is LostReasonWordRepeated -> context.getString(R.string.dialog_game_lost_reason_repetition_text, validationResult.lostReasons.repeatedWord)
                is LostReasonIncorrectWord -> incorrectWordLit.toString()
            }
        }
    }

    private fun showGameWon() {
        viewBinding.apply {
            btnOK.background = ContextCompat.getDrawable(context, R.drawable.bg_button_won)
            tvSecondaryText.text = context.getString(R.string.bot_lost_expl_text)
            tvTitle.text = context.getString(R.string.game_history_item_player_type_you)
        }
    }


    companion object {
        fun show(context: Context): ResultDialog {
            var resultDialog: ResultDialog? = null
            try {

                resultDialog = ResultDialog(context)
                resultDialog.show()

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return resultDialog!!
        }
    }

}