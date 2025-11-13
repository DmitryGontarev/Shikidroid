package com.shikidroid.utils

import android.app.AlarmManager
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Context
import android.hardware.SensorManager
import android.media.AudioManager
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager

/** Функция получения доступа к сервису системной сигнализации */
fun Context.alarmManager(): AlarmManager =
    getSystemService(Context.ALARM_SERVICE) as AlarmManager

/** Функция получения доступа к аудио сервису */
fun Context.audioManager(): AudioManager =
    getSystemService(Context.AUDIO_SERVICE) as AudioManager

/** Функция получения доступа к буферу обмена */
fun Context.clipboardManager(): ClipboardManager =
    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

/** Функция получения доступа к сервису загрузки */
fun Context.downloadManager(): DownloadManager? =
    getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

/** Функция получения доступа к сервису ввода */
fun Context.inputMethodManager(): InputMethodManager? =
    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

/** Функция получения доступа к сервису уведомлений */
fun Context.notificationManager(): NotificationManager =
    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

/** Функция получения доступа к сервису сенсоров устройства */
fun Context.sensorManager(): SensorManager =
    getSystemService(Context.SENSOR_SERVICE) as SensorManager