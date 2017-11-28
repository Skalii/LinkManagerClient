package com.skaliy.linkmanager.client.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

import java.util.ArrayList;

public class ClientHandler extends ChannelInboundMessageHandlerAdapter<String> {

    static int resultSize = 0;
    static ArrayList<String[]> queryResult = new ArrayList<String[]>();
    static boolean isFullResult = false;

    @Override
    public void messageReceived(ChannelHandlerContext channelHandlerContext, String message) throws Exception {

        if (message.startsWith("[SERVER] - accepted the query: ")) {
            return;
        }

        if (message.startsWith("[SERVER] - query state: ")) {
            resultSize = 1;
            return;
        }

        if (message.startsWith("[SERVER] - result size: ")) {
            resultSize = Integer.parseInt(message
                    .substring(message.lastIndexOf(" ") + 1));
            return;
        }

        String[] record = message
                .substring(1, message.length() - 1)
                .split(", ");

        queryResult.add(record);

        if (queryResult.size() == resultSize) {
            isFullResult = true;
        }
    }

}