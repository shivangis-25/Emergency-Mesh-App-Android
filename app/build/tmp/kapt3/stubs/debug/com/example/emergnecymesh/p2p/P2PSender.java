package com.example.emergnecymesh.p2p;

/**
 * Interface for sending P2P messages
 * Implemented by P2PManager and can be injected into mesh layer
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&\u00a8\u0006\u0006"}, d2 = {"Lcom/example/emergnecymesh/p2p/P2PSender;", "", "sendMessage", "", "message", "Lcom/example/emergnecymesh/model/Message;", "app_debug"})
public abstract interface P2PSender {
    
    /**
     * Send a message to all connected peers
     */
    public abstract void sendMessage(@org.jetbrains.annotations.NotNull
    com.example.emergnecymesh.model.Message message);
}