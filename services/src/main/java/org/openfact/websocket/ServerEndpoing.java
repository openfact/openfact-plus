package org.openfact.websocket;

import org.openfact.representations.idm.DocumentRepresentation;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@Singleton
@ServerEndpoint("/changes")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ServerEndpoing {

    private Session session;

    @OnOpen
    public void onOnpen(Session session) {
        this.session = session;
    }

    @OnClose
    public void onClose(Session session) {
        this.session = null;
    }

    public void onToDoChange(@Observes(during = TransactionPhase.AFTER_SUCCESS) @ChangeEvent(ChangeEvent.Type.CREATION) DocumentRepresentation document) {
        if (this.session != null && this.session.isOpen()) {
            try {
                this.session.getBasicRemote().sendText("Hello world");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


