package com.wenbo.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class DiscardClientHandler extends SimpleChannelInboundHandler<Object>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(msg instanceof String){
			System.out.println("client accept:"+msg);
		}else{
			ByteBuf buf = (ByteBuf)msg;
			byte[] bb = new byte[buf.readableBytes()];
			buf.readBytes(bb);
			System.out.println("client accept:"+new String(bb));
		}
		
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("channelActive");
		String msg = "hello word!\n";
		ByteBuf byteBuf = Unpooled.copiedBuffer(msg.getBytes());
		ctx.writeAndFlush(byteBuf);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		super.exceptionCaught(ctx, cause);
	}
	
	


}
