package com.wenbo.netty.udp;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class UDPClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
	
	public static long count = 0;
    @Override
    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
//        String response = msg.content().toString(CharsetUtil.UTF_8);
//        if (response.startsWith("QOTM: ")) {
//            System.out.println("Quote of the Moment: " + response.substring(6));
//            
//            ctx.close();
//        }
    	
    	//if (++count % 1000 == 0)
    	//if (++count > 9999997) 
    	count++;
    	System.out.println("rec count: " + count);
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
    	
//    	Thread.sleep(10);
//    	if (++count > 10)
    		//System.out.println("rec count: " + count);
    		
    		//msg.release();
        
        
    	
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
