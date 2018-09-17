package com.dianping.pigeon.remoting.netty.invoker;

import com.dianping.pigeon.remoting.common.codec.CodecConfig;
import com.dianping.pigeon.remoting.common.codec.CodecConfigFactory;
import com.dianping.pigeon.remoting.netty.codec.CompressHandler;
import com.dianping.pigeon.remoting.netty.codec.Crc32Handler;
import com.dianping.pigeon.remoting.netty.codec.FrameDecoder;
import com.dianping.pigeon.remoting.netty.codec.FramePrepender;
import com.dianping.pigeon.remoting.netty.invoker.codec.InvokerDecoder;
import com.dianping.pigeon.remoting.netty.invoker.codec.InvokerEncoder;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import static org.jboss.netty.channel.Channels.pipeline;

public class NettyClientPipelineFactory implements ChannelPipelineFactory {

    private NettyClient client;

    private static CodecConfig codecConfig = CodecConfigFactory.createClientConfig();

    public NettyClientPipelineFactory(NettyClient client) {
        this.client = client;
    }

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();
        pipeline.addLast("framePrepender", new FramePrepender());
        pipeline.addLast("frameDecoder", new FrameDecoder());
        pipeline.addLast("crc32Handler", new Crc32Handler(codecConfig));
        pipeline.addLast("compressHandler", new CompressHandler(codecConfig));
        pipeline.addLast("invokerDecoder", new InvokerDecoder());
        pipeline.addLast("invokerEncoder", new InvokerEncoder());
        pipeline.addLast("clientHandler", new NettyClientHandler(this.client));
        return pipeline;
    }

}
