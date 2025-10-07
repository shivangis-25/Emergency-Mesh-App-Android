package com.emergency.mesh.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\f\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\n0\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ$\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\b0\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0014\u0010\u0015R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0016"}, d2 = {"Lcom/emergency/mesh/data/EmergencyAlertRepository;", "", "contactDao", "Lcom/emergency/mesh/data/EmergencyContactDao;", "smsManager", "Lcom/emergency/mesh/util/SMSManager;", "(Lcom/emergency/mesh/data/EmergencyContactDao;Lcom/emergency/mesh/util/SMSManager;)V", "addEmergencyContact", "", "contact", "Lcom/emergency/mesh/data/EmergencyContact;", "(Lcom/emergency/mesh/data/EmergencyContact;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteContact", "getAllContacts", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendEmergencyAlert", "Lkotlin/Result;", "alertMessage", "", "sendEmergencyAlert-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class EmergencyAlertRepository {
    @org.jetbrains.annotations.NotNull
    private final com.emergency.mesh.data.EmergencyContactDao contactDao = null;
    @org.jetbrains.annotations.NotNull
    private final com.emergency.mesh.util.SMSManager smsManager = null;
    
    public EmergencyAlertRepository(@org.jetbrains.annotations.NotNull
    com.emergency.mesh.data.EmergencyContactDao contactDao, @org.jetbrains.annotations.NotNull
    com.emergency.mesh.util.SMSManager smsManager) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object addEmergencyContact(@org.jetbrains.annotations.NotNull
    com.emergency.mesh.data.EmergencyContact contact, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getAllContacts(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.List<com.emergency.mesh.data.EmergencyContact>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object deleteContact(@org.jetbrains.annotations.NotNull
    com.emergency.mesh.data.EmergencyContact contact, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}