package com.emergency.mesh.mesh

import com.emergency.mesh.data.MessageEntity
import com.emergency.mesh.data.MessageRepository
import com.emergency.mesh.p2p.P2PSender
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertTrue

class MeshManagerTest {
    private val mockSender = mockk<P2PSender>(relaxed = true)
    private val fakeRepo = mockk<MessageRepository>()

    @Test
    fun `rebroadcasts valid message`() = runBlocking {
        val msg = MessageEntity("1", "senderA", "Help!", 12.0, 77.0, System.currentTimeMillis(), 3)

        coEvery { fakeRepo.getMessageById("1") } returns null
        coEvery { fakeRepo.saveMessage(any()) } returns Unit

        val manager = MeshManager(mockSender, fakeRepo)
        manager.handleIncoming(msg)

        coVerify { mockSender.broadcast(any()) }
    }

    @Test
    fun `ignores duplicate message`() = runBlocking {
        val msg = MessageEntity("1", "senderA", "Help!", 12.0, 77.0, System.currentTimeMillis(), 3)

        coEvery { fakeRepo.getMessageById("1") } returns msg

        val manager = MeshManager(mockSender, fakeRepo)
        manager.handleIncoming(msg)

        coVerify(exactly = 0) { mockSender.broadcast(any()) }
    }

    @Test
    fun `ignores expired TTL message`() = runBlocking {
        val msg = MessageEntity("2", "senderB", "SOS", 15.0, 80.0, System.currentTimeMillis(), 0)

        coEvery { fakeRepo.getMessageById("2") } returns null
        coEvery { fakeRepo.saveMessage(any()) } returns Unit

        val manager = MeshManager(mockSender, fakeRepo)
        manager.handleIncoming(msg)

        coVerify(exactly = 0) { mockSender.broadcast(any()) }
    }
}
