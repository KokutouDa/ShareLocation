package com.notesdea.server;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by notes on 2017/2/24.
 */

//todo 需要用到Gson 和 Json，还有个用户位置的类，服务端如何使用Gson类？
    //todo 看看客户端代码
public class SocketHandler extends IoHandlerAdapter {
    private Set<IoSession> sessions = new HashSet<>();

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        sessions.add(session);
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

        for (IoSession ioSession : sessions) {
            if (ioSession != session) {
                //todo 把已格式化的数据发送到其它会话上
                String data = msg/*reformatData(msg, )*/;
                session.write(data);
            }
        }
    }

    //??sessionId干嘛用？ sessionId用来告诉别人是哪个客户传来的信息。
    private String reformatData(String data, long sessionId) {
        //todo
        return null;
    }
}
