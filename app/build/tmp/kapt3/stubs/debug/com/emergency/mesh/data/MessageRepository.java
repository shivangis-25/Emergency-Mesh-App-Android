package com.emergency.mesh.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0012\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006J\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/emergency/mesh/data/MessageRepository;", "", "dao", "Lcom/emergency/mesh/data/MessageDao;", "(Lcom/emergency/mesh/data/MessageDao;)V", "getAllMessages", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/emergency/mesh/data/MessageEntity;", "saveMessage", "", "message", "(Lcom/emergency/mesh/data/MessageEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class MessageRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.emergency.mesh.data.MessageDao dao = null;
    
    public MessageRepository(@org.jetbrains.annotations.NotNull()
    com.emergency.mesh.data.MessageDao dao) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveMessage(@org.jetbrains.annotations.NotNull()
    com.emergency.mesh.data.MessageEntity message, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.emergency.mesh.data.MessageEntity>> getAllMessages() {
        return null;
    }
}