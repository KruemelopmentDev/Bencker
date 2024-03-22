package org.kruemelopment.de.bewecker

import android.annotation.SuppressLint
import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log

@SuppressLint("Override")
class TileClass : TileService() {
    override fun onTileAdded() {
        val tile = this.qsTile
        val status = serviceStatus
        tile.state =
            if (status) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.updateTile()
    }

    /**
     * Called when this tile begins listening for events.
     */
    override fun onStartListening() {
        val tile = this.qsTile
        val status = serviceStatus
        tile.state =
            if (status) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.updateTile()
    }

    /**
     * Called when the user taps the tile.
     */
    override fun onClick() {
        updateTile()
    }

    /**
     * Called when this tile moves out of the listening state.
     */
    override fun onStopListening() {
        Log.d("QS", "Stop Listening")
    }

    /**
     * Called when the user removes this tile from Quick Settings.
     */
    override fun onTileRemoved() {
        Log.d("QS", "Tile removed")
    }

    private fun updateTile() {
        changeServiceStatus()
    }

    private fun changeServiceStatus() {
        val prefs =
            applicationContext.getSharedPreferences("global", MODE_PRIVATE)
        var isActive = prefs.getBoolean("isactive", true)
        isActive = !isActive
        prefs.edit().putBoolean("isactive", isActive).apply()
        val tile = this.qsTile
        tile.state =
            if (isActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.updateTile()
        val intent = Intent(applicationContext, NotificationListener::class.java)
        intent.setAction("isactiveupdate")
        applicationContext.startService(intent)
    }

    private val serviceStatus: Boolean
        get() {
            val prefs = applicationContext.getSharedPreferences(
                "global",
                MODE_PRIVATE
            )
            return prefs.getBoolean("isactive", true)
        }
}