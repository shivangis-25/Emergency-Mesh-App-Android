package com.emergency.mesh.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00050\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\n\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u000b"}, d2 = {"Lcom/emergency/mesh/data/EmergencyContactDao;", "", "deleteContact", "", "contact", "Lcom/emergency/mesh/data/EmergencyContactEntity;", "(Lcom/emergency/mesh/data/EmergencyContactEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllContacts", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertContact", "app_debug"})
@androidx.room.Dao
public abstract interface EmergencyContactDao {
    
    @androidx.room.Query(value = "SELECT * FROM emergency_contacts")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getAllContacts(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.List<com.emergency.mesh.data.EmergencyContactEntity>> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object insertContact(@org.jetbrains.annotations.NotNull
    com.emergency.mesh.data.EmergencyContactEntity contact, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object deleteContact(@org.jetbrains.annotations.NotNull
    com.emergency.mesh.data.EmergencyContactEntity contact, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}