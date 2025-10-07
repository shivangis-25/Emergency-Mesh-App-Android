package com.emergency.mesh.ui;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ&\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00072\u0006\u0010\u0012\u001a\u00020\u00072\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014R\u001d\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/emergency/mesh/ui/MessageViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/emergency/mesh/data/MessageRepository2;", "meshManager", "Lcom/emergency/mesh/p2p/MeshManager;", "senderNumber", "", "(Lcom/emergency/mesh/data/MessageRepository2;Lcom/emergency/mesh/p2p/MeshManager;Ljava/lang/String;)V", "allMessages", "Landroidx/lifecycle/LiveData;", "", "Lcom/emergency/mesh/data/Message;", "getAllMessages", "()Landroidx/lifecycle/LiveData;", "sendMessage", "", "text", "messageType", "latitude", "", "longitude", "app_debug"})
public final class MessageViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final com.emergency.mesh.data.MessageRepository2 repository = null;
    @org.jetbrains.annotations.NotNull
    private final com.emergency.mesh.p2p.MeshManager meshManager = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String senderNumber = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.lifecycle.LiveData<java.util.List<com.emergency.mesh.data.Message>> allMessages = null;
    
    public MessageViewModel(@org.jetbrains.annotations.NotNull
    com.emergency.mesh.data.MessageRepository2 repository, @org.jetbrains.annotations.NotNull
    com.emergency.mesh.p2p.MeshManager meshManager, @org.jetbrains.annotations.NotNull
    java.lang.String senderNumber) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<java.util.List<com.emergency.mesh.data.Message>> getAllMessages() {
        return null;
    }
    
    public final void sendMessage(@org.jetbrains.annotations.NotNull
    java.lang.String text, @org.jetbrains.annotations.NotNull
    java.lang.String messageType, double latitude, double longitude) {
    }
}