#include <jni.h>
#include <string>
//导入android-log日志
#include <android/log.h>

//当前C++兼容C语言
extern "C"{
//avcodec:编解码(最重要的库)
#include "libavcodec/avcodec.h"
//avformat:封装格式处理
#include "libavformat/avformat.h"
//avutil:工具库(大部分库都需要这个库的支持)
#include "libavutil/imgutils.h"
//swscale:视频像素数据格式转换
#include "libswscale/swscale.h"

JNIEXPORT void JNICALL Java_com_tz_dream_ffmpeg_FFmpegTest_ffmpegTest
        (JNIEnv *, jobject);
JNIEXPORT void JNICALL Java_com_tz_dream_ffmpeg_FFmpegTest_ffmpegDecoder
        (JNIEnv *env, jobject jobj,jstring jInFilePath,jstring jOutFilePath);
}

//1、NDK音视频编解码：FFmpeg-测试配置
JNIEXPORT void JNICALL Java_com_tz_dream_ffmpeg_FFmpegTest_ffmpegTest(
        JNIEnv *env, jobject jobj) {
    //(char *)表示C语言字符串
    const char *configuration = avcodec_configuration();
    __android_log_print(ANDROID_LOG_INFO,"main","%s",configuration);
}


//2.NDK音视频编解码：FFmpeg-视频解码-视频像素数据(YUV420P)
JNIEXPORT void JNICALL Java_com_tz_dream_ffmpeg_FFmpegTest_ffmpegDecoder(
        JNIEnv *env, jobject jobj, jstring jinputFilePath, jstring joutputFilePath) {

    //将java->string类型->C字符串->char*
    const char* cinputFilePath = env->GetStringUTFChars(jinputFilePath,NULL);
    const char* coutputFilePath = env->GetStringUTFChars(joutputFilePath,NULL);

    //第一步：注册所有组件
    av_register_all();

    //第二步：打开视频输入文件
    //参数一：封装格式上下文->AVFormatContext->包含了视频信息(视频格式、大小等等...)
    AVFormatContext* avformat_context = avformat_alloc_context();
    //参数二：打开文件(入口文件)->url
    int avformat_open_result = avformat_open_input(&avformat_context,cinputFilePath,NULL,NULL);
    if (avformat_open_result != 0){
        //获取异常信息
        char* error_info;
        av_strerror(avformat_open_result, error_info, 1024);
        __android_log_print(ANDROID_LOG_INFO,"main","异常信息：%s",error_info);
        return;
    }


    //第三步：查找视频文件信息
    //参数一：封装格式上下文->AVFormatContext
    //参数二：配置
    //返回值：0>=返回OK，否则失败
    int avformat_find_stream_info_result = avformat_find_stream_info(avformat_context, NULL);
    if (avformat_find_stream_info_result < 0){
        //获取失败
        char* error_info;
        av_strerror(avformat_find_stream_info_result, error_info, 1024);
        __android_log_print(ANDROID_LOG_INFO,"main","异常信息：%s",error_info);
        return;
    }


    //第四步：查找解码器
    //第一点：获取当前解码器是属于什么类型解码器->找到了视频流
    //音频解码器、视频解码器、字幕解码器等等...
    //获取视频解码器流引用->指针
    int av_stream_index = -1;
    for (int i = 0; i < avformat_context->nb_streams; ++i) {
        //循环遍历每一流
        //视频流、音频流、字幕流等等...
        if (avformat_context->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO){
            //找到了
            av_stream_index = i;
            break;
        }
    }
    if (av_stream_index == -1){
        __android_log_print(ANDROID_LOG_INFO,"main","%s","没有找到视频流");
        return;
    }

    //第二点：根据视频流->查找到视频解码器上下文->视频压缩数据
    AVCodecContext* avcodec_context = avformat_context->streams[av_stream_index]->codec;

    //第三点：根据解码器上下文->获取解码器ID
    AVCodec* avcodec = avcodec_find_decoder(avcodec_context->codec_id);
    if (avcodec == NULL){
        __android_log_print(ANDROID_LOG_INFO,"main","%s","没有找到视频解码器");
        return;
    }

    //第五步：打开解码器
    int avcodec_open2_result = avcodec_open2(avcodec_context,avcodec,NULL);
    if (avcodec_open2_result != 0){
        char* error_info;
        av_strerror(avcodec_open2_result, error_info, 1024);
        __android_log_print(ANDROID_LOG_INFO,"main","异常信息：%s",error_info);
        return;
    }

    //输出视频信息
    //输出：文件格式
    __android_log_print(ANDROID_LOG_INFO,"main","文件格式：%s",avformat_context->iformat->name);
    //输出：解码器名称
    __android_log_print(ANDROID_LOG_INFO,"main","解码器名称：%s",avcodec->name);


    //第六步：循环读取视频帧，进行循环解码->输出YUV420P视频->格式：yuv格式

    //读取帧数据换成到哪里->缓存到packet里面
    AVPacket* av_packet = (AVPacket*)av_malloc(sizeof(AVPacket));

    //输入->环境一帧数据->缓冲区->类似于一张图
    AVFrame* av_frame_in = av_frame_alloc();
    //输出->帧数据->视频像素数据格式->yuv420p
    AVFrame* av_frame_out_yuv420p = av_frame_alloc();

    //解码的状态类型(0:表示解码完毕，非0:表示正在解码)
    int got_picture_ptr, av_decode_result, y_size, u_size, v_size, current_frame_index = 0;

    //准备一个视频像素数据格式上下文
    //参数一：输入帧数据宽
    //参数二：输入帧数据高
    //参数三：输入帧数据格式
    //参数四：输出帧数据宽
    //参数五：输出帧数据高
    //参数六：输出帧数据格式->AV_PIX_FMT_YUV420P
    //参数七：视频像素数据格式转换算法类型
    //参数八：字节对齐类型(C/C++里面)->提高读取效率
    SwsContext* sws_context = sws_getContext(avcodec_context->width,
                                             avcodec_context->height,
                                             avcodec_context->pix_fmt,
                                             avcodec_context->width,
                                             avcodec_context->height,
                                             AV_PIX_FMT_YUV420P,
                                             SWS_BICUBIC,NULL,NULL,NULL);


    //打开文件
    FILE* out_file_yuv = fopen(coutputFilePath,"rwb");
    if (out_file_yuv == NULL){
        __android_log_print(ANDROID_LOG_INFO,"main","文件不存在");
        return;
    }


    //>=0:说明有数据，继续读取
    //<0:说明读取完毕，结束
    while (av_read_frame(avformat_context,av_packet) >= 0){
        //解码什么类型流(视频流、音频流、字幕流等等...)
        if (av_packet->stream_index == av_stream_index){

            //扩展知识面(有更新)
            //解码一帧视频流数据
            //分析：avcodec_decode_video2函数
            //参数一：解码器上下文
            //参数二：一帧数据
            //参数三：got_picture_ptr->是否正在解码(0:表示解码完毕，非0:表示正在解码)
            //参数四：一帧压缩数据(对压缩数据进行解码操作)
            //返回值：av_decode_result == 0表示解码一帧数据成功，否则失败
            //av_decode_result = avcodec_decode_video2(avcodec_context,av_frame_in,&got_picture_ptr,av_packet);

            //新的API操作
            //发送一帧数据->接收一帧数据

            //发送一帧数据
            avcodec_send_packet(avcodec_context, av_packet);

            //接收一帧数据->解码一帧
            av_decode_result = avcodec_receive_frame(avcodec_context, av_frame_in);

            //解码出来的每一帧数据成功之后，将每一帧数据保存为YUV420格式文件类型(.yuv文件格式)
            if ( av_decode_result == 0 ){
                //sws_scale：作用将视频像素数据格式->yuv420p格式
                //输出.yuv文件->视频像素数据格式文件->输出到文件API
                //参数一：视频像素数据格式->上下文
                //参数二：输入数据
                //参数三：输入画面每一行的大小
                //参数四：输入画面每一行的要转码的开始位置
                //参数五：每一帧数据高
                //参数六：输出画面数据
                //参数七：输出画面每一行的大小
                sws_scale(sws_context,
                          (const uint8_t *const*)av_frame_in->data,
                          av_frame_in->linesize,
                          0,
                          avcodec_context->height,
                          av_frame_out_yuv420p->data,
                          av_frame_out_yuv420p->linesize);


                //一帧一帧写入文件->yuv420p->视频像素数据格式
                //第一点：分析yuv420p格式原理
                //写入文件：按照像素点位置来进行写入->将av_frame_out_yuv420p一帧数据一个个字节读取
                //普及一下YUV420格式(人对眼睛亮度敏感，对色度不敏感)
                //Y代表：亮度
                //UV代表：色度
                //第二点：分析yuv420规则->计算机图像原理（听老师讲解于原理->扩展知识面）->直播技术
                //yuv420规则一：Y结构表示一个像素点
                //yuv420规则二：四个Y对应一个U和一个V（也就是四个像素点，对应一个U和一个V）
                //第三点：分析Y和U、V大小计算原理
                // y = 宽 * 高
                // u = y / 4
                // v = y / 4
                y_size = avcodec_context->width * avcodec_context->height;
                u_size = y_size / 4;
                v_size = y_size / 4;


                //第四点：写入文件
                //写入->Y
                //av_frame_in->data[0]:表示Y
                fwrite(av_frame_in->data[0], 1, y_size, out_file_yuv);
                //写入->U
                //av_frame_in->data[1]:表示U
                fwrite(av_frame_in->data[1], 1, u_size, out_file_yuv);
                //写入->V
                //av_frame_in->data[2]:表示V
                fwrite(av_frame_in->data[2], 1, v_size, out_file_yuv);

                current_frame_index++;

                __android_log_print(ANDROID_LOG_INFO,"main","当前遍历第%d帧",current_frame_index);

            }

        }
    }


    //第七步：关闭解码组件->释放内存
    av_packet_free(&av_packet);
    //关闭流
    fclose(out_file_yuv);
    av_frame_free(&av_frame_in);
    av_frame_free(&av_frame_out_yuv420p);
    avcodec_close(avcodec_context);
    avformat_free_context(avformat_context);

}
