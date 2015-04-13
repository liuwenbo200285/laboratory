package com.wenbo.netty.udp;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

public class QuoteOfTheMomentClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        String response = msg.content().toString(CharsetUtil.UTF_8);
        System.out.println("接收到："+response);
//        ctx.writeAndFlush(new DatagramPacket(
//                Unpooled.copiedBuffer("test?", CharsetUtil.UTF_8),
//                new InetSocketAddress("127.0.0.1",7686))).sync();
//        if (response.startsWith("QOTM: ")) {
//            System.out.println("Quote of the Moment: " + response.substring(6));
//            ctx.close();
//        }
        ByteBuf encoded = ctx.alloc().buffer(37);
        
        int version = 0X10000001;
        encoded.writeInt(version);
        
        short cmd = 1001;
        encoded.writeShort(cmd);
        
        // flags 无效 
        encoded.writeShort(1);
        
        String pluginName = "mqc";
        encoded.writeInt(pluginName.length());
        encoded.writeBytes(pluginName.getBytes());
        encoded.writeInt(3);
        
        String varName = "root";
        encoded.writeInt(varName.length());
        encoded.writeBytes(varName.getBytes());
        
        String var = "664111";
        encoded.writeInt(var.length());
        encoded.writeBytes(var.getBytes());
        
        ctx.writeAndFlush(new DatagramPacket(encoded, new InetSocketAddress("127.0.0.1", 7686))).sync();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
