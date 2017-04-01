package com.notesdea.server;

import com.google.gson.Gson;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by notes on 2017/2/24.
 */

public class SocketHandler extends IoHandlerAdapter {
    private Set<IoSession> sessions = new HashSet<>();

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        sessions.add(session);
        System.out.println("sessionCreated executed");
        for (IoSession ioSession : sessions) {
            if (ioSession != session) {
                ioSession.write("@create session");
            }
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        if (sessions.contains(session)) {
            sessions.remove(session);
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String msg = ((String) message).trim();
        if (msg.equals("@quit")) {
            sessions.remove(session);
            session.closeNow();
            return;
        }

        System.out.println(msg);

        for (IoSession ioSession : sessions) {
            if (ioSession != session) {
                String data = reformatData(msg, ioSession.getId());
                session.write(data);
            }
        }
    }

    private String reformatData(String data, long sessionId) {
        String marker = "@location";
        if (data.startsWith(marker)) {
            data = data.replace(marker, "");
            System.out.println(data);
        }
        UserLocation userLocation = new Gson().fromJson(data, UserLocation.class);
        userLocation.setSessionId(sessionId);

        return marker + data;
    }
}
