package com.spotware.example;

import java.math.BigDecimal;

import com.google.protobuf.MessageLite;
import com.spotware.connect.Config;
import com.spotware.connect.netty.AuthHelper;
import com.spotware.connect.netty.NettyClient;
import com.spotware.connect.netty.handler.ProtoMessageReceiver;
import com.xtrader.protocol.openapi.v2.ProtoOAClosePositionReq;
import com.xtrader.protocol.openapi.v2.ProtoOAErrorRes;
import com.xtrader.protocol.openapi.v2.ProtoOAExecutionEvent;
import com.xtrader.protocol.openapi.v2.ProtoOAOrderErrorEvent;

public class ProtoOAClosePositionReqExample {
	
	private static Config config;
    
	public static void main(String[] args) throws InterruptedException {
        config = new Config();
        NettyClient nettyClient = new NettyClient(config.getHost(), config.getPort());
        try {
        	Thread.sleep(1000);
            AuthHelper authHelper = nettyClient.getAuthHelper();
            authHelper.authorizeOnlyOneTrader(config.getClientId(), config.getClientSecret(), config.getCtid(), config.getAccessToken());

            sendProtoOAClosePositionReq(nettyClient, config.getCtid());
        } finally {
        	nettyClient.closeConnection();
        }
    }

    private static void sendProtoOAClosePositionReq(NettyClient nettyClient, long ctidTraderAccountId) throws InterruptedException {
    	ProtoOAClosePositionReq protoOAClosePositionReq = ProtoOAClosePositionReq
                .newBuilder()
                .setCtidTraderAccountId(ctidTraderAccountId)
                .setPositionId(19366573)
                .setVolume(BigDecimal.valueOf(0.01).multiply(BigDecimal.valueOf(10000000)).longValue())
                .build();
        ProtoMessageReceiver receiver = nettyClient.writeAndFlush(protoOAClosePositionReq);

        MessageLite messageLite = receiver.waitSingleResult();
        
        if (messageLite instanceof ProtoOAExecutionEvent) {
        	ProtoOAExecutionEvent errorRes = (ProtoOAExecutionEvent) messageLite;
            System.out.println(errorRes);
        } else if (messageLite instanceof ProtoOAOrderErrorEvent) {
        	ProtoOAOrderErrorEvent errorRes = (ProtoOAOrderErrorEvent) messageLite;
            System.out.println(errorRes);
        } else if (messageLite instanceof ProtoOAErrorRes) {
            ProtoOAErrorRes errorRes = (ProtoOAErrorRes) messageLite;
            System.out.println(errorRes);
        }
    }
    
}
