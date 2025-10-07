package com.emergency.mesh.mesh;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/emergency/mesh/mesh/MeshManager;", "", "p2pSender", "Lcom/emergency/mesh/p2p/P2PSender;", "messageDao", "Lcom/emergency/mesh/data/MessageDao;", "(Lcom/emergency/mesh/p2p/P2PSender;Lcom/emergency/mesh/data/MessageDao;)V", "scope", "Lkotlinx/coroutines/CoroutineScope;", "handleIncoming", "", "message", "Lcom/emergency/mesh/data/MessageEntity;", "app_debug"})
public final class MeshManager {
    @org.jetbrains.annotations.NotNull
    private final com.emergency.mesh.p2p.P2PSender p2pSender = null;
    @org.jetbrains.annotations.NotNull
    private final com.emergency.mesh.data.MessageDao messageDao = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.CoroutineScope scope = null;
    
    public MeshManager(@org.jetbrains.annotations.NotNull
    com.emergency.mesh.p2p.P2PSender p2pSender, @org.jetbrains.annotations.NotNull
    com.emergency.mesh.data.MessageDao messageDao) {
        super();
    }
    
    public final void handleIncoming(@org.jetbrains.annotations.NotNull
    com.emergency.mesh.data.MessageEntity message) {
    }
}