package com.emergency.mesh.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0016\u001a\u00020\u0017H\u0002J\u0010\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J\b\u0010\u001b\u001a\u00020\u001cH\u0002J\b\u0010\u001d\u001a\u00020\u0017H\u0002J\u0012\u0010\u001e\u001a\u00020\u00172\b\u0010\u001f\u001a\u0004\u0018\u00010 H\u0014J\u0018\u0010!\u001a\u00020\u00172\u0006\u0010\"\u001a\u00020\u00142\u0006\u0010#\u001a\u00020\u0014H\u0002J\b\u0010$\u001a\u00020\u0017H\u0002J\b\u0010%\u001a\u00020\u0017H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0005\u001a\u00020\u00068BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\r\u001a\u00020\u000e8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0011\u0010\n\u001a\u0004\b\u000f\u0010\u0010R\u001c\u0010\u0012\u001a\u0010\u0012\f\u0012\n \u0015*\u0004\u0018\u00010\u00140\u00140\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006&"}, d2 = {"Lcom/emergency/mesh/ui/MainActivity3;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/emergency/mesh/databinding/ActivityMain3Binding;", "fusedLocationClient", "Lcom/google/android/gms/location/FusedLocationProviderClient;", "getFusedLocationClient", "()Lcom/google/android/gms/location/FusedLocationProviderClient;", "fusedLocationClient$delegate", "Lkotlin/Lazy;", "messageAdapter", "Lcom/emergency/mesh/ui/MessageAdapter;", "messageViewModel", "Lcom/emergency/mesh/ui/MessageViewModel;", "getMessageViewModel", "()Lcom/emergency/mesh/ui/MessageViewModel;", "messageViewModel$delegate", "requestPermissionLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "", "kotlin.jvm.PlatformType", "checkLocationPermission", "", "getUserPhoneNumber", "context", "Landroid/content/Context;", "hasLocationPermission", "", "observeMessages", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "sendMessage", "text", "type", "setupClickListeners", "setupRecyclerView", "app_debug"})
public final class MainActivity3 extends androidx.appcompat.app.AppCompatActivity {
    private com.emergency.mesh.databinding.ActivityMain3Binding binding;
    @org.jetbrains.annotations.NotNull
    private final com.emergency.mesh.ui.MessageAdapter messageAdapter = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy fusedLocationClient$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final kotlin.Lazy messageViewModel$delegate = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> requestPermissionLauncher = null;
    
    public MainActivity3() {
        super();
    }
    
    private final com.google.android.gms.location.FusedLocationProviderClient getFusedLocationClient() {
        return null;
    }
    
    private final com.emergency.mesh.ui.MessageViewModel getMessageViewModel() {
        return null;
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupRecyclerView() {
    }
    
    private final void observeMessages() {
    }
    
    private final void setupClickListeners() {
    }
    
    private final void sendMessage(java.lang.String text, java.lang.String type) {
    }
    
    private final boolean hasLocationPermission() {
        return false;
    }
    
    private final void checkLocationPermission() {
    }
    
    private final java.lang.String getUserPhoneNumber(android.content.Context context) {
        return null;
    }
}