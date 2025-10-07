package com.emergency.mesh.util;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0005\u001a\u00020\u0006J\b\u0010\u0007\u001a\u00020\u0006H\u0002J/\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\u000f\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0012"}, d2 = {"Lcom/emergency/mesh/util/SMSManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "checkSMSPermission", "", "hasPermission", "sendEmergencyAlert", "Lkotlin/Result;", "", "contacts", "", "Lcom/emergency/mesh/data/EmergencyContact;", "message", "", "sendEmergencyAlert-gIAlu-s", "(Ljava/util/List;Ljava/lang/String;)Ljava/lang/Object;", "app_debug"})
public final class SMSManager {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    
    public SMSManager(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    private final boolean hasPermission() {
        return false;
    }
    
    public final boolean checkSMSPermission() {
        return false;
    }
}