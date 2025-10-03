package com.emergency.mesh.p2p

import com.emergency.mesh.data.MessageEntity

class P2PSenderImpl : P2PSender {
    override fun send(message: MessageEntity) {
        // Stubbed for now, can be replaced with Bluetooth/WiFi Direct
        println("Sending message: ${message.text} from ${message.senderId}")
    }
}
