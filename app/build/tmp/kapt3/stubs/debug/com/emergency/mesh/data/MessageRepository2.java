package com.emergency.mesh.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u0003H&J\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00a6@\u00a2\u0006\u0002\u0010\u0007J\u0016\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u00a6@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u000f\u00a8\u0006\u0010"}, d2 = {"Lcom/emergency/mesh/data/MessageRepository2;", "", "getAllMessages", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/emergency/mesh/data/Message;", "getUnsyncedMessages", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "markAsSynced", "", "messageId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveMessage", "message", "(Lcom/emergency/mesh/data/Message;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface MessageRepository2 {
    
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object saveMessage(@org.jetbrains.annotations.NotNull
    com.emergency.mesh.data.Message message, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getUnsyncedMessages(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.List<com.emergency.mesh.data.Message>> $completion);
    
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object markAsSynced(@org.jetbrains.annotations.NotNull
    java.lang.String messageId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.emergency.mesh.data.Message>> getAllMessages();
}