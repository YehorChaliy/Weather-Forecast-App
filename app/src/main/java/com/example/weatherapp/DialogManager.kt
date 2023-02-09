package com.example.weatherapp

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText

// в этом классе(object) спрашиваем пользователя нужно включить GPS и создаем диалоговое окно
object DialogManager {
    fun locationSettingDialog(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle("GPS disabled")
        dialog.setMessage("Do you want enable location?")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK"){_,_ ->
            // прослушиваем функцию интерфейса
            listener.onClick(null)
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"){_,_ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    fun searchByNameDialog(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val edName = EditText(context)
        // добавляем чтобы видеть вписанный текст
        builder.setView(edName)
        val dialog = builder.create()
        dialog.setTitle("Enter city:")
        dialog.setMessage("Search for a city name")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK"){_,_ ->
            listener.onClick(edName.text.toString())
            dialog.dismiss()
        }
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel"){_,_ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    // мост между диалогом и MainFragment
    interface Listener{
        fun onClick(name: String?)
    }
}