package com.emergency.mesh.data;

/**
 * Implementation of MessageRepository2 using Room.
 * Added support for senderNumber field.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\bH\u0016J\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0096@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0005H\u0096@\u00a2\u0006\u0002\u0010\u0010J\u0016\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\nH\u0096@\u00a2\u0006\u0002\u0010\u0013J\f\u0010\u0014\u001a\u00020\n*\u00020\u0015H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/emergency/mesh/data/RoomMessageRepository;", "Lcom/emergency/mesh/data/MessageRepository2;", "messageDao", "Lcom/emergency/mesh/db/MessageDao;", "senderNumber", "", "(Lcom/emergency/mesh/db/MessageDao;Ljava/lang/String;)V", "getAllMessages", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/emergency/mesh/data/Message;", "getUnsyncedMessages", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "markAsSynced", "", "messageId", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveMessage", "message", "(Lcom/emergency/mesh/data/Message;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toMessage", "Lcom/emergency/mesh/db/MessageEntity;", "app_debug"})
public final class RoomMessageRepository implements com.emergency.mesh.data.MessageRepository2 {
    @org.jetbrains.annotations.NotNull
    private final com.emergency.mesh.db.MessageDao messageDao = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String senderNumber = null;
    
    public RoomMessageRepository(@org.jetbrains.annotations.NotNull
    com.emergency.mesh.db.MessageDao messageDao, @org.jetbrains.annotations.NotNull
    java.lang.String senderNumber) {
        super();
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.Nullable
    public java.lang.Object saveMessage(@org.jetbrains.annotations.NotNull
    com.emergency.mesh.data.Message message, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.Nullable
    public java.lang.Object getUnsyncedMessages(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.List<com.emergency.mesh.data.Message>> $completion) {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.Nullable
    public java.lang.Object markAsSynced(@org.jetbrains.annotations.NotNull
    java.lang.String messageId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public kotlinx.coroutines.flow.Flow<java.util.List<com.emergency.mesh.data.Message>> getAllMessages() {
        return null;
    }
    
    private final com.emergency.mesh.data.Message toMessage(com.emergency.mesh.db.MessageEntity $this$toMessage) {
        return null;
    }
}