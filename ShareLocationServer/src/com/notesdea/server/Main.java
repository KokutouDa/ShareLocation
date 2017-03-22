package com.notesdea.server;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by notes on 2017/2/24.
 */
public class Main {

    private static final int PORT = 8000;

    public static void main(String[] args) throws IOException {
        NioSocketAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
        acceptor.setHandler(new SocketHandler());
        acceptor.bind(new InetSocketAddress(PORT));
        System.out.println("Listening on port " + PORT);
    }
}
