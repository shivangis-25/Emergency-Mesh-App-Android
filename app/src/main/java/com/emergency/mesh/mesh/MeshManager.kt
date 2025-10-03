package com.emergency.mesh.mesh

import com.emergency.mesh.data.MessageDao
import com.emergency.mesh.data.MessageEntity
import com.emergency.mesh.p2p.P2PSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MeshManager(
    private val p2pSender: P2PSender,
    private val messageDao: MessageDao
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun handleIncoming(message: MessageEntity) {
        // Save locally
        scope.launch {
            messageDao.insertMessage(message)
        }

        // Forward via P2P
        p2pSender.send(message)
    }
}
