package com.emergency.mesh.p2p

import com.emergency.mesh.data.MessageEntity

interface P2PSender {
    fun send(message: MessageEntity)
}
