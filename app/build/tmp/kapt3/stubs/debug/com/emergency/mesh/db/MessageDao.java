package com.emergency.mesh.db;

/**
 * Data Access Object (DAO) for the messages table.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0019\u0010\u0002\u001a\u0013\u0012\u000f\u0012\r\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\u0002\b\u00060\u0003H\'J\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u0010\u00a8\u0006\u0011"}, d2 = {"Lcom/emergency/mesh/db/MessageDao;", "", "getAllMessages", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/emergency/mesh/db/MessageEntity;", "Lkotlin/jvm/JvmSuppressWildcards;", "getUnsyncedMessages", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "", "message", "(Lcom/emergency/mesh/db/MessageEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "markAsSynced", "messageId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao
public abstract interface MessageDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull
    com.emergency.mesh.db.MessageEntity message, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM messages ORDER BY timestamp DESC")
    @org.jetbrains.annotations.NotNull
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.emergency.mesh.db.MessageEntity>> getAllMessages();
    
    @androidx.room.Query(value = "SELECT * FROM messages WHERE isSynced = 0")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getUnsyncedMessages(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.List<com.emergency.mesh.db.MessageEntity>> $completion);
    
    @androidx.room.Query(value = "UPDATE messages SET isSynced = 1 WHERE id = :messageId")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object markAsSynced(@org.jetbrains.annotations.NotNull
    java.lang.String messageId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}