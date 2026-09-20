package com.dansplugins.cellphones

import org.bukkit.plugin.java.JavaPlugin

class Cellphones : JavaPlugin() {

    override fun onEnable() {
        logger.info("Cellphones ${description.version} enabled.")
    }

    override fun onDisable() {
        logger.info("Cellphones disabled.")
    }
}
